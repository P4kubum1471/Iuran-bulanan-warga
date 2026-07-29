package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.model.Citizen
import com.example.data.model.CitizenPaymentItem
import com.example.data.model.HijriMonth
import com.example.data.model.PaymentRecord

object ReportUtils {

    fun generateCsvExport(
        citizens: List<Citizen>,
        paymentRecords: List<PaymentRecord>,
        selectedYear: Int = 1446
    ): String {
        val sb = StringBuilder()
        // Header
        sb.append("No,Nama Warga,Blok/No,No HP")
        HijriMonth.ALL_MONTHS.forEach { month ->
            sb.append(",${month.name}_Tagihan,${month.name}_Admin,${month.name}_Denda,${month.name}_KSU,${month.name}_Total")
        }
        sb.append("\n")

        // Rows
        citizens.forEachIndexed { index, citizen ->
            sb.append("${index + 1},\"${citizen.name}\",\"${citizen.houseNumber}\",\"${citizen.phone}\"")
            HijriMonth.ALL_MONTHS.forEach { month ->
                val record = paymentRecords.find { it.citizenId == citizen.id && it.monthIndex == month.index && it.year == selectedYear }
                val tagihan = record?.tagihan?.toLong() ?: 0
                val admin = record?.admin?.toLong() ?: 0
                val denda = record?.denda?.toLong() ?: 0
                val ksu = record?.ksu?.toLong() ?: 0
                val total = tagihan + admin + denda + ksu
                sb.append(",$tagihan,$admin,$denda,$ksu,$total")
            }
            sb.append("\n")
        }

        return sb.toString()
    }

    fun generateMonthlyPrintText(
        monthName: String,
        year: Int,
        items: List<CitizenPaymentItem>
    ): String {
        val sb = StringBuilder()
        sb.append("====================================================\n")
        sb.append("      LAPORAN PENAGIHAN IURAN WARGA (HIJRIYAH)\n")
        sb.append("            BULAN: $monthName $year H\n")
        sb.append("====================================================\n\n")

        val paidCount = items.count { it.isPaid }
        val totalCitizens = items.size
        val totalTagihan = items.sumOf { it.tagihan }
        val totalAdmin = items.sumOf { it.admin }
        val totalDenda = items.sumOf { it.denda }
        val totalKsu = items.sumOf { it.ksu }
        val grandTotal = items.sumOf { it.total }
        val percentage = if (totalCitizens > 0) (paidCount.toDouble() / totalCitizens * 100).toInt() else 0

        sb.append("RINGKASAN:\n")
        sb.append("- Total Warga        : $totalCitizens orang\n")
        sb.append("- Warga Sudah Bayar  : $paidCount orang ($percentage%)\n")
        sb.append("- Total Tagihan      : ${CurrencyUtils.formatRupiah(totalTagihan)}\n")
        sb.append("- Total Admin        : ${CurrencyUtils.formatRupiah(totalAdmin)}\n")
        sb.append("- Total Denda        : ${CurrencyUtils.formatRupiah(totalDenda)}\n")
        sb.append("- Total KSU          : ${CurrencyUtils.formatRupiah(totalKsu)}\n")
        sb.append("- Total Penerimaan   : ${CurrencyUtils.formatRupiah(grandTotal)}\n")
        sb.append("----------------------------------------------------\n\n")

        sb.append(String.format("%-4s | %-20s | %-12s | %-8s\n", "No", "Nama Warga", "Status", "Total"))
        sb.append("----------------------------------------------------\n")

        items.forEachIndexed { idx, item ->
            val status = if (item.isPaid) "LUNAS" else "BELUM"
            sb.append(String.format("%-4d | %-20s | %-12s | %-8s\n",
                idx + 1,
                item.citizen.name.take(20),
                status,
                CurrencyUtils.formatRupiah(item.total)
            ))
        }
        sb.append("----------------------------------------------------\n")
        sb.append("Dicetak pada: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}\n")
        return sb.toString()
    }

    fun generateYearlyPrintText(
        year: Int,
        citizens: List<Citizen>,
        paymentRecords: List<PaymentRecord>
    ): String {
        val sb = StringBuilder()
        sb.append("====================================================\n")
        sb.append("      LAPORAN TAHUNAN PENAGIHAN IURAN WARGA\n")
        sb.append("               TAHUN $year HIJRIYAH\n")
        sb.append("====================================================\n\n")

        val grandTotalYear = paymentRecords.filter { it.year == year }.sumOf { it.total }
        val totalTransactions = paymentRecords.filter { it.year == year && it.isPaid }.size

        sb.append("RINGKASAN TAHUNAN:\n")
        sb.append("- Total Warga Terdaftar : ${citizens.size} orang\n")
        sb.append("- Total Transaksi Bayar : $totalTransactions transaksi\n")
        sb.append("- Total Pemasukan Setahun: ${CurrencyUtils.formatRupiah(grandTotalYear)}\n\n")

        sb.append("PERIKANAN TIAP BULAN HIJRIYAH:\n")
        sb.append("----------------------------------------------------\n")
        sb.append(String.format("%-15s | %-12s | %-16s\n", "Bulan Hijriyah", "Sudah Bayar", "Total Penerimaan"))
        sb.append("----------------------------------------------------\n")

        HijriMonth.ALL_MONTHS.forEach { month ->
            val monthRecords = paymentRecords.filter { it.monthIndex == month.index && it.year == year }
            val paidCount = monthRecords.count { it.isPaid }
            val totalReceived = monthRecords.sumOf { it.total }
            sb.append(String.format("%-15s | %-12s | %-16s\n",
                month.name,
                "$paidCount orang",
                CurrencyUtils.formatRupiah(totalReceived)
            ))
        }
        sb.append("----------------------------------------------------\n")
        return sb.toString()
    }

    fun shareText(context: Context, title: String, content: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        context.startActivity(Intent.createChooser(shareIntent, title))
    }
}
