package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    kelurahanName: String,
    selectedYear: Int,
    availableYears: List<Int>,
    onYearSelected: (Int) -> Unit,
    onMenuClick: () -> Unit,
    onRefreshClick: (() -> Unit)? = null
) {
    var showYearMenu by remember { mutableStateOf(false) }
    var showCustomYearDialog by remember { mutableStateOf(false) }
    var customYearInput by remember { mutableStateOf("") }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = kelurahanName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondaryTeal,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Navigasi"
                )
            }
        },
        actions = {
            // Year Selector Chip
            Box {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { showYearMenu = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = SecondaryTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$selectedYear H",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showYearMenu,
                    onDismissRequest = { showYearMenu = false }
                ) {
                    Text(
                        text = "Pilih Tahun Hijriyah:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = PrimaryNavy
                    )
                    HorizontalDivider()
                    availableYears.forEach { yr ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Tahun $yr H",
                                    fontWeight = if (yr == selectedYear) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onYearSelected(yr)
                                showYearMenu = false
                            }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "+ Tambah Tahun Lain...",
                                style = MaterialTheme.typography.labelMedium.copy(color = SecondaryTeal, fontWeight = FontWeight.Bold)
                            )
                        },
                        onClick = {
                            showYearMenu = false
                            showCustomYearDialog = true
                        }
                    )
                }
            }

            if (onRefreshClick != null) {
                IconButton(onClick = onRefreshClick) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Muat Ulang"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryNavy,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )

    if (showCustomYearDialog) {
        AlertDialog(
            onDismissRequest = { showCustomYearDialog = false },
            title = { Text("Tambah Tahun Hijriyah") },
            text = {
                Column {
                    Text("Masukkan angka tahun Hijriyah yang ingin Anda kelola:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customYearInput,
                        onValueChange = { customYearInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Tahun Hijriyah (misal: 1447)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val yearInt = customYearInput.toIntOrNull()
                        if (yearInt != null && yearInt > 1300 && yearInt < 1700) {
                            onYearSelected(yearInt)
                            showCustomYearDialog = false
                            customYearInput = ""
                        }
                    }
                ) {
                    Text("Ganti Tahun")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomYearDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

