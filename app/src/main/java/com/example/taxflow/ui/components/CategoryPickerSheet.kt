package com.example.taxflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.shared2.domain.model.Category

/**
 * Ersetzt die horizontale Chip-Leiste: bei >5 Kategorien wird eine scrollbare
 * Zeile schnell unpräzise (Optionen außerhalb des Viewports werden übersehen).
 * Ein Bottom Sheet mit vollständiger, klar lesbarer Liste skaliert deutlich besser.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Text(
            "Kategorie wählen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        LazyColumn(modifier = Modifier.padding(bottom = 24.dp)) {
            items(categories) { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCategorySelected(category.id)
                            onDismiss()
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(parseCategoryColor(category.colorHex), CircleShape)
                        )
                        Text(
                            category.name,
                            modifier = Modifier.padding(start = 12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (category.id == selectedCategoryId) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

private fun parseCategoryColor(hex: String): Color = try {
    Color(hex.toColorInt())
} catch (e: Exception) {
    Color.Gray
}