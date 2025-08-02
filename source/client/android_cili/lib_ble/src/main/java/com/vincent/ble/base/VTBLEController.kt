package com.vincent.ble.base

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.util.Log
import com.vincent.ble.cfg.LED_DEVICE_NAME
import kotlin.math.min

@SuppressLint("MissingPermission")
class VTBLEController(
    private val mContext: Context,
    private val mDeviceAddress: String,
    private val mDeviceServiceID: String,
    private val mDeviceCharacteristicID: String
) {
    private var mGatt: BluetoothGatt? = null
    private var mCharacteristic: BluetoothGattCharacteristic? = null
    private var mBLEVTBLECallback: VTBLECallback = DefaultBLECallback()

    /**
     * 扫描设备
     */
    fun scan(callback: VTBLECallback) {
        mBLEVTBLECallback = callback
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        val scanCallback = object : ScanCallback() {

            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                Log.d("ble", "scan device:${result?.device?.name}")
                if (result?.device?.name == LED_DEVICE_NAME) {
                    bluetoothLeScanner?.stopScan(this)
                    result?.device?.apply {
                        connectDevice(this)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                //showToast(R.string.find_no_device)
                Log.d("ble", "onScanFailed")
                callback.onScanFailed();
            }
        }
        bluetoothLeScanner?.startScan(scanCallback)
    }

    /**
     * 连接设备
     */
    private fun connectDevice(device: BluetoothDevice) {
        Log.d("ble", "connectDevice: ${device.name}");

        device.connectGatt(mContext, true, object : BluetoothGattCallback() {

            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                Log.d("ble", "onConnectionStateChange $newState status:$status");
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        mBLEVTBLECallback.onConnected(device.name, device.address)
                        gatt?.discoverServices()

                    }

                    BluetoothProfile.STATE_DISCONNECTED -> mBLEVTBLECallback.onDisConnected();
                    BluetoothProfile.STATE_CONNECTING -> mBLEVTBLECallback.onConnecting();
                }

            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)

                gatt?.services?.apply {
                    this.forEach { service ->

                        Log.d("ble", "onServicesDiscovered success___${service.uuid.toString()}")
                        if (service.uuid.toString() == mDeviceServiceID) {
                            checkCharacteristic(gatt, service)
                            return@forEach
                        }
                    }
                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                val value = characteristic?.value?.toString(Charsets.UTF_8)
            }
        }, BluetoothDevice.TRANSPORT_LE)
    }

    /**
     *  检查 特征值是否存在
     */
    private fun checkCharacteristic(
        gatt: BluetoothGatt,
        service: BluetoothGattService?
    ) {

        service?.characteristics?.forEach { characteristic ->

            val characUUID = characteristic.uuid.toString();
            Log.d("ble", "checkCharacteristic find success___${characteristic.uuid}")
            if (characUUID == mDeviceCharacteristicID) {
                Log.d("ble", "checkCharacteristic book");
                mGatt = gatt;
                mCharacteristic = characteristic;
                gatt.setCharacteristicNotification(characteristic, true)
                mBLEVTBLECallback.onCheckCharacteristicSuccess();
                return@forEach
            }

        }


    }

    /**
     * 发送文本数据
     */
    fun sendText(serviceUUID: String, characteristicUUID: String, text: String) {

        mGatt?.let {
            writeDataToCharacteristicText(
                it,
                serviceUUID,
                characteristicUUID,
                text
            )
        }
    }

    /**
     * 发送字节数据
     */
    fun sendBytes(serviceUUID: String, characteristicUUID: String, bytes: ByteArray) {

        mGatt?.let {
            writeDataToCharacteristicBytes(
                it,
                serviceUUID,
                characteristicUUID,
                bytes
            )
        }
    }


    private fun sendLargeData(
        bluetoothGatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        data: ByteArray
    ) {
        val chunkSize = 20 // 默认情况下，每块20字节
        var offset = 0
        //while (offset < data.size) {
        val length = min(chunkSize.toDouble(), (data.size - offset).toDouble()).toInt()
        val chunk = ByteArray(length)
        System.arraycopy(data, offset, chunk, 0, length)
        characteristic.setValue(chunk)
        bluetoothGatt.writeCharacteristic(characteristic)
        offset += length
        Log.d("ble", "sendLargeData:$offset")
        //}
    }

    private fun writeDataToCharacteristicText(
        gatt: BluetoothGatt,
        serviceUUID: String,
        characteristicUUID: String,
        text: String
    ) {

        writeDataToCharacteristicBytes(gatt, serviceUUID, characteristicUUID, text.toByteArray())
    }


    private fun writeDataToCharacteristicBytes(
        gatt: BluetoothGatt,
        serviceUUID: String,
        characteristicUUID: String,
        data: ByteArray
    ) {

        gatt.services.first {
            it.uuid.toString() == serviceUUID
        }.characteristics.first {
            it.uuid.toString() == characteristicUUID
        }.apply {

            setValue(data)
        }.let {
            val isSuccess = gatt.writeCharacteristic(it)
            mBLEVTBLECallback.writeDataCallback(isSuccess)
        }
    }
}


fun logd(text: String) {
    Log.d("ble", text)

}



