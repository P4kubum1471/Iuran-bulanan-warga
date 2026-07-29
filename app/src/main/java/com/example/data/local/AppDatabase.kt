package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Citizen
import com.example.data.model.PaymentRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Citizen::class, PaymentRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun citizenDao(): CitizenDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope? = null): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "iuran_warga_db"
                )
                .addCallback(DatabaseCallback(coroutineScope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope?
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope?.launch(Dispatchers.IO) {
                        populateInitialData(database.citizenDao(), database.paymentDao())
                    }
                }
            }

            suspend fun populateInitialData(citizenDao: CitizenDao, paymentDao: PaymentDao) {
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

                // Sample payments for Muharram (Month 1) & Shafar (Month 2)
                val year = 1446
                val records = mutableListOf<PaymentRecord>()

                // Muharram payments (some paid)
                citizenIds.forEachIndexed { index, citizenId ->
                    if (index < 6) { // 6 paid
                        records.add(
                            PaymentRecord(
                                citizenId = citizenId,
                                monthIndex = 1,
                                year = year,
                                tagihan = 50000.0,
                                admin = 5000.0,
                                denda = if (index == 5) 10000.0 else 0.0,
                                ksu = 10000.0,
                                paymentDate = System.currentTimeMillis()
                            )
                        )
                    }
                }

                // Shafar payments (some paid)
                citizenIds.forEachIndexed { index, citizenId ->
                    if (index < 4) { // 4 paid
                        records.add(
                            PaymentRecord(
                                citizenId = citizenId,
                                monthIndex = 2,
                                year = year,
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
    }
}
