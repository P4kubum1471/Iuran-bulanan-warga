package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HijriMonth
import com.example.ui.components.AppDrawerContent
import com.example.ui.components.AppTopBar
import com.example.ui.components.DrawerDestination
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DataWargaScreen
import com.example.ui.screens.LaporanExportScreen
import com.example.ui.screens.PembayaranBulananScreen
import com.example.ui.theme.IuranWargaTheme
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IuranWargaTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val kelurahanName by viewModel.kelurahanName.collectAsStateWithLifecycle()
    val availableYears by viewModel.availableYears.collectAsStateWithLifecycle()

    var currentDestination by remember { mutableStateOf<DrawerDestination>(DrawerDestination.Dashboard) }

    val topBarTitle = when (val dest = currentDestination) {
        is DrawerDestination.Dashboard -> "Dashboard Iuran Warga"
        is DrawerDestination.DataWarga -> "Kelola Data Warga"
        is DrawerDestination.Laporan -> "Laporan & Export Data"
        is DrawerDestination.Month -> "Pembayaran Bulan ${dest.monthName}"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentRoute = currentDestination.route,
                kelurahanName = kelurahanName,
                onSelectDestination = { destination ->
                    currentDestination = destination
                    if (destination is DrawerDestination.Month) {
                        viewModel.setSelectedMonth(destination.index)
                    }
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = topBarTitle,
                    kelurahanName = kelurahanName,
                    selectedYear = selectedYear,
                    availableYears = availableYears,
                    onYearSelected = { viewModel.setSelectedYear(it) },
                    onMenuClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val dest = currentDestination) {
                    is DrawerDestination.Dashboard -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToMonth = { monthIndex ->
                                val monthName = HijriMonth.getByIndex(monthIndex).name
                                currentDestination = DrawerDestination.Month(monthIndex, monthName)
                                viewModel.setSelectedMonth(monthIndex)
                            },
                            onNavigateToWarga = {
                                currentDestination = DrawerDestination.DataWarga
                            }
                        )
                    }
                    is DrawerDestination.DataWarga -> {
                        DataWargaScreen(viewModel = viewModel)
                    }
                    is DrawerDestination.Month -> {
                        PembayaranBulananScreen(viewModel = viewModel)
                    }
                    is DrawerDestination.Laporan -> {
                        LaporanExportScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

