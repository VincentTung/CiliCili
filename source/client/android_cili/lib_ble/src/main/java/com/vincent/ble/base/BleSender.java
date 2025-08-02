package com.vincent.ble.base;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BleSender {
    private final Queue<byte[]> sendQueue = new ConcurrentLinkedQueue<>();
    private final BluetoothGatt bluetoothGatt;
    private final BluetoothGattCharacteristic characteristic;
    private boolean isSending = false;

    public BleSender(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic) {
        this.bluetoothGatt = bluetoothGatt;
        this.characteristic = characteristic;
    }

    // 添加数据到发送队列
    public void enqueueData(byte[] data) {
        sendQueue.offer(data);
        if (!isSending) {
            sendNext();
        }
    }

    // 发送下一个数据包
    private void sendNext() {
        if (sendQueue.isEmpty()) {
            isSending = false;
            return; // 队列为空，结束发送
        }

        isSending = true;
        byte[] dataToSend = sendQueue.poll();
        sendData(dataToSend);
    }

    // 发送数据
    private void sendData(byte[] data) {
        characteristic.setValue(data);
        boolean success = bluetoothGatt.writeCharacteristic(characteristic);

        if (success) {
            // 等待确认（假设有一个方法可以检查确认）
            waitForAck();
        } else {
            // 发送失败，处理逻辑（例如重试）
            isSending = false;
        }
    }

    // 等待确认
    private void waitForAck() {
        // 这里可以使用 Handler 或其他方式来等待确认
        // 一旦确认收到，调用 sendNext() 来发送下一个数据包
        // 此处省略确认的具体实现
        sendNext();
    }

    // 处理接收的确认消息
    public void onAckReceived() {
        sendNext();
    }
}