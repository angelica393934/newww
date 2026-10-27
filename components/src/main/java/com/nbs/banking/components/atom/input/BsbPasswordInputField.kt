package com.nbs.banking.components.atom.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.R
import com.nbs.banking.components.theme.primitives.Dimension

@Composable
fun BsbPasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    BsbInputField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        isError = isError,
        errorMessage = errorMessage,
        enabled = enabled,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = null,
                modifier = Modifier.size(Dimension.SIZE_24),
                tint = Color.Unspecified
            )
        },
        trailingIcon = {
            val image = if (passwordVisible) {
                R.drawable.ic_visibility_off
            } else {
                R.drawable.ic_visibility
            }

            Icon(
                painter = painterResource(id = image),
                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                modifier = Modifier
                    .size(Dimension.SIZE_24)
                    .clickable(enabled = enabled) {
                        passwordVisible = !passwordVisible
                    },
                tint = Color.Unspecified
            )
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Password),
        keyboardActions = keyboardActions
    )
}

@Preview
@Composable
private fun BsbPasswordInputFieldPreview() {
    Surface(modifier = Modifier.padding(Dimension.SIZE_16)) {
        Column {
            var password by remember { mutableStateOf("") }
            BsbPasswordInputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter your password",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimension.SIZE_16))

            var passwordError by remember { mutableStateOf("wrong_password") }
            BsbPasswordInputField(
                value = passwordError,
                onValueChange = { passwordError = it },
                label = "Password (Error)",
                placeholder = "Enter your password",
                isError = true,
                errorMessage = "Incorrect password",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Dimension.SIZE_16))
            
            BsbPasswordInputField(
                value = "disabled_password",
                onValueChange = { },
                label = "Password (Disabled)",
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}