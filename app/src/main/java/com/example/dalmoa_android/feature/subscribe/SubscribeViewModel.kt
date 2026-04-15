package com.example.dalmoa_android.feature.subscribe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dalmoa_android.model.SubCategory
import com.example.dalmoa_android.model.Subscribe

class SubscribeViewModel : ViewModel() {

    private val _subscriptions = MutableLiveData<List<Subscribe>>()
    val subscriptions: LiveData<List<Subscribe>> = _subscriptions

    private val _filteredSubscriptions = MutableLiveData<List<Subscribe>>()
    val filteredSubscriptions: LiveData<List<Subscribe>> = _filteredSubscriptions

    private val _selectedCategory = MutableLiveData<SubCategory?>(null)
    val selectedCategory: LiveData<SubCategory?> = _selectedCategory

    init {
        loadSubscriptions()
    }

    private fun loadSubscriptions() {
        // 임시 더미 데이터
        val dummyData = listOf(
            Subscribe(1, "넷플릭스", 17000.0, "KRW", "2024-04-14", SubCategory.OTT),
            Subscribe(2, "유튜브 프리미엄", 14900.0, "KRW", "2024-04-20", SubCategory.MUSIC),
            Subscribe(3, "쿠팡 와우", 4990.0, "KRW", "2024-04-25", SubCategory.LIFESTYLE),
            Subscribe(4, "티빙", 9500.0, "KRW", "2024-04-10", SubCategory.OTT),
            Subscribe(5, "멜론", 10900.0, "KRW", "2024-04-15", SubCategory.MUSIC),
            Subscribe(6, "PS Plus", 7500.0, "KRW", "2024-04-05", SubCategory.GAME)
        )
        _subscriptions.value = dummyData
        filterByCategory(null)
    }

    fun filterByCategory(category: SubCategory?) {
        _selectedCategory.value = category
        val currentList = _subscriptions.value ?: emptyList()
        if (category == null) {
            _filteredSubscriptions.value = currentList
        } else {
            _filteredSubscriptions.value = currentList.filter { it.subCategory == category }
        }
    }

    fun getTotalAmount(): Double {
        return _filteredSubscriptions.value?.sumOf { it.price } ?: 0.0
    }

    fun getSubscriptionCount(): Int {
        return _filteredSubscriptions.value?.size ?: 0
    }
}