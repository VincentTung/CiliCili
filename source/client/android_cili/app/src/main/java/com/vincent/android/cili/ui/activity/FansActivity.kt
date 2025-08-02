package com.vincent.android.cili.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.vincent.android.cili.entity.Owner
import com.vincent.android.cili.view.NoDataView
import com.vincent.android.cili.view.TopBar
import com.vincent.android.cili.view.UpWidget
import com.vincent.android.cili.view.VideoDivider
import com.vincent.android.cili.viewmodel.FansViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FansActivity : BaseActivity() {
    private val fansViewModel: FansViewModel by viewModels()

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, FansActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            fansViewModel.fansList.value.let {
                Column {
                    TopBar(title = "粉丝列表", onClick = {
                        finish()
                    })
                    FansList(list = it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fansViewModel.getFansList()
    }
}


@Composable
fun FansList(list: List<Owner>) {
    if (list.isEmpty()) {
        NoDataView(text = "暂无粉丝")
    } else {
        LazyColumn {
            items(list.size) { index ->
                FansItem(list[index])
                VideoDivider()
            }
        }
    }
}

@Composable
fun FansItem(fan: Owner) {

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(all = 15.dp)) {

        Image(
            painter = rememberAsyncImagePainter(fan.face),
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(15.dp))
        UpWidget()

        Spacer(modifier = Modifier.width(15.dp))
        Text(text = fan.name, fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "粉丝:${fan.fans}", fontSize = 12.sp, color = Color.Gray)

    }
}