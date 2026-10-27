package com.nbs.banking.components.atom.input

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.R
import com.nbs.banking.components.theme.primitives.Dimension

class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.filter { it.isDigit() }
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if ((i + 1) % 4 == 0 && i != trimmed.lastIndex) out += " "
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                return offset + (offset - 1) / 4
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                return offset - (offset / 5)
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

@Composable
fun BsbPhoneInputField(
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
    BsbInputField(
        value = value,
        onValueChange = { newValue ->
            // Filter input to only allow digits
            val digitsOnly = newValue.filter { it.isDigit() }
            onValueChange(digitsOnly)
        },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        isError = isError,
        errorMessage = errorMessage,
        enabled = enabled,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_phone),
                contentDescription = null,
                modifier = Modifier.size(Dimension.SIZE_24),
                tint = Color.Unspecified
            )
        },
        visualTransformation = PhoneNumberVisualTransformation(),
        // Force the keyboard to numeric while preserving other passed options (like imeAction)
        keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Number),
        keyboardActions = keyboardActions
    )
}

@Preview
@Composable
private fun BsbPhoneInputFieldPreview() {
    Surface(modifier = Modifier.padding(Dimension.SIZE_16)) {
        Column {
            var phone1 by remember { mutableStateOf("") }
            BsbPhoneInputField(
                value = phone1,
                onValueChange = { phone1 = it },
                label = "Phone Number",
                placeholder = "Input nomor handphone",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimension.SIZE_16))

            var phone2 by remember { mutableStateOf("081211117298") }
            BsbPhoneInputField(
                value = phone2,
                onValueChange = { phone2 = it },
                label = "Phone Number (Error)",
                isError = true,
                errorMessage = "Phone number is invalid",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
