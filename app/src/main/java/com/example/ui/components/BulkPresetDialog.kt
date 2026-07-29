package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.util.CurrencyUtils

@Composable
fun BulkPresetDialog(
    monthName: String,
    onDismiss: () -> Unit,
    onConfirm: (tagihan: Double, admin: Double, ksu: Double) -> Unit
) {
    var tagihanText by remember { mutableStateOf("50000") }
    var adminText by remember { mutableStateOf("5000") }
    var ksuText by remember { mutableStateOf("10000") }

    val tagihanVal = CurrencyUtils.parseRupiahInput(tagihanText)
    val adminVal = CurrencyUtils.parseRupiahInput(adminText)
    val ksuVal = CurrencyUtils.parseRupiahInput(ksuText)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Isi Tagihan Standard Massal",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Terapkan nominal iuran standard berikut ke seluruh data warga untuk Bulan $monthName:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = tagihanText,
                    onValueChange = { tagihanText = it.filter { c -> c.isDigit() } },
                    label = { Text("Standard Tagihan") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = adminText,
                    onValueChange = { adminText = it.filter { c -> c.isDigit() } },
                    label = { Text("Standard Admin") },
                    prefix = { Text("Rp ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = ksuText,
                    onValueChange = { ksuText = it.filter { c -> c.isDigit() } },
                    label = { Text("Standard KSU") },
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
                    onConfirm(tagihanVal, adminVal, ksuVal)
                }
            ) {
                Text("Terapkan Ke Semua Warga")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
