package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.HijriMonth
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal
import com.example.ui.viewmodel.MainViewModel
import com.example.util.CurrencyUtils

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToMonth: (Int) -> Unit,
    onNavigateToWarga: () -> Unit,
    modifier: Modifier = Modifier
) {
    val citizens by viewModel.allCitizens.collectAsStateWithLifecycle()
    val paymentRecords by viewModel.allPaymentRecords.collectAsStateWithLifecycle()
    val currentYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val kelurahanName by viewModel.kelurahanName.collectAsStateWithLifecycle()
    val availableYears by viewModel.availableYears.collectAsStateWithLifecycle()

    val totalCitizens = citizens.size
    val totalYearlyIncome = paymentRecords.filter { it.year == currentYear }.sumOf { it.total }
    val totalPaidTransactions = paymentRecords.filter { it.year == currentYear && it.isPaid }.size

    var showYearDialog by remember { mutableStateOf(false) }
    var customYearInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Hero Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hijri_banner),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.35f
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = kelurahanName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = SecondaryTeal,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        // Year Switcher Chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.clickable { showYearDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "$currentYear H",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Ganti Tahun",
                                    tint = SecondaryTeal,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    Column {
                        Text(
                            text = CurrencyUtils.formatRupiah(totalYearlyIncome),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Total Pemasukan Setahun ($currentYear H)",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                        )
                    }
                }
            }
        }

        // Summary Statistics Cards Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Warga",
                value = "$totalCitizens Orang",
                icon = Icons.Default.People,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToWarga() }
            )
            StatCard(
                title = "Transaksi Bayar",
                value = "$totalPaidTransactions Transaksi",
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }

        // Section Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Iuran Per Bulan Hijriyah",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = PrimaryNavy
            )
            Text(
                text = "12 Bulan",
                style = MaterialTheme.typography.labelSmall.copy(color = SecondaryTeal)
            )
        }

        // 12 Hijri Month Cards Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(HijriMonth.ALL_MONTHS) { month ->
                val monthRecords = paymentRecords.filter { it.monthIndex == month.index && it.year == currentYear }
                val paidCount = monthRecords.count { it.isPaid }
                val totalCollected = monthRecords.sumOf { it.total }

                MonthSummaryCard(
                    month = month,
                    paidCount = paidCount,
                    totalCitizens = totalCitizens,
                    totalCollected = totalCollected,
                    onClick = { onNavigateToMonth(month.index) }
                )
            }
        }
    }

    if (showYearDialog) {
        AlertDialog(
            onDismissRequest = { showYearDialog = false },
            title = { Text("Pilih / Ganti Tahun Hijriyah") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pilih tahun Hijriyah yang ingin Anda kelola penagihannya:")

                    availableYears.chunked(3).forEach { yearRow ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            yearRow.forEach { year ->
                                FilterChip(
                                    selected = (year == currentYear),
                                    onClick = {
                                        viewModel.setSelectedYear(year)
                                        showYearDialog = false
                                    },
                                    label = { Text("$year H") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryNavy,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Atau masukkan tahun baru:", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = customYearInput,
                        onValueChange = { customYearInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Tahun Hijriyah (Misal: 1447)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val yearInt = customYearInput.toIntOrNull()
                        if (yearInt != null && yearInt in 1300..1700) {
                            viewModel.setSelectedYear(yearInt)
                            showYearDialog = false
                            customYearInput = ""
                        }
                    }
                ) {
                    Text("Terapkan Tahun")
                }
            },
            dismissButton = {
                TextButton(onClick = { showYearDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SecondaryTeal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = SecondaryTeal)
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryNavy
                )
            }
        }
    }
}

@Composable
private fun MonthSummaryCard(
    month: HijriMonth,
    paidCount: Int,
    totalCitizens: Int,
    totalCollected: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryNavy)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${month.index}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Text(
                    text = month.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryNavy
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sudah bayar: $paidCount / $totalCitizens org",
                style = MaterialTheme.typography.bodySmall,
                color = if (paidCount > 0) PaidGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = CurrencyUtils.formatRupiah(totalCollected),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = SecondaryTeal
            )

            Spacer(modifier = Modifier.height(8.dp))

            val progress = if (totalCitizens > 0) paidCount.toFloat() / totalCitizens else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = SecondaryTeal,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
