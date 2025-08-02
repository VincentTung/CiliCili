package com.vincent.android.cili.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.vincent.android.cili.R
import com.vincent.android.cili.extensions.log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FlowExampleActivity : BaseActivity() {

    companion object {
        private val TAG = FlowExampleActivity::class.java.simpleName
    }

    private val mChannel = Channel<String>()
    private lateinit var mTV1: TextView
    private lateinit var mTV2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_flow)


        mTV1 = findViewById(R.id.tv1)
        mTV2 = findViewById(R.id.tv2)

        findViewById<Button>(R.id.button).setOnClickListener {
            lifecycleScope.launch {
                val s = "hello"
//                while (true) {
                mChannel.send(System.currentTimeMillis().toString())
//                    mChannel.send(s)
//                    delay(2000L)
//                }


            }

        }


//        lifecycleScope.launch {
//
//            val flow = mChannel.receiveAsFlow()
//            launch {
//                flow.collect {
//                    mTV1.text = it
//                }
//
//            }
//
//            launch {
//                flow.collect {
//                    mTV2.text = it
//                }
//            }
//
//        }
//
//        //回调操作符
//        lifecycleScope.launch {
//
//            flow {
//                for (i in 1..10) {
//                    emit(i)
//                    delay(1000L)
//                }
//            }
//                .flowOn(Dispatchers.IO)
//                .onStart {
//                    emit(10000)
//                    Log.d(TAG, "onStart: ")
//                }
//                .onEach {
//                    Log.d(TAG, "onEach:$it")
//                }
//                .onCompletion {
//                    Log.d(TAG, "onCompletion")
//                }
//                .map {
//                    "---$it"
//                }
//                .collect {
//                    Log.d(TAG, "collect:$it ")
//                }
//        }
//
//        val emptyFlow = emptyFlow<Int>().onEmpty {
//            Log.d(TAG, "onEmpty")
//        }.launchIn(lifecycleScope)
//
//
//
//
//        flowOf(1, 2, 3, 4).flowOn(Dispatchers.IO).onEach { }.map {
//
//
//        }.launchIn(lifecycleScope)
//

        val channel = Channel<Int>(Channel.BUFFERED)


        var value = 0

//        lifecycleScope.launch {
//            while (true) {
//
//                value++
//
//                channel.send(value)
//                Log.d(TAG, "channel——send:$value ")
//                delay(500L)
//
//            }
//
//        }
//        lifecycleScope.launch {
//
//            while (true) {
//
//
//                val value = channel.receive()
//                Log.d(TAG, "channel——receive:$value ")
//
//                delay(2000L)
//            }


//
//
//        val channelFlow = channelFlow<Int> {
//            send(1)
//            withContext(Dispatchers.IO) {
//                send(2)
//                send(2)
//                send(2)
//                send(2)
//                send(2)
//            }
//        }.onEach {
//            delay(1000L)
//            Log.d("channelFlow", "onEach:$it ")
//        }

//        channelFlow.launchIn(lifecycleScope)

//            listOf<Int>(1, 2, 3).asFlow()
//            arrayOf(1, 2, 4).asFlow()
//            flow<Int> { emit(1) }
//            flowOf("hello", "world")
//            channelFlow<Int> { send(2) }
//            emptyFlow<Int>()
//
//            MutableSharedFlow<Int>()
//            MutableStateFlow(1)
//
//            callbackFlow<Int> {
//
//


//        }

        lifecycleScope.launch {
            flow<Int> {
                var value = 0
                while (true) {
                    value++

                    emit(value)
                    log(TAG, "flow_send:$value")
                    delay(500L)
                }
            }.buffer(capacity = Channel.CONFLATED,onBufferOverflow = BufferOverflow.SUSPEND).flowOn(Dispatchers.IO).onEach { value ->
                log(TAG, "flow_receive:$value")
                delay(1000L)
            }.collect {
                log(TAG, "onCreate: collect flow")
            }


        }
//        var count = 0
//
//        val mutex = Mutex()
//        List(1000){
//            GlobalScope.launch {
//                mutex.withLock {
//                    count ++
//                }
//            }
//        }.joinAll()

//        flow<String> {
//            emit()
//        }
    }
}