package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Citizen
import com.example.ui.components.AddEditCitizenDialog
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DataWargaScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val citizens by viewModel.filteredCitizens.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingCitizen by remember { mutableStateOf<Citizen?>(null) }
    var deletingCitizen by remember { mutableStateOf<Citizen?>(null) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Tambah Warga") },
                containerColor = PrimaryNavy,
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Cari nama, blok rumah, atau HP warga...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )

            // Header info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Warga (${citizens.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryNavy
                )
                Text(
                    text = "Otomatis sinkron ke 12 bulan",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryTeal
                )
            }

            if (citizens.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PeopleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Warga tidak ditemukan" else "Belum ada data warga",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(citizens, key = { it.id }) { citizen ->
                        CitizenItemCard(
                            citizen = citizen,
                            onEdit = { editingCitizen = citizen },
                            onDelete = { deletingCitizen = citizen }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        AddEditCitizenDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, houseNumber, phone, notes ->
                viewModel.addCitizen(name, houseNumber, phone, notes)
                showAddDialog = false
            }
        )
    }

    if (editingCitizen != null) {
        AddEditCitizenDialog(
            citizen = editingCitizen,
            onDismiss = { editingCitizen = null },
            onConfirm = { name, houseNumber, phone, notes ->
                editingCitizen?.let { current ->
                    viewModel.updateCitizen(
                        current.copy(
                            name = name,
                            houseNumber = houseNumber,
                            phone = phone,
                            notes = notes
                        )
                    )
                }
                editingCitizen = null
            }
        )
    }

    if (deletingCitizen != null) {
        AlertDialog(
            onDismissRequest = { deletingCitizen = null },
            title = { Text("Hapus Data Warga?") },
            text = {
                Text("Menghapus '${deletingCitizen?.name}' akan secara otomatis menghapus seluruh catatan iuran bulanan milik warga ini di semua bulan Hijriyah.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        deletingCitizen?.let { viewModel.deleteCitizen(it.id) }
                        deletingCitizen = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus Warga")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingCitizen = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun CitizenItemCard(
    citizen: Citizen,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryNavy.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryNavy
                    )
                }

                Column {
                    Text(
                        text = citizen.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryNavy
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (citizen.houseNumber.isNotBlank()) {
                            Text(
                                text = citizen.houseNumber,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = SecondaryTeal
                            )
                        }
                        if (citizen.phone.isNotBlank()) {
                            Text(
                                text = "• ${citizen.phone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryNavy)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
