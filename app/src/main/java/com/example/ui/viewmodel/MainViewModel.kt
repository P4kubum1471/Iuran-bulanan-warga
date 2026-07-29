package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Citizen
import com.example.data.model.CitizenPaymentItem
import com.example.data.model.HijriMonth
import com.example.data.model.PaymentRecord
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    val selectedYear = MutableStateFlow(1446)
    val selectedMonthIndex = MutableStateFlow(1) // 1 = Muharram
    val searchQuery = MutableStateFlow("")

    val allCitizens: StateFlow<List<Citizen>>
    val allPaymentRecords: StateFlow<List<PaymentRecord>>

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = AppRepository(db.citizenDao(), db.paymentDao())

        allCitizens = repository.allCitizens
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allPaymentRecords = repository.allPaymentRecords
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Filtered citizens based on search query
    val filteredCitizens: StateFlow<List<Citizen>> = combine(allCitizens, searchQuery) { citizens, query ->
        if (query.isBlank()) {
            citizens
        } else {
            citizens.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.houseNumber.contains(query, ignoreCase = true) ||
                it.phone.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined Citizen Payment items for current selected month
    val currentMonthPaymentItems: StateFlow<List<CitizenPaymentItem>> = combine(
        filteredCitizens,
        allPaymentRecords,
        selectedMonthIndex,
        selectedYear
    ) { citizens, records, monthIdx, year ->
        citizens.map { citizen ->
            val record = records.find {
                it.citizenId == citizen.id && it.monthIndex == monthIdx && it.year == year
            }
            CitizenPaymentItem(citizen = citizen, paymentRecord = record)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setSelectedMonth(index: Int) {
        selectedMonthIndex.value = index
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun addCitizen(name: String, houseNumber: String, phone: String, notes: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                val citizen = Citizen(
                    name = name.trim(),
                    houseNumber = houseNumber.trim(),
                    phone = phone.trim(),
                    notes = notes.trim()
                )
                repository.addCitizen(citizen)
            }
        }
    }

    fun updateCitizen(citizen: Citizen) {
        viewModelScope.launch {
            repository.updateCitizen(citizen)
        }
    }

    fun deleteCitizen(citizenId: Long) {
        viewModelScope.launch {
            repository.deleteCitizen(citizenId)
        }
    }

    fun updatePayment(
        citizenId: Long,
        monthIndex: Int,
        tagihan: Double,
        admin: Double,
        denda: Double,
        ksu: Double
    ) {
        viewModelScope.launch {
            repository.savePaymentRecord(
                citizenId = citizenId,
                monthIndex = monthIndex,
                year = selectedYear.value,
                tagihan = tagihan,
                admin = admin,
                denda = denda,
                ksu = ksu
            )
        }
    }

    fun bulkApplyPresetDues(
        tagihan: Double,
        admin: Double,
        ksu: Double
    ) {
        viewModelScope.launch {
            val citizens = allCitizens.value
            val citizenIds = citizens.map { it.id }
            repository.bulkApplyStandardDues(
                citizenIds = citizenIds,
                monthIndex = selectedMonthIndex.value,
                year = selectedYear.value,
                tagihan = tagihan,
                admin = admin,
                ksu = ksu
            )
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }

    fun restoreSampleData() {
        viewModelScope.launch {
            repository.restoreSampleData()
        }
    }
}
