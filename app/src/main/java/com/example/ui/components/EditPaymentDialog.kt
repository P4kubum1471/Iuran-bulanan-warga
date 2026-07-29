package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.CitizenPaymentItem
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal
import com.example.util.CurrencyUtils

@Composable
fun EditPaymentDialog(
    item: CitizenPaymentItem,
    monthName: String,
    onDismiss: () -> Unit,
    onSave: (tagihan: Double, admin: Double, denda: Double, ksu: Double) -> Unit
) {
    var tagihanText by remember { mutableStateOf(item.tagihan.toLong().toString()) }
    var adminText by remember { mutableStateOf(item.admin.toLong().toString()) }
    var dendaText by remember { mutableStateOf(item.denda.toLong().toString()) }
    var ksuText by remember { mutableStateOf(item.ksu.toLong().toString()) }

    val tagihanVal = CurrencyUtils.parseRupiahInput(tagihanText)
    val adminVal = CurrencyUtils.parseRupiahInput(adminText)
    val dendaVal = CurrencyUtils.parseRupiahInput(dendaText)
    val ksuVal = CurrencyUtils.parseRupiahInput(ksuText)

    val calculatedTotal = tagihanVal + adminVal + dendaVal + ksuVal

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Input Pembayaran",
                    style = MaterialTheme.typography.labelLarge.copy(color = SecondaryTeal)
                )
                Text(
                    text = item.citizen.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Bulan $monthName",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Calculated total banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryNavy.copy(alpha = 0.08f))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL BAYAR",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryNavy
                        )
                        Text(
                            text = CurrencyUtils.formatRupiah(calculatedTotal),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (calculatedTotal > 0) PaidGreen else PrimaryNavy
                        )
                    }
                }

                OutlinedTextField(
                    value = tagihanText,
                    onValueChange = { tagihanText = it.filter { c -> c.isDigit() } },
                    label = { Text("Tagihan (Iuran Utama)") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = adminText,
                    onValueChange = { adminText = it.filter { c -> c.isDigit() } },
                    label = { Text("Biaya Admin") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dendaText,
                    onValueChange = { dendaText = it.filter { c -> c.isDigit() } },
                    label = { Text("Denda Keterlambatan") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = ksuText,
                    onValueChange = { ksuText = it.filter { c -> c.isDigit() } },
                    label = { Text("KSU / Simpanan") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(tagihanVal, adminVal, dendaVal, ksuVal)
                }
            ) {
                Text("Simpan Pembayaran")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
