package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CitizenPaymentItem
import com.example.data.model.HijriMonth
import com.example.ui.components.BulkPresetDialog
import com.example.ui.components.EditPaymentDialog
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.UnpaidRed
import com.example.ui.viewmodel.MainViewModel
import com.example.util.CurrencyUtils
import com.example.util.ReportUtils

@Composable
fun PembayaranBulananScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedMonthIndex by viewModel.selectedMonthIndex.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val paymentItems by viewModel.currentMonthPaymentItems.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val currentMonthObj = HijriMonth.getByIndex(selectedMonthIndex)

    var editingItem by remember { mutableStateOf<CitizenPaymentItem?>(null) }
    var showBulkDialog by remember { mutableStateOf(false) }

    // Summary Statistics Calculations
    val totalCitizens = paymentItems.size
    val paidCount = paymentItems.count { it.isPaid }
    val percentage = if (totalCitizens > 0) (paidCount.toDouble() / totalCitizens * 100).toInt() else 0

    val sumTagihan = paymentItems.sumOf { it.tagihan }
    val sumAdmin = paymentItems.sumOf { it.admin }
    val sumDenda = paymentItems.sumOf { it.denda }
    val sumKsu = paymentItems.sumOf { it.ksu }
    val grandTotal = paymentItems.sumOf { it.total }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Hijri Month Fast Picker Scrollable Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HijriMonth.ALL_MONTHS.forEach { month ->
                val isSelected = month.index == selectedMonthIndex
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedMonth(month.index) },
                    label = { Text("${month.index}. ${month.name}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryNavy,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Top Statistics Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bulan ${currentMonthObj.name} $selectedYear H",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                        )
                        Text(
                            text = "Ringkasan Pembayaran Iuran",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SecondaryTeal.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$percentage% Paid",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SecondaryTeal
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { if (totalCitizens > 0) paidCount.toFloat() / totalCitizens else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PaidGreen,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Paid Count row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sudah Bayar: $paidCount dari $totalCitizens warga",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = PaidGreen
                    )
                    Text(
                        text = "Belum Bayar: ${totalCitizens - paidCount} warga",
                        style = MaterialTheme.typography.bodySmall,
                        color = UnpaidRed
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Stats breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBreakdownItem("Tagihan", CurrencyUtils.formatRupiah(sumTagihan))
                    StatBreakdownItem("Admin", CurrencyUtils.formatRupiah(sumAdmin))
                    StatBreakdownItem("Denda", CurrencyUtils.formatRupiah(sumDenda))
                    StatBreakdownItem("KSU", CurrencyUtils.formatRupiah(sumKsu))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryNavy)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL PENERIMAN",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = CurrencyUtils.formatRupiah(grandTotal),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = SecondaryTeal
                        )
                    }
                }
            }
        }

        // Action Buttons Bar & Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Cari nama...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Button(
                onClick = { showBulkDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Isi Massal", style = MaterialTheme.typography.labelSmall)
            }

            IconButton(
                onClick = {
                    val printText = ReportUtils.generateMonthlyPrintText(currentMonthObj.name, selectedYear, paymentItems)
                    ReportUtils.shareText(context, "Laporan Iuran ${currentMonthObj.name} $selectedYear H", printText)
                }
            ) {
                Icon(Icons.Default.Share, contentDescription = "Bagikan Laporan", tint = PrimaryNavy)
            }
        }

        // Monthly Payments List Table
        if (paymentItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada data warga terdaftar.\nSilakan tambah warga di menu 'Data Warga'.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(paymentItems, key = { _, item -> item.citizen.id }) { index, item ->
                    PaymentTableRowCard(
                        index = index + 1,
                        item = item,
                        onClick = { editingItem = item }
                    )
                }
            }
        }
    }

    // Dialogs
    if (editingItem != null) {
        EditPaymentDialog(
            item = editingItem!!,
            monthName = currentMonthObj.name,
            onDismiss = { editingItem = null },
            onSave = { tagihan, admin, denda, ksu ->
                viewModel.updatePayment(
                    citizenId = editingItem!!.citizen.id,
                    monthIndex = selectedMonthIndex,
                    tagihan = tagihan,
                    admin = admin,
                    denda = denda,
                    ksu = ksu
                )
                editingItem = null
                Toast.makeText(context, "Data pembayaran tersimpan", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showBulkDialog) {
        BulkPresetDialog(
            monthName = currentMonthObj.name,
            onDismiss = { showBulkDialog = false },
            onConfirm = { tagihan, admin, ksu ->
                viewModel.bulkApplyPresetDues(tagihan, admin, ksu)
                showBulkDialog = false
                Toast.makeText(context, "Tagihan standard berhasil diterapkan ke seluruh warga", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun StatBreakdownItem(label: String, amount: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = PrimaryNavy
        )
    }
}

@Composable
private fun PaymentTableRowCard(
    index: Int,
    item: CitizenPaymentItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPaid) PaidGreen.copy(alpha = 0.04f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(PrimaryNavy.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryNavy
                        )
                    }

                    Column {
                        Text(
                            text = item.citizen.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryNavy
                        )
                        if (item.citizen.houseNumber.isNotBlank()) {
                            Text(
                                text = item.citizen.houseNumber,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Payment Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (item.isPaid) PaidGreen.copy(alpha = 0.15f) else UnpaidRed.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (item.isPaid) "LUNAS" else "BELUM BAYAR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (item.isPaid) PaidGreen else UnpaidRed
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details Breakdown Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), shape = RoundedCornerShape(6.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailCol("Tagihan", CurrencyUtils.formatRupiah(item.tagihan))
                DetailCol("Admin", CurrencyUtils.formatRupiah(item.admin))
                DetailCol("Denda", CurrencyUtils.formatRupiah(item.denda))
                DetailCol("KSU", CurrencyUtils.formatRupiah(item.ksu))
                DetailCol("Total", CurrencyUtils.formatRupiah(item.total), isHighlight = true)
            }
        }
    }
}

@Composable
private fun DetailCol(label: String, value: String, isHighlight: Boolean = false) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
                color = if (isHighlight) PrimaryNavy else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
