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
import com.xiaoyh.sensor.listener.MyListViewListener
import com.xiaoyh.sensor.listener.MySensorListener
import com.xiaoyh.sensor.listener.MySwitchListener
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

        lateinit var acclineChart: MyLineChart
    }

    private lateinit var lva: ListViewAdapter

    private var connectAsyncTask: ConnectAsyncTask? = null
    private var socket: BluetoothSocket? = null

    // 蓝牙广播接受者
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

        // 设置监听器（Switch 监听器内置了 Sensor 监听器）
        sensor_swicth.setOnCheckedChangeListener(MySwitchListener(socket, MySensorListener(txv1, txv2, txv3)))
        blueListView.onItemClickListener = MyListViewListener(this, socket, connectAsyncTask)

        acclineChart = MyLineChart(chart, "加速度计")

        // 按钮注册
        search.setOnClickListener(this)
        stop.setOnClickListener(this)
        disconnect.setOnClickListener(this)

        // 蓝牙部分初始化
        bluetoothInit()

        // 注册广播（蓝牙扫描设备，扫描完成）
        broadcastInit()

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

    private fun broadcastInit() {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(bluetoothReceiver, filter)
    }

    override fun onPause() {
        sensor_swicth.isChecked = false
        connectAsyncTask?.listener = null
        try {
            // 处理广播未注册上的异常
            unregisterReceiver(bluetoothReceiver)
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
