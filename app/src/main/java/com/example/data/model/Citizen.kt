package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citizens")
data class Citizen(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val houseNumber: String = "",
    val phone: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
