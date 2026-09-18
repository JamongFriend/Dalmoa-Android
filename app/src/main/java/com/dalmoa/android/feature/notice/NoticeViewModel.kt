package com.dalmoa.android.feature.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.data.remote.api.NoticeApi
import com.dalmoa.android.data.remote.dto.notice.NoticeDetail
import com.dalmoa.android.data.remote.dto.notice.NoticeListItem
import kotlinx.coroutines.launch

class NoticeViewModel : ViewModel() {

    private val noticeApi = ApiClient.retrofit.create(NoticeApi::class.java)

    private val _notices = MutableLiveData<List<NoticeListItem>>(emptyList())
    val notices: LiveData<List<NoticeListItem>> = _notices

    private val _notice = MutableLiveData<NoticeDetail?>(null)
    val notice: LiveData<NoticeDetail?> = _notice

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadNotices() {
        viewModelScope.launch {
            _error.value = null
            try {
                val response = noticeApi.getNotices()
                if (response.isSuccessful && response.body() != null) {
                    _notices.value = response.body()!!
                } else {
                    _error.value = "공지사항을 불러오지 못했습니다 (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "네트워크 오류: ${e.localizedMessage}"
            }
        }
    }

    fun loadNotice(id: Long) {
        viewModelScope.launch {
            _error.value = null
            try {
                val response = noticeApi.getNotice(id)
                if (response.isSuccessful && response.body() != null) {
                    _notice.value = response.body()
                } else {
                    _error.value = "공지사항을 불러오지 못했습니다 (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "네트워크 오류: ${e.localizedMessage}"
            }
        }
    }
}
