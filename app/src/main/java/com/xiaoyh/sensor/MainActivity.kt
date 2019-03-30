package com.xiaoyh.sensor

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.*
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.xiaoyh.sensor.listener.FabListener
import com.xiaoyh.sensor.listener.MyListViewListener
import com.xiaoyh.sensor.listener.MySensorListener
import com.xiaoyh.sensor.util.LogUtil
import com.xiaoyh.sensor.util.PermissionUtil
import com.xiaoyh.sensor.util.ToastUtil
import kotlinx.android.synthetic.main.activity_bluetooth.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        // 待用传感器数组：加速度计、磁力计、陀螺仪
        val sensors = intArrayOf(Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_GYROSCOPE)
        val accs = FloatArray(3)
        val mags = FloatArray(3)
        val gyrs = FloatArray(3)
        val mBlueList = mutableListOf<BluetoothDevice>()
        lateinit var ba: BluetoothAdapter
        lateinit var sm: SensorManager

        lateinit var accLineChart: MyLineChart
        lateinit var magLineChart: MyLineChart
        lateinit var gyrLineChart: MyLineChart
    }

    private lateinit var lva: ListViewAdapter               // listView适配器
    private lateinit var fabListener: FabListener           // FAB点击监听器
    private var connectAsyncTask: ConnectAsyncTask? = null  // 蓝牙连接AsyncTask
    private var socket: BluetoothSocket? = null             // 蓝牙Socket

    // 蓝牙广播接受者（待优化）
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (!mBlueList.contains(device)) {
                        mBlueList.add(device)
                        lva.notifyDataSetChanged()
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> ToastUtil.toast("搜索结束")
            }
            LogUtil.d(mBlueList.size.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // toolbar 与 drawerLayout 建立联系
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        // 获取传感器管理者
        sm = getSystemService(SENSOR_SERVICE) as SensorManager

        // 设置监听器（fab 点击监听器内置了 Sensor 监听器）
        blueListView.onItemClickListener = MyListViewListener(this, socket, connectAsyncTask)
        search.setOnClickListener(this)
        stop.setOnClickListener(this)
        disconnect.setOnClickListener(this)
        fabListener = FabListener(fab, socket, MySensorListener())
        fab.setOnClickListener(fabListener)

        // 折线图初始化
        accLineChart = MyLineChart(chart_acc, "加速度计")
        magLineChart = MyLineChart(chart_mag, "磁力计")
        gyrLineChart = MyLineChart(chart_gyr, "陀螺仪")

        // 蓝牙部分初始化
        bluetoothInit()

        // 权限申请
        PermissionUtil.getPermission(this)
    }

    private fun bluetoothInit() {
        // 获取蓝牙适配器
        ba = BluetoothAdapter.getDefaultAdapter()
        // 若蓝牙没打开则打开它
        if (!ba.isEnabled) {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0)
        }
        // 为list添加以连接设备
        if (ba.bondedDevices.size > 0 && !mBlueList.containsAll(ba.bondedDevices)) {
            mBlueList.addAll(ba.bondedDevices)
        }

        // 注册listView adapter
        lva = ListViewAdapter(mBlueList, this)
        blueListView.adapter = lva
    }

    // 退到桌面后再切换回APP，不会执行 onCreate 方法，但是蓝牙广播已经注销
    // 所以必须在 onStart 方法中再次注册蓝牙广播
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(bluetoothReceiver, filter)
    }

    override fun onPause() {
        if (fabListener.running) fab.performClick() // 关闭传感器即蓝牙的发送
        connectAsyncTask?.listener = null           // 将连接AsyncTask的监听器置空（待优化）
        disconnect.performClick()                   // 断开蓝牙
        try {
            // 处理广播未注册上的异常
            unregisterReceiver(bluetoothReceiver)   // 注销广播，防止重复注册
        } catch (e: IllegalArgumentException) {
            ToastUtil.toast("请打开蓝牙后重启")
        }
        super.onPause()
    }

    override fun onClick(v: View?) {
        when (v) {
            search -> {
                if (!ba.isDiscovering) {
                    ba.startDiscovery()
                    ToastUtil.toast("开始搜索")
                } else {
                    ToastUtil.toast("正在搜索ing")
                }
            }
            stop -> if (ba.isDiscovering) ba.cancelDiscovery()
            disconnect -> {
                socket?.let {
                    if (it.isConnected) {
                        it.close()
                        address.text = ""
                        ToastUtil.toast("连接已断开")
                    } else {
                        ToastUtil.toast("当前无连接")
                    }
                }
            }
        }
    }

    //在界面中若有EditText则按退出键会直接退出应用而不是退出菜单
    //所以对退出键的onBackPressed方法进行重写
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(left_drawer)) {
            drawer_layout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
}
