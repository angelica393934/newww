package com.nbs.banking.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nbs.banking.R
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateHomeMvi: () -> Unit,
    onNavigateUserSettings: () -> Unit,
    onNavigateNavDemo: () -> Unit,
    onNavigateNote: () -> Unit,
    onNavigateActivity: () -> Unit,
    onNavigateLongList: () -> Unit,
    onNavigateTransactionList: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { 
                Text(
                    text = stringResource(R.string.title_post_login_dashboard),
                    style = Bsb.Font.heading3,
                    color = Bsb.Color.baseColor.primary1
                ) 
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimension.SIZE_16),
            verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_12, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNavigateHomeMvi,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_home_mvi))
            }
            Button(
                onClick = onNavigateUserSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_user_settings))
            }
            Button(
                onClick = onNavigateNavDemo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_nav_demo))
            }
            Button(
                onClick = onNavigateNote,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_note))
            }
            Button(
                onClick = onNavigateActivity,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_random_activity))
            }
            Button(
                onClick = onNavigateLongList,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_long_list))
            }
            Button(
                onClick = onNavigateTransactionList,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_transaction_list))
            }
        }
    }
}
