package com.nbs.banking.membership.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.atom.button.BsbButton
import com.nbs.banking.components.atom.button.BsbButtonVariant
import com.nbs.banking.components.atom.input.BsbInputField
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimension.SIZE_24),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Login Screen", style = Bsb.Font.heading1, color = Bsb.Color.baseColor.primary1)
            Spacer(modifier = Modifier.height(Dimension.SIZE_32))
            
            BsbInputField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                placeholder = "Enter username",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Dimension.SIZE_16))
            
            BsbInputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter password",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Dimension.SIZE_32))
            
            BsbButton(
                text = "Login",
                onClick = onLoginClick,
                variant = BsbButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun VerificationSuccessScreen(
    onContinueClick: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimension.SIZE_24),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Verification Success!", style = Bsb.Font.heading1, color = Bsb.Color.baseColor.primary1)
            Spacer(modifier = Modifier.height(Dimension.SIZE_16))
            Text(text = "Your account has been verified.", style = Bsb.Font.text1, color = Bsb.Color.baseColor.gray700)
            Spacer(modifier = Modifier.height(Dimension.SIZE_32))
            BsbButton(
                text = "Continue to App",
                onClick = onContinueClick,
                variant = BsbButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview(){
    LoginScreen {  }
}

@Preview
@Composable
private fun VerificationSuccessScreenPreview(){
    VerificationSuccessScreen {  }
}