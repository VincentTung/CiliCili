package com.vincent.android.myled.ble

import android.bluetooth.BluetoothAdapter

object VTBluetoothUtil {

    fun isEnable(): Boolean {
        var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter?.isEnabled == true) {
            return true
        } else {
            return false
        }
    }
}