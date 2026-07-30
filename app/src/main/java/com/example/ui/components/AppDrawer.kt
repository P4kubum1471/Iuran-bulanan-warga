package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.HijriMonth
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal

sealed class DrawerDestination(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : DrawerDestination("dashboard", "Dashboard", Icons.Default.Dashboard)
    object DataWarga : DrawerDestination("data_warga", "Data Warga", Icons.Default.People)
    object Laporan : DrawerDestination("laporan", "Laporan & Export", Icons.Default.Assessment)
    data class Month(val index: Int, val monthName: String) : DrawerDestination("month_$index", monthName, Icons.Default.CalendarMonth)
}

@Composable
fun AppDrawerContent(
    currentRoute: String,
    kelurahanName: String,
    onSelectDestination: (DrawerDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(PrimaryNavy)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hijri_banner),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.4f
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Iuran Warga Hijriah",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = kelurahanName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SecondaryTeal,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Menu
            Text(
                text = "UTAMA",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            NavigationDrawerItem(
                label = { Text("Dashboard") },
                selected = currentRoute == DrawerDestination.Dashboard.route,
                onClick = { onSelectDestination(DrawerDestination.Dashboard) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            NavigationDrawerItem(
                label = { Text("Data Warga") },
                selected = currentRoute == DrawerDestination.DataWarga.route,
                onClick = { onSelectDestination(DrawerDestination.DataWarga) },
                icon = { Icon(Icons.Default.People, contentDescription = null) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp))

            // Hijri Months Section
            Text(
                text = "BULAN HIJRIYAH",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            HijriMonth.ALL_MONTHS.forEach { month ->
                val monthDest = DrawerDestination.Month(month.index, month.name)
                NavigationDrawerItem(
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${month.index}. ",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = SecondaryTeal
                            )
                            Text(text = month.name)
                        }
                    },
                    selected = currentRoute == monthDest.route,
                    onClick = { onSelectDestination(monthDest) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (currentRoute == monthDest.route) SecondaryTeal else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp))

            // Report & Settings
            Text(
                text = "LAPORAN & OPSI",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            NavigationDrawerItem(
                label = { Text("Laporan & Export") },
                selected = currentRoute == DrawerDestination.Laporan.route,
                onClick = { onSelectDestination(DrawerDestination.Laporan) },
                icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
