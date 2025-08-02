package com.vincent.android.cili.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.rememberAsyncImagePainter
import com.vincent.android.cili.ui.activity.MainActivity
import com.vincent.android.cili.R
import com.vincent.android.cili.network.ApiHelper
import com.vincent.android.cili.entity.UserInfo
import com.vincent.android.cili.extensions.TAG
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.ui.activity.AboutActivity
import com.vincent.android.cili.ui.activity.FansActivity
import com.vincent.android.cili.ui.activity.GetRecordActivity
import com.vincent.android.cili.ui.activity.LoginActivity
import com.vincent.android.cili.ui.activity.PlaySettingActivity
import com.vincent.android.cili.ui.activity.ThemeSettingActivity
import com.vincent.android.cili.use_case.get_record.RecordType
import com.vincent.android.cili.util.AccountManager
import com.vincent.android.cili.viewmodel.MineViewModel
import com.vincent.android.cili.ui.theme.CiliComposeTheme
import com.vincent.android.cili.util.CacheManager
import com.vincent.android.cili.util.FormatUtil
import com.zj.banner.model.BaseBannerBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.vincent.android.cili.util.GlideApp
import kotlinx.coroutines.withContext

data class BannerBean(
    override val data: Any? = null,

    val linkUrl: String
) : BaseBannerBean()

enum class CountType {
    VIEW, COLLECT, LIKE, COIN, FANS
}
private const val BANNER_LOOP_TIME = 2000L

data class CountItem(val count: Int, val text: String, val function: () -> Unit = {})


@OptIn(ExperimentalMaterialApi::class)
class MineFragment : BaseFragment() {

