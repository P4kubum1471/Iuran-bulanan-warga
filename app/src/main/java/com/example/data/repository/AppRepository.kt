package com.example.data.repository

import com.example.data.local.CitizenDao
import com.example.data.local.PaymentDao
import com.example.data.model.Citizen
import com.example.data.model.PaymentRecord
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val citizenDao: CitizenDao,
    private val paymentDao: PaymentDao
) {
    val allCitizens: Flow<List<Citizen>> = citizenDao.getAllCitizens()
    val allPaymentRecords: Flow<List<PaymentRecord>> = paymentDao.getAllPaymentRecords()

    fun searchCitizens(query: String): Flow<List<Citizen>> = citizenDao.searchCitizens(query)

    fun getPaymentRecordsForMonth(monthIndex: Int, year: Int = 1446): Flow<List<PaymentRecord>> {
        return paymentDao.getPaymentRecordsForMonth(monthIndex, year)
    }

    suspend fun addCitizen(citizen: Citizen): Long {
        return citizenDao.insertCitizen(citizen)
    }

    suspend fun updateCitizen(citizen: Citizen) {
        citizenDao.updateCitizen(citizen)
    }

    suspend fun deleteCitizen(id: Long) {
        paymentDao.deleteRecordsForCitizen(id)
        citizenDao.deleteCitizenById(id)
    }

    suspend fun savePaymentRecord(
        citizenId: Long,
        monthIndex: Int,
        year: Int,
        tagihan: Double,
        admin: Double,
        denda: Double,
        ksu: Double
    ) {
        val existing = paymentDao.getPaymentRecord(citizenId, monthIndex, year)
        val record = existing?.copy(
            tagihan = tagihan,
            admin = admin,
            denda = denda,
            ksu = ksu,
            paymentDate = if (tagihan > 0) System.currentTimeMillis() else null
        ) ?: PaymentRecord(
            citizenId = citizenId,
            monthIndex = monthIndex,
            year = year,
            tagihan = tagihan,
            admin = admin,
            denda = denda,
            ksu = ksu,
            paymentDate = if (tagihan > 0) System.currentTimeMillis() else null
        )
        paymentDao.insertOrUpdatePaymentRecord(record)
    }

    suspend fun bulkApplyStandardDues(
        citizenIds: List<Long>,
        monthIndex: Int,
        year: Int,
        tagihan: Double,
        admin: Double,
        ksu: Double
    ) {
        val records = mutableListOf<PaymentRecord>()
        for (citizenId in citizenIds) {
            val existing = paymentDao.getPaymentRecord(citizenId, monthIndex, year)
            val record = existing?.copy(
                tagihan = tagihan,
                admin = admin,
                ksu = ksu,
                paymentDate = if (tagihan > 0) System.currentTimeMillis() else null
            ) ?: PaymentRecord(
                citizenId = citizenId,
                monthIndex = monthIndex,
                year = year,
                tagihan = tagihan,
                admin = admin,
                denda = 0.0,
                ksu = ksu,
                paymentDate = if (tagihan > 0) System.currentTimeMillis() else null
            )
            records.add(record)
        }
        paymentDao.insertPaymentRecords(records)
    }

    suspend fun resetAllData() {
        paymentDao.deleteAllPaymentRecords()
        citizenDao.deleteAllCitizens()
    }

    suspend fun restoreSampleData() {
        resetAllData()
        val initialCitizens = listOf(
            Citizen(name = "Bambang Santoso", houseNumber = "Blok A-01", phone = "081234567890"),
            Citizen(name = "Siti Rahmawati", houseNumber = "Blok A-02", phone = "081298765432"),
            Citizen(name = "Ahmad Hidayat", houseNumber = "Blok A-03", phone = "081311223344"),
            Citizen(name = "Eko Prasetyo", houseNumber = "Blok B-01", phone = "081555667788"),
            Citizen(name = "Dewi Kurnia", houseNumber = "Blok B-02", phone = "081799887766"),
            Citizen(name = "Hendra Wijaya", houseNumber = "Blok B-03", phone = "081900112233"),
            Citizen(name = "Rina Sulistia", houseNumber = "Blok C-01", phone = "082133445566"),
            Citizen(name = "Nurhasan", houseNumber = "Blok C-02", phone = "082344556677")
        )

        val citizenIds = mutableListOf<Long>()
        for (citizen in initialCitizens) {
            val id = citizenDao.insertCitizen(citizen)
            citizenIds.add(id)
        }

        val records = mutableListOf<PaymentRecord>()
        // Muharram
        citizenIds.forEachIndexed { index, citizenId ->
            if (index < 6) {
                records.add(
                    PaymentRecord(
                        citizenId = citizenId,
                        monthIndex = 1,
                        year = 1446,
                        tagihan = 50000.0,
                        admin = 5000.0,
                        denda = if (index == 5) 10000.0 else 0.0,
                        ksu = 10000.0,
                        paymentDate = System.currentTimeMillis()
                    )
                )
            }
        }
        // Shafar
        citizenIds.forEachIndexed { index, citizenId ->
            if (index < 4) {
                records.add(
                    PaymentRecord(
                        citizenId = citizenId,
                        monthIndex = 2,
                        year = 1446,
                        tagihan = 50000.0,
                        admin = 5000.0,
                        denda = 0.0,
                        ksu = 10000.0,
                        paymentDate = System.currentTimeMillis()
                    )
                )
            }
        }
        paymentDao.insertPaymentRecords(records)
    }
}
