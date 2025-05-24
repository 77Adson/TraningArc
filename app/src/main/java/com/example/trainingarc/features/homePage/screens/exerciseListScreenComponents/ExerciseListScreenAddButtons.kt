package com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.trainingarc.R
import com.example.trainingarc.ui.theme.sizes


@Composable
fun FloatingAddButton(
    onPrimaryClick: () -> Unit,
    onCreateClick: () -> Unit,
    onAddExistingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var addButtonColor = MaterialTheme.colorScheme.primary
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (expanded) {
            addButtonColor = MaterialTheme.colorScheme.surface

            // Create New Button
            ExpandedButton(
                text = "Create New",
                icon = R.drawable.ic_new_squere,
                contentDescription = "Create New",
                onClick = {
                    onCreateClick()
                    expanded = false // Collapse after click
                },
                // No need to pass expanded here, as its visibility is controlled by the `if (expanded)` block
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )

            // Add Existing Button
            ExpandedButton(
                text = "Add Existing",
                icon = R.drawable.ic_library,
                contentDescription = "Add existing",
                onClick = {
                    onAddExistingClick()
                    expanded = false // Collapse after click
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
                // No need to pass expanded here
            )


        }

        // Main FAB
        FloatingActionButton(
            onClick = {
                expanded = !expanded
                if (!expanded) { // If we are collapsing, onPrimaryClick might not be desired
                    // Or, always call it, depending on desired UX:
                    onPrimaryClick()
                } else {
                    // If expanding, perhaps onPrimaryClick is only for the main action
                    // and not for just revealing other buttons.
                    // Consider if onPrimaryClick should always be called or only when collapsing.
                    // For now, let's assume it's for the main action or toggling.
                    onPrimaryClick()
                }
            },
            containerColor = addButtonColor
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add exercise",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun ExpandedButton(
    text: String,
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit, // Renamed from onCreateClick for clarity, and it now handles collapsing
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface, // Default to surface or another appropriate color
    contentColor: Color = MaterialTheme.colorScheme.onSurface // Default content color
){
    FloatingActionButton(
        onClick = onClick, // Call the passed-in lambda which will handle both the action and collapsing
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier
            .padding(bottom = 8.dp) // Apply modifier here
            .height(MaterialTheme.sizes.components.smallCardHeight)
            .width(MaterialTheme.sizes.components.cardWidth)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp) // Add some padding for text and icon
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.sizes.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                painter = painterResource(id = icon),
                contentDescription = contentDescription,
                modifier = Modifier.size(MaterialTheme.sizes.icons.medium)
                )
                Text(text)
            }
        }
    }
}