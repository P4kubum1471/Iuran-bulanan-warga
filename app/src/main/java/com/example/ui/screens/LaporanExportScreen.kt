package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HijriMonth
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal
import com.example.ui.viewmodel.MainViewModel
import com.example.util.ReportUtils

@Composable
fun LaporanExportScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val citizens by viewModel.allCitizens.collectAsStateWithLifecycle()
    val paymentRecords by viewModel.allPaymentRecords.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val selectedMonthIndex by viewModel.selectedMonthIndex.collectAsStateWithLifecycle()
    val paymentItems by viewModel.currentMonthPaymentItems.collectAsStateWithLifecycle()
    val kelurahanName by viewModel.kelurahanName.collectAsStateWithLifecycle()

    val currentMonthObj = HijriMonth.getByIndex(selectedMonthIndex)

    var showResetDialog by remember { mutableStateOf(false) }
    var showKelurahanDialog by remember { mutableStateOf(false) }
    var kelurahanInput by remember { mutableStateOf(kelurahanName) }
    var showReportPreviewDialog by remember { mutableStateOf<Pair<String, String>?>(null) } // Title to Text

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Laporan, Export & Pengaturan Data",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryNavy
            )
        )

        // Card 0: Setting Nama Kelurahan
        OptionCard(
            title = "Nama Kelurahan / Wilayah",
            description = "Saat ini: \"$kelurahanName\". Nama ini dicantumkan pada header laporan dan cetakan penagihan iuran.",
            icon = Icons.Default.LocationOn,
            buttonText = "Ubah Nama Kelurahan",
            onButtonClick = {
                kelurahanInput = kelurahanName
                showKelurahanDialog = true
            }
        )

        // Card 1: Export Data ke Excel / CSV
        OptionCard(
            title = "Export Data ke Excel (.csv)",
            description = "Unduh seluruh data warga dan histori pembayaran 12 bulan Hijriyah ($selectedYear H) ke dalam format spreadsheet Excel.",
            icon = Icons.Default.FileDownload,
            buttonText = "Export Excel (.csv)",
            onButtonClick = {
                val csvContent = ReportUtils.generateCsvExport(citizens, paymentRecords, selectedYear, kelurahanName)
                ReportUtils.shareText(
                    context = context,
                    title = "Data_Iuran_Warga_Hijriah_$selectedYear.csv",
                    content = csvContent
                )
            }
        )

        // Card 2: Cetak Laporan Bulanan
        OptionCard(
            title = "Cetak Laporan Bulanan",
            description = "Buat laporan penagihan iuran resmi untuk Bulan ${currentMonthObj.name} $selectedYear H ($kelurahanName) beserta statistik penerimaan.",
            icon = Icons.Default.Print,
            buttonText = "Lihat & Bagikan Laporan Bulanan",
            onButtonClick = {
                val reportText = ReportUtils.generateMonthlyPrintText(currentMonthObj.name, selectedYear, paymentItems, kelurahanName)
                showReportPreviewDialog = Pair("Laporan Bulan ${currentMonthObj.name} $selectedYear H", reportText)
            }
        )

        // Card 3: Cetak Laporan Tahunan
        OptionCard(
            title = "Cetak Laporan Tahunan",
            description = "Buat laporan rekapitulasi penagihan iuran selama satu tahun penuh ($selectedYear H) untuk seluruh 12 bulan Hijriyah.",
            icon = Icons.Default.Assessment,
            buttonText = "Lihat & Bagikan Laporan Tahunan",
            onButtonClick = {
                val reportText = ReportUtils.generateYearlyPrintText(selectedYear, citizens, paymentRecords, kelurahanName)
                showReportPreviewDialog = Pair("Laporan Tahunan $selectedYear H", reportText)
            }
        )

        // Card 4: Restore Sample Data
        OptionCard(
            title = "Muat Data Contoh (Demo)",
            description = "Isi ulang aplikasi dengan data contoh warga dan histori iuran untuk keperluan demo atau pengujian.",
            icon = Icons.Default.Restore,
            buttonText = "Muat Data Contoh",
            onButtonClick = {
                viewModel.restoreSampleData()
                Toast.makeText(context, "Data contoh berhasil dimuat!", Toast.LENGTH_SHORT).show()
            }
        )

        // Card 5: Danger Zone - Reset Data
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Reset Seluruh Data",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                }
                Text(
                    text = "Tindakan ini akan menghapus seluruh data warga dan seluruh catatan pembayaran iuran secara permanen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Data")
                }
            }
        }
    }

    // Dialog Preview Laporan
    if (showReportPreviewDialog != null) {
        val (title, text) = showReportPreviewDialog!!
        AlertDialog(
            onDismissRequest = { showReportPreviewDialog = null },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        ReportUtils.shareText(context, title, text)
                        showReportPreviewDialog = null
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bagikan / Cetak")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportPreviewDialog = null }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Dialog Edit Kelurahan Name
    if (showKelurahanDialog) {
        AlertDialog(
            onDismissRequest = { showKelurahanDialog = false },
            title = { Text("Ubah Nama Kelurahan") },
            text = {
                Column {
                    Text("Masukkan nama kelurahan / wilayah administrasi tanpa mencantumkan RT/RW:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = kelurahanInput,
                        onValueChange = { kelurahanInput = it },
                        label = { Text("Nama Kelurahan") },
                        placeholder = { Text("Contoh: Kelurahan Sukamaju") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (kelurahanInput.isNotBlank()) {
                            viewModel.setKelurahanName(kelurahanInput)
                            showKelurahanDialog = false
                            Toast.makeText(context, "Nama kelurahan diperbarui", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showKelurahanDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Konfirmasi Reset Data") },
            text = {
                Text("Apakah Anda benar-benar yakin ingin menghapus SELURUH data warga dan histori pembayaran iuran?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetData()
                        showResetDialog = false
                        Toast.makeText(context, "Seluruh data berhasil direset", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Hapus Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun OptionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(PrimaryNavy.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = PrimaryNavy)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(buttonText)
            }
        }
    }
}
