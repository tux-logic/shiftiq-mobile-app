package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.customers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text

@Composable
fun CustomerDirectoryRoute(
    onNavigateBack: () -> Unit,
    onCreateAppointment: (branchId: String, customerId: String) -> Unit,
    viewModel: CustomerDirectoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refresh() }
    CustomerDirectoryScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onNavigateBack = onNavigateBack,
        onCreateAppointment = onCreateAppointment
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDirectoryScreen(
    uiState: CustomerDirectoryUiState,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    onCreateAppointment: (branchId: String, customerId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val filteredCustomers = remember(uiState.customers, query) {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isEmpty()) uiState.customers else uiState.customers.filter {
            it.name.lowercase().contains(normalizedQuery) ||
                it.phone.lowercase().contains(normalizedQuery) ||
                it.document.lowercase().contains(normalizedQuery)
        }
    }

    Column(modifier.fillMaxSize()) {
        ShiftIQPageHeader(
            title = "Directorio de clientes",
            subtitle = "Clientes registrados en esta sede",
            showBackButton = true,
            onBackPressed = onNavigateBack
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            label = { Text("Buscar cliente") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true
        )
        Text("${filteredCustomers.size} clientes registrados", modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)

        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
        when {
            uiState.isLoading && uiState.customers.isEmpty() -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator(color = PrimaryBlue) }
            uiState.errorMessage != null && uiState.customers.isEmpty() -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }
            filteredCustomers.isEmpty() -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No se encontraron clientes", fontWeight = FontWeight.Bold)
                Text("Prueba con otro término o revisa los registros de esta sede.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCustomers, key = { it.customerId }) { customer ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                        border = BorderStroke(1.dp, ShiftIQBorderSlate)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            if (customer.phone.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryBlue)
                                    Text(customer.phone)
                                }
                            }
                            if (customer.document.isNotBlank()) Text(customer.document, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Button(
                                onClick = { onCreateAppointment(uiState.branchId, customer.customerId) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Event, contentDescription = null)
                                Text("Nueva cita", modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }
        }
        }
    }
}
