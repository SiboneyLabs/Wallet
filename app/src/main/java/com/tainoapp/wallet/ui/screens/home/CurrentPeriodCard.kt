package com.tainoapp.wallet.ui.screens.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tainoapp.wallet.util.DateUtils

@Composable
fun CurrentPeriodCard(currentPeriodKey: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = DateUtils.parsePeriodKey(currentPeriodKey),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
