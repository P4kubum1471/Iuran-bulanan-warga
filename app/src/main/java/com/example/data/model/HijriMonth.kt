package com.example.data.model

data class HijriMonth(
    val index: Int, // 1..12
    val name: String,
    val description: String
) {
    companion object {
        val ALL_MONTHS = listOf(
            HijriMonth(1, "Muharram", "Bulan ke-1 Hijriyah"),
            HijriMonth(2, "Shafar", "Bulan ke-2 Hijriyah"),
            HijriMonth(3, "Rabiul Awal", "Bulan ke-3 Hijriyah"),
            HijriMonth(4, "Rabiul Akhir", "Bulan ke-4 Hijriyah"),
            HijriMonth(5, "Jumadil Awal", "Bulan ke-5 Hijriyah"),
            HijriMonth(6, "Jumadil Akhir", "Bulan ke-6 Hijriyah"),
            HijriMonth(7, "Rajab", "Bulan ke-7 Hijriyah"),
            HijriMonth(8, "Sya'ban", "Bulan ke-8 Hijriyah"),
            HijriMonth(9, "Ramadhan", "Bulan ke-9 Hijriyah"),
            HijriMonth(10, "Syawal", "Bulan ke-10 Hijriyah"),
            HijriMonth(11, "Dzulqa'dah", "Bulan ke-11 Hijriyah"),
            HijriMonth(12, "Zulhijjah", "Bulan ke-12 Hijriyah")
        )

        fun getByIndex(index: Int): HijriMonth {
            return ALL_MONTHS.find { it.index == index } ?: ALL_MONTHS[0]
        }
    }
}
