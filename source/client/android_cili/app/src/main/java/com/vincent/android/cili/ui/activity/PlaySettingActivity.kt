package com.vincent.android.cili.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vincent.android.cili.R
import kotlinx.coroutines.launch
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.vincent.android.cili.ui.theme.CiliComposeTheme

class PlaySettingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CiliComposeTheme {
                PlaySettingScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun PlaySettingScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var autoPlay by remember { mutableStateOf(false) }
    var autoFullscreen by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }

    // 只在首次进入时读取 DataStore
    LaunchedEffect(Unit) {
        if (!initialized) {
            autoPlay = PlaySettingDataStore.getAutoPlay(context)
            autoFullscreen = PlaySettingDataStore.getAutoFullscreen(context)
            initialized = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(LocalContext.current.getColor(R.color.theme_setting_bg)))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                tint = Color(LocalContext.current.getColor(R.color.theme_item_text)),
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "返回",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .size(30.dp)
                    .clickable { onBack() }
            )
            Text(
                text = "播放设置",
                fontSize = 20.sp,
                color = Color(LocalContext.current.getColor(R.color.theme_item_text)),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        SettingSwitchItem(
            title = "详情页直接播放",
            desc = "进入视频详情页后自动播放视频",
            checked = autoPlay,
            onCheckedChange = {
                autoPlay = it
                scope.launch { PlaySettingDataStore.setAutoPlay(context, it) }
            }
        )
        SettingSwitchItem(
            title = "详情页自动全屏",
            desc = "进入视频详情页自动全屏播放视频",
            checked = autoFullscreen,
            onCheckedChange = {
                autoFullscreen = it
                scope.launch { PlaySettingDataStore.setAutoFullscreen(context, it) }
            }
        )

        SettingSwitchItem(
            title = "详情页自动连接弹幕显示器",
            desc = "进入视频详情页详情页自动连接弹幕显示器",
            checked = autoFullscreen,
            onCheckedChange = {
                autoFullscreen = it
                scope.launch { PlaySettingDataStore.setAutoConnectBarrageLED(context, it) }
            }
        )
    }
}

@Composable
fun SettingSwitchItem(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color(LocalContext.current.getColor(R.color.theme_item_text)),
            )
            Text(text = desc, fontSize = 14.sp, color = Color.Gray)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}


private val Context.playSettingDataStore by preferencesDataStore(name = "play_settings")

object PlaySettingDataStore {
    private val AUTO_PLAY_KEY = booleanPreferencesKey("auto_play")
    private val AUTO_FULLSCREEN_KEY = booleanPreferencesKey("auto_fullscreen")
    private val AUTO_CONNECT_LED_KEY = booleanPreferencesKey("auto_connect_led")
    suspend fun setAutoPlay(context: Context, value: Boolean) {
        context.playSettingDataStore.edit { it[AUTO_PLAY_KEY] = value }
    }

    suspend fun getAutoPlay(context: Context): Boolean {
        return context.playSettingDataStore.data.map { it[AUTO_PLAY_KEY] ?: false }.first()
    }

    suspend fun setAutoFullscreen(context: Context, value: Boolean) {
        context.playSettingDataStore.edit { it[AUTO_FULLSCREEN_KEY] = value }
    }

    suspend fun getAutoFullscreen(context: Context): Boolean {
        return context.playSettingDataStore.data.map { it[AUTO_FULLSCREEN_KEY] ?: false }.first()
    }

    suspend fun setAutoConnectBarrageLED(context: Context, value: Boolean) {

        context.playSettingDataStore.edit { it[AUTO_CONNECT_LED_KEY] = value }
    }

    suspend fun getAutoConnectBarrageLED(context: Context): Boolean {
        return context.playSettingDataStore.data.map { it[AUTO_CONNECT_LED_KEY] ?: false }.first()
    }
} 