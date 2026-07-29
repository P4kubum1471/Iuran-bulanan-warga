package com.example.data.local

import androidx.room.*
import com.example.data.model.PaymentRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_records")
    fun getAllPaymentRecords(): Flow<List<PaymentRecord>>

    @Query("SELECT * FROM payment_records WHERE monthIndex = :monthIndex AND year = :year")
    fun getPaymentRecordsForMonth(monthIndex: Int, year: Int): Flow<List<PaymentRecord>>

    @Query("SELECT * FROM payment_records WHERE citizenId = :citizenId AND monthIndex = :monthIndex AND year = :year LIMIT 1")
    suspend fun getPaymentRecord(citizenId: Long, monthIndex: Int, year: Int): PaymentRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePaymentRecord(record: PaymentRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentRecords(records: List<PaymentRecord>)

    @Query("DELETE FROM payment_records WHERE citizenId = :citizenId")
    suspend fun deleteRecordsForCitizen(citizenId: Long)

    @Query("DELETE FROM payment_records")
    suspend fun deleteAllPaymentRecords()
}
