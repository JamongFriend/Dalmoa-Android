package com.dalmoa.android.feature.subscribe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.data.remote.api.SubscribeApi
import com.dalmoa.android.model.SubCategory
import com.dalmoa.android.model.Subscribe
import kotlinx.coroutines.launch
import java.util.Calendar

class SubscribeViewModel : ViewModel() {

    private val subscribeApi = ApiClient.retrofit.create(SubscribeApi::class.java)

    private val _subscriptions = MutableLiveData<List<Subscribe>>(emptyList())
    val subscriptions: LiveData<List<Subscribe>> = _subscriptions

    private val _filteredSubscriptions = MutableLiveData<List<Subscribe>>(emptyList())
    val filteredSubscriptions: LiveData<List<Subscribe>> = _filteredSubscriptions

    private val _selectedCategory = MutableLiveData<SubCategory?>(null)
    val selectedCategory: LiveData<SubCategory?> = _selectedCategory

    // 현재 선택된 날짜 상태 추가
    private val _currentCalendar = MutableLiveData<Calendar>(Calendar.getInstance())
    val currentCalendar: LiveData<Calendar> = _currentCalendar

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadSubscriptions()
    }

    fun loadSubscriptions(memberId: Long = 1L) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 전달받은 memberId 사용
                val response = subscribeApi.getSubscriptions(memberId)
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    _subscriptions.value = data
                    filterByCategory(_selectedCategory.value)
                } else {
                    _error.value = "데이터를 불러오지 못했습니다 ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "네트워크 오류: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 다음 달로 이동
    fun nextMonth() {
        val cal = _currentCalendar.value ?: Calendar.getInstance()
        cal.add(Calendar.MONTH, 1)
        _currentCalendar.value = cal
        // 달이 바뀌면 데이터를 다시 로드하거나 필터링
        loadSubscriptions()
    }

    // 이전 달로 이동
    fun prevMonth() {
        val cal = _currentCalendar.value ?: Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        _currentCalendar.value = cal
        loadSubscriptions()
    }

    fun filterByCategory(category: SubCategory?) {
        _selectedCategory.value = category
        val currentList = _subscriptions.value ?: emptyList()
        if (category == null) {
            _filteredSubscriptions.value = currentList
        } else {
            _filteredSubscriptions.value = currentList.filter { it.category == category }
        }
    }

    // 카테고리별 지출 합계 계산
    fun getSpendingByCategory(): Map<SubCategory, Double> {
        return _subscriptions.value?.groupBy { it.category }
            ?.mapValues { entry -> entry.value.sumOf { it.price } }
            ?: emptyMap()
    }

    // 가장 많이 지출한 카테고리 정보 가져오기
    fun getTopSpendingCategory(): Pair<SubCategory, Double>? {
        return getSpendingByCategory().maxByOrNull { it.value }?.toPair()
    }

    fun getTotalAmount(): Double {
        return _filteredSubscriptions.value?.sumOf { it.price } ?: 0.0
    }

    fun getSubscriptionCount(): Int {
        return _filteredSubscriptions.value?.size ?: 0
    }
}
