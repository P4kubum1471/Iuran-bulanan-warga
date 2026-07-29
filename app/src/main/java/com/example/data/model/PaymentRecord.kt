package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payment_records",
    foreignKeys = [
        ForeignKey(
            entity = Citizen::class,
            parentColumns = ["id"],
            childColumns = ["citizenId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("citizenId"),
        Index(value = ["citizenId", "monthIndex", "year"], unique = true)
    ]
)
data class PaymentRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val citizenId: Long,
    val monthIndex: Int, // 1..12
    val year: Int = 1446, // Default Hijri year
    val tagihan: Double = 0.0,
    val admin: Double = 0.0,
    val denda: Double = 0.0,
    val ksu: Double = 0.0,
    val paymentDate: Long? = null
) {
    val total: Double
        get() = tagihan + admin + denda + ksu

    val isPaid: Boolean
        get() = tagihan > 0
}
