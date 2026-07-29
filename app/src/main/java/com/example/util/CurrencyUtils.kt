package com.example.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun formatRupiah(amount: Double): String {
        return try {
            val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
            "Rp" + formatter.format(amount.toLong())
        } catch (e: Exception) {
            "Rp" + amount.toLong().toString()
        }
    }

    fun parseRupiahInput(input: String): Double {
        val clean = input.replace("[^0-9]".toRegex(), "")
        return clean.toDoubleOrNull() ?: 0.0
    }
}
