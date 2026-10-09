package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.branch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQErrorBanner
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPrimaryButton
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme

@Composable
fun BranchSelectionScreen(
    uiState: BranchSelectionUiState,
    onBranchSelected: (String) -> Unit,
    onConfirmClick: () -> Unit,
    onConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(uiState.isConfirmed) {
        if (uiState.isConfirmed) {
            onConfirmed()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Seleccionar Sucursal",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = "Elige la sede o taller en la que operarás durante esta sesión de trabajo.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                if (uiState.errorMessage != null) {
                    ShiftIQErrorBanner(
                        message = uiState.errorMessage,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.branches) { branch ->
                        val isSelected = branch.id == uiState.selectedBranchId
                        BranchCard(
                            branch = branch,
                            isSelected = isSelected,
                            onSelect = { onBranchSelected(branch.id) }
                        )
                    }
                }
            }

            ShiftIQPrimaryButton(
                text = "Ingresar a esta Sucursal",
                onClick = onConfirmClick,
                isLoading = uiState.isLoading,
                enabled = uiState.selectedBranchId != null,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun BranchCard(
    branch: BranchItemUi,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = branch.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = branch.address,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Preview(name = "Branch Selection Screen", showBackground = true)
@Composable
private fun BranchSelectionScreenPreview() {
    ShiftIQTheme {
        BranchSelectionScreen(
            uiState = BranchSelectionUiState(),
            onBranchSelected = {},
            onConfirmClick = {},
            onConfirmed = {}
        )
    }
}
