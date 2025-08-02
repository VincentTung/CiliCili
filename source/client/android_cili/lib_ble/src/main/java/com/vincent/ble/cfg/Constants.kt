package com.vincent.ble.cfg


const val LOG_TAG = "MyLED"

/**
 * 打开蓝牙 request code
 */
const val REQUEST_ENABLE_BLUETOOTH = 0x16

/**
 * 蓝牙设备地址
 */
const val LED_DEVICE_ADDRESS = "88:13:BF:00:78:42"

/**
 * 蓝牙设备名称
 */
const val LED_DEVICE_NAME = "MyLED"

/**
 * 服务UUID
 */
const val LED_SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"

/**
 * 特征UUID
 */

//文本
const val LED_CHARACTERISTIC_TEXT_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"
const val LED_CHARACTERISTIC_TEXT_SCROLL_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a3"
//GIF
const val LED_CHARACTERISTIC_GIF_UUID  ="beb5483e-36e1-4688-b7f5-ea07361b26b1"
//图片
const val LED_CHARACTERISTIC_DRAW_IMAGE_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26b2"
//画图(黑白)
const val  LED_CHARACTERISTIC_DRAW_NORMAL_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a7"
//画图(彩色)
const val  LED_CHARACTERISTIC_DRAW_COLORFUL_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a6"
//亮度
const val LED_CHARACTERISTIC_BRIGHTNESS_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a9"
//单像素
const val  LED_CHARACTERISTIC_FILL_PIXEL_UUID ="beb5483e-36e1-4688-b7f5-ea07361b26a5"
//全屏
const val  LED_CHARACTERISTIC_FILL_SCREEN_UUID ="beb5483e-36e1-4688-b7f5-ea07361b26a4"

//LED最小亮度为5，小于5就看不见了
const val LED_MINIMUM_BRIGHTNESS = 0
//LED默认亮度
const val LED_DEFAULT_BRIGHTNESS = 20
//LED默认显示文本
const val LED_DEFAULT_DISPLAY_TEXT = "已连接"
//LED默认显示字体大小
const val LED_DEFAULT_FONT_SIZE  = 1

val TEST_NI_DATAS: IntArray = intArrayOf(
    0xF800, 0xF800, 0xF800, 0xF800, 0xF800, 0xF800, 0xF800, 0xF800, 0xF800, 0xF800, 0xF800, 0xF800, 0xF800, 0xF800/*"C:\Users\Administrator\Desktop\1111.bmp",0*/

)
