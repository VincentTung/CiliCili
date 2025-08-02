package com.vincent.android.cili.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vincent.android.cili.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
// DataStore 工具对象
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.saveable.rememberSaveable
import com.vincent.android.cili.ui.theme.CiliComposeTheme

enum class ThemeMode(val title: String, val desc: String) {
    LIGHT("浅色主题", "使用浅色主题"),
    DARK("深色主题", "使用深色主题"),
    SYSTEM("跟随系统", "跟随系统主题设置")
}

class ThemeSettingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CiliComposeTheme {
                ThemeSettingScreen(
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun ThemeSettingScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val themeMode by produceState(initialValue = ThemeMode.SYSTEM) {
        value = ThemeDataStore.getThemeMode(context)
    }
    var selectedMode by rememberSaveable(themeMode) { mutableStateOf(themeMode) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(LocalContext.current.getColor(R.color.theme_setting_bg)),)
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
                // tint = Color.Black, // 移除tint，自动适配主题
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .size(30.dp)
                    .clickable { onBack() }
            )
            Text(
                text = "主题设置",
                fontSize = 16.sp,
                color = Color(LocalContext.current.getColor(R.color.theme_item_text)),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        ThemeMode.entries.forEach { mode ->
            ThemeModeItem(
                mode = mode,
                selected = selectedMode == mode,
                onClick = {
                    if (selectedMode != mode) {
                        selectedMode = mode
                        scope.launch {
                            ThemeDataStore.saveThemeMode(context, mode)
                            applyTheme(mode)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ThemeModeItem(mode: ThemeMode, selected: Boolean, onClick: () -> Unit) {
    val iconRes = when (mode) {
        ThemeMode.LIGHT -> R.drawable.ic_light_mode
        ThemeMode.DARK -> R.drawable.ic_dark_mode
        ThemeMode.SYSTEM -> R.drawable.ic_system_mode
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = mode.title,
                fontSize = 16.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = Color(LocalContext.current.getColor(R.color.theme_item_text)),
            )
            Text(
                text = mode.desc,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

fun applyTheme(mode: ThemeMode) {
    when (mode) {
        ThemeMode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ThemeMode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        ThemeMode.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}



private val Context.dataStore by preferencesDataStore(name = "theme_settings")
object ThemeDataStore {
    private val THEME_KEY = stringPreferencesKey("theme_mode")
    suspend fun saveThemeMode(context: Context, mode: ThemeMode) {
        context.dataStore.edit { it[THEME_KEY] = mode.name }
    }
    suspend fun getThemeMode(context: Context): ThemeMode {
        val value = context.dataStore.data.map { it[THEME_KEY] ?: ThemeMode.SYSTEM.name }.first()
        return ThemeMode.valueOf(value)
    }
} 