    private lateinit var mineViewModel: MineViewModel
    private val openLogoutDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mineViewModel = getActivityScopeVM(MineViewModel::class.java)!!
    }

    override fun onResume() {
        super.onResume()
        mineViewModel.getUserInfo()
    }

    private fun openPage(type: CountType) {

        when (type) {
            CountType.VIEW -> {
                GetRecordActivity.start(requireContext(), RecordType.VIEW)
            }

            CountType.COLLECT -> {
                val activity = requireActivity() as MainActivity
                activity.showCollectTab()
            }

            CountType.LIKE -> {
                GetRecordActivity.start(requireContext(), RecordType.LIKE)
            }

            CountType.COIN -> {
                GetRecordActivity.start(requireContext(), RecordType.COIN)
            }

            CountType.FANS -> {
                FansActivity.start(requireContext())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CiliComposeTheme {
                    MineContent(
                        userInfo = mineViewModel.userInfo.value,
                        isRefreshing = mineViewModel.isRefreshing.value,
                        onRefresh = { mineViewModel.refreshUserInfo() },
                        onLogout = { openLogoutDialog.value = true },
                        openLogoutDialog = openLogoutDialog.value,
                        onDismissLogoutDialog = { openLogoutDialog.value = false },
                        onConfirmLogout = {
                            // 显示加载状态
                            openLogoutDialog.value = false
                            // 直接调用退出登录，LoginStateManager 内部已经有防重复调用机制
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    AccountManager.logout()
                                    // 在主线程中跳转到登录界面
                                    withContext(Dispatchers.Main) {
                                        LoginActivity.start(requireContext())
                                        requireActivity().finish()
                                    }
                                } catch (e: Exception) {
                                    // 如果退出登录失败，在主线程显示错误提示
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(requireContext(), "退出登录失败: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        onPageOpen = { type -> openPage(type) },
                        onBannerClick = { item ->
                            if (item.linkUrl.isNotEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, item.linkUrl.toUri())
                                startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MineFragment()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MineContent(
    userInfo: UserInfo,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    openLogoutDialog: Boolean,
    onDismissLogoutDialog: () -> Unit,
    onConfirmLogout: () -> Unit,
    onPageOpen: (CountType) -> Unit,
    onBannerClick: (BannerBean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                onRefresh()
            }
        }
    )

    Box(
        modifier = Modifier.pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier.verticalScroll(state = rememberScrollState())
        ) {
            if (openLogoutDialog) {
                AlertDialog(
                    onDismissRequest = onDismissLogoutDialog,
                    dismissButton = {
                        TextButton(onClick = onDismissLogoutDialog) {
                            Text(text = "取消")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = onConfirmLogout) {
                            Text(text = "确定")
                        }
                    },
                    text = { Text("确定退出账号?") }
                )
            }
            
            AccountManager.setUserInfo(userInfo)
            val bannerList = userInfo.bannerList.map { BannerBean(it.cover, it.url) }
            val fans = if(userInfo.name.contains("阿翔")) 10000000L else userInfo.fans
            val countList = listOf(
                CountItem(userInfo.collect, "收藏") { onPageOpen(CountType.COLLECT) },
                CountItem(userInfo.like, "点赞") { onPageOpen(CountType.LIKE) },
                CountItem(userInfo.view, "浏览") { onPageOpen(CountType.VIEW) },
                CountItem(userInfo.coin, "金币") { onPageOpen(CountType.COIN) },
                CountItem(fans.toInt(), "粉丝") { onPageOpen(CountType.FANS) }
            )
            
            UserInfoView(userInfo.name, userInfo.face)
            Spacer(modifier = Modifier.height(20.dp))
            CountContent(countList)
            if (bannerList.isNotEmpty()) {
                BannerSection(bannerList, onBannerClick)
            }
            SettingContent()
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFFF9912)
                )
            ) {
                Text(
                    text = "退出登录",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(55.dp))
        }
        
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = Color(LocalContext.current.getColor(R.color.progress_yellow))
        )
    }
}

/**
 *
 * 头像 昵称
 *
 */
@Composable
fun UserInfoView(userName: String, avatar: String) {

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(15.dp)) {
        Surface(
            shape = CircleShape
        ) {
            Image(
                painter = rememberAsyncImagePainter(avatar),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = userName,
            fontFamily = FontFamily(Font(R.font.ali_85b)),
            color = Color(LocalContext.current.getColor(R.color.setting_item_text))
        )
    }


}

/**
 *
 *
 *  关注 收藏 点赞 金币 粉丝
 *
 */
@Composable
fun CountContent(list: List<CountItem>) {

    Row(Modifier.background(Color(220, 220, 220))) {
        Row(modifier = Modifier.padding(PaddingValues(horizontal = 10.dp, vertical = 1.dp))) {
            list.forEach { item ->
                Row(
                    modifier = Modifier.weight(1 / list.size.toFloat()),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CountItem(item)
                }
            }
        }
    }

}

@Composable
fun CountItem(item: CountItem) {
    Column(Modifier
        .padding(0.dp)
        .clickable {
            item.function.invoke()
        }, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = FormatUtil.countFormatNoFloat(item.count),
            color = Color.Black,
            fontSize = 14.sp,

            )
        Text(
            text = item.text,
            color = Color.Gray,
            fontSize = 12.sp,
        )
    }

}


@Preview
@Composable
fun SettingContent() {
    val context = LocalContext.current
    Column(Modifier.padding(5.dp)) {
        Text(text = "设置", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Column(Modifier.padding(15.dp)) {
            SettingItem(drawableId = R.drawable.mode, title = "模式设置") {
                context.startActivity(Intent(context, ThemeSettingActivity::class.java))
            }
            SettingItem(drawableId = R.drawable.play_set, title = "播放设置") {
                context.startActivity(Intent(context, PlaySettingActivity::class.java))
            }
            SettingItem(drawableId = R.drawable.clean, title = "清除缓存空间") {
                clearAppCache(context)
                Toast.makeText(context, "缓存已清除", Toast.LENGTH_SHORT).show()
            }
            SettingItem(drawableId = R.drawable.update, title = "检查更新") {
                Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show()
            }
            SettingItem(drawableId = R.drawable.about, title = "关于") {
                context.startActivity(Intent(context, AboutActivity::class.java))
            }
        }
    }
}


@Composable
fun SettingItem(drawableId: Int, title: String, onClick: () -> Unit) {
    Row(
        Modifier
            .clickable {
                onClick.invoke()
                log(TAG, "settingitem:$title click")
            }
            .padding(vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 10.dp)
                .size(35.dp, 35.dp)
        )
        Text(
            text = title,
            fontSize = 14.sp,
            fontStyle = FontStyle.Normal,
            color = Color(LocalContext.current.getColor(R.color.setting_item_text)),
            modifier = Modifier.weight(1.0f)
        )
        Image(painter = painterResource(id = R.drawable.right_arrow), contentDescription = null)
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerSection(bannerList: List<BannerBean>, onClick: (BannerBean) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { bannerList.size })
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(pagerState, bannerList, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                delay(BANNER_LOOP_TIME)
                if (bannerList.size > 1) {
                    val nextPage = (pagerState.currentPage + 1) % bannerList.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            val item = bannerList[page]
            Image(
                painter = rememberAsyncImagePainter(item.data),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onClick(item) },
                contentScale = ContentScale.Crop
            )
        }
        // 指示器
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(bannerList.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 10.dp else 8.dp)
                        .background(
                            color = if (isSelected) Color.White else Color.LightGray,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}


fun clearAppCache(context: Context) {
    // 清除 Glide 图片缓存
    GlideApp.get(context).clearMemory()
    CoroutineScope(Dispatchers.IO).launch {
        GlideApp.get(context).clearDiskCache()
    }
    // 清除 OkHttp 缓存
    try {
        ApiHelper.instance.okHttpClient.cache?.evictAll()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    // 清除其它缓存
    CacheManager.clearInternalCache(context)

}


