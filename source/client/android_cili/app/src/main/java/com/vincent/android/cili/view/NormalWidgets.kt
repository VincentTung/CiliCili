package com.vincent.android.cili.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider

import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vincent.android.cili.R

@Composable
fun TopBar(title: String, onClick: () -> Unit = {}) {


    Row (verticalAlignment = Alignment.CenterVertically,modifier = Modifier.height(50.dp)){

        Modifier.width(15.dp)
        IconButton(onClick = { onClick.invoke() }) {
            Image(
                painter = painterResource(id = R.drawable.left_arrow),
                contentDescription = null,
                modifier = Modifier.padding(end = 10.dp)
            )
        }

        Modifier.width(15.dp)
        Text(text = title)


    }


}

@Composable
fun UpWidget(){
    Text(text = "UP",
        Modifier
            .border(width = 0.5.dp, color = Color.Gray)
            .padding(all = 2.dp),fontSize = 8.sp,color = Color.Gray)
}
@Composable
fun VideoDivider(){
    Divider(color = Color.Gray, thickness = 0.5.dp)
}