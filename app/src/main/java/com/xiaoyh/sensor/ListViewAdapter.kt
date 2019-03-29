package com.xiaoyh.sensor

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ListViewAdapter(private val list: List<BluetoothDevice>, private val context: Context) : BaseAdapter() {

    override fun getItem(position: Int): BluetoothDevice {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = View.inflate(context, R.layout.list_item, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val item: BluetoothDevice = getItem(position)
        viewHolder.deviceName.text = when (item.name.isNullOrEmpty()) {
            true -> "未知设备"
            false -> item.name
        }
        viewHolder.deviceAddress.text = item.address

        return view
    }

    private class ViewHolder(viewItem: View) {
        val deviceName: TextView = viewItem.findViewById(R.id.deviceName)
        val deviceAddress: TextView = viewItem.findViewById(R.id.deviceAddress)
    }
}