package com.vincent.android.cili.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.vincent.android.cili.R

const val indicatorTextSize = 15.0f
const val tabTextSize = 12.0f
val activeColor: Int = "#ff678f".toColorInt()
val normalColor: Int = "#666666".toColorInt()

val AliFontFamily = FontFamily(
    Font(R.font.ali_45r)
)

val CiliTypography = Typography(
    defaultFontFamily = AliFontFamily,
    h1 = TextStyle(
        fontFamily = AliFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    h2 = TextStyle(
        fontFamily = AliFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    h3 = TextStyle(
        fontFamily = AliFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    body1 = TextStyle(
        fontFamily = AliFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = AliFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = AliFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = AliFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = AliFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = AliFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = AliFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )
)

@Composable
fun CiliComposeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = CiliTypography,
        content = content
    )
} 