package com.nbs.banking.navdemo.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenC(
    count: Int,
    onBack: () -> Unit,
    onBackToA: () -> Unit,
    onOpenNewC: () -> Unit,
    onNextToD: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Screen C (Instance #$count)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Screen C (#$count)",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onOpenNewC,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open New Screen C")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onNextToD,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next to Screen D")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onBackToA,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Screen A")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}
