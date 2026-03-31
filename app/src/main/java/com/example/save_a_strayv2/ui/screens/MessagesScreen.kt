package com.example.save_a_strayv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.save_a_strayv2.ui.theme.SaveaStrayV2Theme

/**
 * Placeholder for the messaging / chat screen.
 * Replace this content with the real conversation list UI.
 */
@Composable
fun MessagesScreen() {
    PlaceholderScreen(
        label      = "Messages",
        subtitle   = "Chat Inbox — coming soon",
        icon       = Icons.Filled.Email,
        gradientStart = MaterialTheme.colorScheme.secondary,
        gradientEnd   = MaterialTheme.colorScheme.tertiary
    )
}

@Preview(showBackground = true)
@Composable
private fun MessagesScreenPreview() {
    SaveaStrayV2Theme { MessagesScreen() }
}
