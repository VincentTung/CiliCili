package com.vincent.ble.led

import android.annotation.SuppressLint
import android.content.Context
import com.vincent.ble.base.VTBLECallback
import com.vincent.ble.base.VTBLEController
import com.vincent.ble.cfg.LED_CHARACTERISTIC_BRIGHTNESS_UUID
import com.vincent.ble.cfg.LED_CHARACTERISTIC_DRAW_NORMAL_UUID
import com.vincent.ble.cfg.LED_CHARACTERISTIC_FILL_PIXEL_UUID
import com.vincent.ble.cfg.LED_CHARACTERISTIC_FILL_SCREEN_UUID
import com.vincent.ble.cfg.LED_CHARACTERISTIC_GIF_UUID
import com.vincent.ble.cfg.LED_CHARACTERISTIC_TEXT_SCROLL_UUID
import com.vincent.ble.cfg.LED_CHARACTERISTIC_TEXT_UUID
import com.vincent.ble.cfg.LED_DEFAULT_BRIGHTNESS
import com.vincent.ble.cfg.LED_DEFAULT_DISPLAY_TEXT
import com.vincent.ble.cfg.LED_DEVICE_ADDRESS
import com.vincent.ble.cfg.LED_MINIMUM_BRIGHTNESS
import com.vincent.ble.cfg.LED_SERVICE_UUID
import com.vincent.ble.cfg.LED_CHARACTERISTIC_DRAW_IMAGE_UUID
import com.vincent.ble.cfg.LED_CHARACTERISTIC_DRAW_COLORFUL_UUID

class LEDController private constructor() {


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LEDController? = null

        fun getInstance(): LEDController {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LEDController().also { INSTANCE = it }
            }
        }
    }

    private lateinit var mContext: Context
    private lateinit var mBLEController: VTBLEController
    fun initBLE(context: Context) {
        mContext = context
        mBLEController = VTBLEController(
            mContext, LED_DEVICE_ADDRESS, LED_SERVICE_UUID,
            LED_CHARACTERISTIC_TEXT_UUID
        )
    }


    /**
     * LED屏幕连接
     */
    fun connect(callback: VTBLECallback) {
        mBLEController.scan(callback)
    }


    /**
     * 显示静态文字
     */
    fun drawStaticText(text: String,fontSize:Int = 1) {
        mBLEController.sendText(
            LED_SERVICE_UUID,
            LED_CHARACTERISTIC_TEXT_UUID,
            "$fontSize,$text"
        )
    }

    /**
     * 显示滚动文字
     */
    fun drawScrollingText(text: String,fontSize:Int = 1,speed:Int = 1) {
        mBLEController.sendText(
            LED_SERVICE_UUID,
            LED_CHARACTERISTIC_TEXT_SCROLL_UUID,
            "$fontSize,$speed,$text"
        )
    }

    /**
     * 显示单色图绘制图
     */
    fun drawNormalCanvas(byteArray: ByteArray) {
        mBLEController.sendBytes(
            LED_SERVICE_UUID,
            LED_CHARACTERISTIC_DRAW_NORMAL_UUID,
            byteArray
        )


    }


    /**
     *  设置亮度
     */
    fun setBrightness(value: Int) {
        val brightness: Int = (255 / 100.0f * value).toInt()
        mBLEController.sendText(
            LED_SERVICE_UUID,
            LED_CHARACTERISTIC_BRIGHTNESS_UUID,
            (if (brightness == 0) LED_MINIMUM_BRIGHTNESS else brightness).toString()
        )


    }

    /**
     * 绘制一个像素
     */
    fun draw1Pixel(x: Int, y: Int, color: Int) {
        val text = "$x,$y,$color";
        mBLEController.sendText(
            LED_SERVICE_UUID,
            LED_CHARACTERISTIC_FILL_PIXEL_UUID,
            text
        )

    }

    /**
     *  填充屏幕
     *   isClear为true的时候黑屏，反之白屏
     */
    fun fillScreen(isClear: Boolean) {
        mBLEController.sendText(
            LED_SERVICE_UUID,
            LED_CHARACTERISTIC_FILL_SCREEN_UUID,
            (if (isClear) "1" else "0").toString()
        )
    }

    /**
     *  绘制图片
     */
    fun drawImage(imagePath: String) {
        // 实现基本的图片路径发送功能
        mBLEController.sendText(
            LED_SERVICE_UUID,
            LED_CHARACTERISTIC_DRAW_IMAGE_UUID,
            imagePath
        )
    }
    /**
     *  绘制Gif动画
     */
    fun drawGif(gifPath: String) {
        mBLEController.sendText(
            LED_SERVICE_UUID,
            LED_CHARACTERISTIC_GIF_UUID,
            gifPath
        )

    }

    /**
     * 显示彩色绘制图
     */
    fun drawColorfulCanvas(data: IntArray) {
        // 将 IntArray 转换为字节数组
        val value = ByteArray(data.size * 2)
        for (i in data.indices) {
            value[i * 2] = ((data[i] shr 8) and 0xFF).toByte()
            value[i * 2 + 1] = (data[i] and 0xFF).toByte()
        }
        
        // 发送彩色数据
        mBLEController.sendBytes(
            LED_SERVICE_UUID,
            LED_CHARACTERISTIC_DRAW_COLORFUL_UUID,
            value
        )
    }

    fun drawDefault() {
        drawStaticText(LED_DEFAULT_DISPLAY_TEXT)
        setBrightness(LED_DEFAULT_BRIGHTNESS)
    }
}