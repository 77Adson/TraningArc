package com.example.trainingarc.features.homePage.screens.homeScreenComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trainingarc.ui.theme.sizes

@Composable
fun TrainingSessionCard(
    sessionName: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(MaterialTheme.sizes.components.cardHeight)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        SessionCardContent(
            sessionName = sessionName,
        )
    }
}

// Content of the card
@Composable
private fun SessionCardContent(
    sessionName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        SessionCardHeader(
            name = sessionName,
        )
        // Add more sections here like SessionCardFooter if needed
    }
}

@Composable
private fun SessionCardHeader(
    name: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
//            .background(MaterialTheme.colorScheme.error),
        verticalAlignment = Alignment.Bottom
    ) {
        SessionTitle(
            name = name,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SessionTitle(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface
    )
}
