package com.example.dalmoa_android.feature.subscribe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dalmoa_android.core.ApiClient
import com.example.dalmoa_android.data.remote.api.SubscribeApi
import com.example.dalmoa_android.model.SubCategory
import com.example.dalmoa_android.model.Subscribe
import kotlinx.coroutines.launch

class SubscribeViewModel : ViewModel() {

    private val subscribeApi = ApiClient.retrofit.create(SubscribeApi::class.java)

    private val _subscriptions = MutableLiveData<List<Subscribe>>(emptyList())
    val subscriptions: LiveData<List<Subscribe>> = _subscriptions

    private val _filteredSubscriptions = MutableLiveData<List<Subscribe>>(emptyList())
    val filteredSubscriptions: LiveData<List<Subscribe>> = _filteredSubscriptions

    private val _selectedCategory = MutableLiveData<SubCategory?>(null)
    val selectedCategory: LiveData<SubCategory?> = _selectedCategory

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadSubscriptions()
    }

    fun loadSubscriptions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 임시로 memberId 1을 사용하여 데이터 요청
                // 실제로는 TokenManager 등을 통해 로그인된 사용자의 ID를 가져와야 합니다.
                val response = subscribeApi.getSubscriptions(1L)
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    _subscriptions.value = data
                    filterByCategory(_selectedCategory.value)
                } else {
                    _error.value = "데이터를 불러오지 못했습니다: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "네트워크 오류: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
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

    fun getTotalAmount(): Double {
        return _filteredSubscriptions.value?.sumOf { it.price } ?: 0.0
    }

    fun getSubscriptionCount(): Int {
        return _filteredSubscriptions.value?.size ?: 0
    }
}
