package com.vincent.ble.base

interface VTBLECallback {

    fun onCheckCharacteristicSuccess();
    fun onDisConnected()
    fun onConnecting()
    fun onScanFailed()
    fun onConnected(name: String?,address:String?)
    fun writeDataCallback(isSuccess: Boolean)
}

