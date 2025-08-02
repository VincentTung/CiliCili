package com.vincent.android.cili.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vincent.android.cili.R
import com.vincent.android.cili.ui.theme.CiliComposeTheme

class AboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CiliComposeTheme {
                AboutScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val versionName = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: Exception) {
        e.printStackTrace()
        "1.0.0"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(  color = Color(LocalContext.current.getColor(R.color.theme_setting_bg)),)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "返回",
                tint = Color(LocalContext.current.getColor(R.color.theme_item_text)),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .size(30.dp)
                    .clickable { onBack() }
            )
            Text(
                text = "关于",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color  = Color(LocalContext.current.getColor(R.color.theme_item_text)),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box(modifier = Modifier.size(height = 100.dp, width = 100.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "App Icon",
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CiliCili",
                fontSize = 16.sp,
                color = Color(0xFF666666)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "版本号: $versionName",
                fontSize = 14.sp,
                color = Color(LocalContext.current.getColor(R.color.theme_item_text)),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "作者: VincentTung",
                fontSize = 14.sp,
                color = Color(LocalContext.current.getColor(R.color.theme_item_text)),
            )
        }
    }
} 