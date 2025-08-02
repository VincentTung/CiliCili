package com.vincent.android.cili.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.android.cili.entity.VideoEntity
import com.vincent.android.cili.use_case.get_record.GetRecordUseCase
import com.vincent.android.cili.use_case.get_record.RecordType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetRecordViewModel @Inject constructor(private val recordUseCase: GetRecordUseCase) :
    ViewModel() {

    private val _recordListState:MutableState<List<VideoEntity>> = mutableStateOf(emptyList())
    val recordList: State<List<VideoEntity>> get() = _recordListState
    fun getRecord(recordType: RecordType) {
        recordUseCase(recordType).onStart { }.onEmpty { }.onEach {
            _recordListState.value = it.data?.list ?: emptyList()
        }.launchIn(
            viewModelScope
        )
    }

}