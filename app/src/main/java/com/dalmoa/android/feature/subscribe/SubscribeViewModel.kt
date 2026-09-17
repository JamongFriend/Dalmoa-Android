package com.dalmoa.android.feature.subscribe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.data.remote.api.SubscribeApi
import com.dalmoa.android.model.MonthSpending
import com.dalmoa.android.model.SubCategory
import com.dalmoa.android.model.Subscribe
import com.dalmoa.android.model.SubscribeDashboard
import com.dalmoa.android.model.Term
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    private val _dashboard = MutableLiveData<SubscribeDashboard?>(null)
    val dashboard: LiveData<SubscribeDashboard?> = _dashboard

    private val _monthlySpendings = MutableLiveData<List<MonthSpending>>(emptyList())
    val monthlySpendings: LiveData<List<MonthSpending>> = _monthlySpendings

    private val _isMonthlyLoading = MutableLiveData<Boolean>(false)
    val isMonthlyLoading: LiveData<Boolean> = _isMonthlyLoading

    fun loadSubscriptions() {
        val (year, month) = currentYearMonth()
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = subscribeApi.getSubscriptions(year, month)
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

    fun loadDashboard() {
        val (year, month) = currentYearMonth()
        viewModelScope.launch {
            try {
                val response = subscribeApi.getDashboard(year, month)
                if (response.isSuccessful) {
                    _dashboard.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 현재 보고 있는 달 기준으로 최근 monthsBack개월(현재 달 포함)치 지출 합계를 불러옴
    fun loadMonthlySpendings(monthsBack: Int = 12) {
        val baseCal = _currentCalendar.value ?: Calendar.getInstance()
        viewModelScope.launch {
            _isMonthlyLoading.value = true
            try {
                val results = (0 until monthsBack).map { offset ->
                    async {
                        val cal = (baseCal.clone() as Calendar).apply { add(Calendar.MONTH, -offset) }
                        val year = cal.get(Calendar.YEAR)
                        val month = cal.get(Calendar.MONTH) + 1
                        val response = subscribeApi.getDashboard(year, month)
                        MonthSpending(year, month, response.body()?.totalAmount ?: 0.0)
                    }
                }.awaitAll()
                _monthlySpendings.value = results
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isMonthlyLoading.value = false
            }
        }
    }

    // 현재 선택된 달을 (year, month) 형태로 반환. month는 1~12 기준
    private fun currentYearMonth(): Pair<Int, Int> {
        val cal = _currentCalendar.value ?: Calendar.getInstance()
        return cal.get(Calendar.YEAR) to (cal.get(Calendar.MONTH) + 1)
    }

    // 다음 달로 이동
    fun nextMonth() {
        val cal = _currentCalendar.value ?: Calendar.getInstance()
        cal.add(Calendar.MONTH, 1)
        _currentCalendar.value = cal
        loadSubscriptions()
        loadDashboard()
    }

    // 이전 달로 이동
    fun prevMonth() {
        val cal = _currentCalendar.value ?: Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        _currentCalendar.value = cal
        loadSubscriptions()
        loadDashboard()
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

    // 카테고리별 월 환산 지출 합계 계산
    fun getSpendingByCategory(): Map<SubCategory, Double> {
        return _subscriptions.value?.groupBy { it.category }
            ?.mapValues { entry -> entry.value.sumOf { it.monthlyKrwAmount } }
            ?: emptyMap()
    }

    // 가장 많이 지출한 카테고리 정보 가져오기
    fun getTopSpendingCategory(): Pair<SubCategory, Double>? {
        return getSpendingByCategory().maxByOrNull { it.value }?.toPair()
    }

    fun getTotalAmount(): Double {
        return _filteredSubscriptions.value?.sumOf { it.monthlyKrwAmount } ?: 0.0
    }

    fun getSubscriptionCount(): Int {
        return _filteredSubscriptions.value?.size ?: 0
    }

    // 결제 주기(주/월/연)별 월 환산 지출 합계 계산
    fun getSpendingByTerm(): Map<Term, Double> {
        return _subscriptions.value?.groupBy { it.term }
            ?.mapValues { entry -> entry.value.sumOf { it.monthlyKrwAmount } }
            ?: emptyMap()
    }

    // 결제 주기(주/월/연)별 구독 개수 계산
    fun getCountByTerm(): Map<Term, Int> {
        return _subscriptions.value?.groupBy { it.term }
            ?.mapValues { entry -> entry.value.size }
            ?: emptyMap()
    }
}
