package com.vincent.android.cili.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.vincent.android.cili.entity.VideoEntity
import com.vincent.android.cili.use_case.get_record.RecordType
import com.vincent.android.cili.view.NoDataView
import com.vincent.android.cili.view.TopBar
import com.vincent.android.cili.view.UpWidget
import com.vincent.android.cili.view.VideoDivider
import com.vincent.android.cili.viewmodel.GetRecordViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect

@AndroidEntryPoint
class GetRecordActivity : BaseActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context, recordType: RecordType) {
            val starter = Intent(context, GetRecordActivity::class.java)
                .putExtra("type", recordType)
            context.startActivity(starter)
        }
    }

    @Inject
    lateinit var getRecordViewModel: GetRecordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = intent.getSerializableExtra("type") as RecordType

        getRecordViewModel.getRecord(type)
        setContent {
            var refreshing by remember { mutableStateOf(false) }
            val recordList = getRecordViewModel.recordList.value
            // 当recordList变化时自动关闭刷新动画
            LaunchedEffect(recordList) {
                refreshing = false
            }
            Column {
                TopBar(title = getTopTitle(type), onClick = {
                    finish()
                })
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = refreshing),
                    onRefresh = {
                        refreshing = true
                        getRecordViewModel.getRecord(type)
                    }
                ) {
                    refreshing = false
                    if (recordList.isEmpty()) {
                        NoDataView(text = "暂无数据")
                    } else {
                        LazyColumn {
                            items(recordList.size) { index ->
                                val video = recordList[index]
                                RecordItem(item = video) {
                                    VideoDetailActivity.start(this@GetRecordActivity, video)
                                }
                                VideoDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun getTopTitle(type: RecordType): String {

        return when (type) {
            RecordType.COIN -> "投币列表"
            RecordType.LIKE -> "点赞列表"
            RecordType.VIEW -> "浏览列表"
            else -> ""
        }
    }

}

@Composable
fun RecordItem(item: VideoEntity, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(all = 15.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(item.cover),
            contentDescription = null,
            modifier = Modifier
                .size(width = 160.dp, height = 100.dp)
                .align(Alignment.CenterVertically),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column() {
            Text(text = item.title ?: "")
            Spacer(modifier = Modifier.height(15.dp))

            Row {

                UpWidget()
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = item.name ?: "", color = Color.Gray, fontSize = 10.sp, maxLines = 2)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Text(text = "观看:${item.view}", color = Color.Gray, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "跟帖:${item.reply}", color = Color.Gray, fontSize = 10.sp)
            }
        }
    }

}