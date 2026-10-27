package com.nbs.banking.components.atom.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.R
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

@Composable
fun BsbInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                modifier = Modifier.padding(start = Dimension.SIZE_20),
                text = label,
                style = Bsb.Font.caption2,
                color = if (isError) Bsb.Color.baseColor.red500 else Bsb.Color.baseColor.gray950
            )
            Spacer(modifier = Modifier.height(Dimension.SIZE_4))
        }

        val colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Bsb.Color.baseColor.gray950,
            unfocusedTextColor = Bsb.Color.baseColor.gray950,
            disabledTextColor = Bsb.Color.baseColor.gray200,

            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Bsb.Color.baseColor.gray50,

            focusedBorderColor = Bsb.Color.baseColor.primary2,
            unfocusedBorderColor = Bsb.Color.baseColor.gray300,
            disabledBorderColor = Bsb.Color.baseColor.gray50,
            errorBorderColor = Bsb.Color.baseColor.red500,

            cursorColor = Bsb.Color.baseColor.primary2,
            errorCursorColor = Bsb.Color.baseColor.red500,
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            textStyle = Bsb.Font.text6.copy(
                color = if (enabled) Bsb.Color.baseColor.gray950 else Bsb.Color.baseColor.gray200
            ),
            interactionSource = interactionSource,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            decorationBox = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = if (placeholder != null) {
                        {
                            Text(
                                text = placeholder,
                                style = Bsb.Font.text5,
                                color = Bsb.Color.baseColor.gray200
                            )
                        }
                    } else null,
                    leadingIcon = leadingIcon?.let { icon ->
                        {
                            Box(modifier = Modifier.padding(start = Dimension.SIZE_12)) {
                                icon()
                            }
                        }
                    },
                    trailingIcon = trailingIcon?.let { icon ->
                        {
                            Box(modifier = Modifier.padding(end = Dimension.SIZE_12)) {
                                icon()
                            }
                        }
                    },
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    contentPadding = PaddingValues(
                        horizontal = Dimension.SIZE_20,
                        vertical = Dimension.SIZE_16
                    ),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = enabled,
                            isError = isError,
                            interactionSource = interactionSource,
                            colors = colors,
                            shape = RoundedCornerShape(Dimension.SIZE_100)
                        )
                    }
                )
            }
        )

        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(Dimension.SIZE_4))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = errorMessage,
                style = Bsb.Font.text7,
                color = Bsb.Color.baseColor.red500,
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview
@Composable
private fun BsbInputFieldPreview() {
    Surface(modifier = Modifier.padding(Dimension.SIZE_16)) {
        Column {
            var text1 by remember { mutableStateOf("") }
            BsbInputField(
                value = text1,
                onValueChange = { text1 = it },
                placeholder = "Enter your username",
                label = "Username",
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = null,
                        modifier = Modifier.size(Dimension.SIZE_24),
                        tint = Color.Unspecified
                    )
                }
            )

            Spacer(modifier = Modifier.height(Dimension.SIZE_16))

            var text2 by remember { mutableStateOf("wrong_password") }
            BsbInputField(
                value = text2,
                onValueChange = { text2 = it },
                label = "Password",
                placeholder = "Enter your password",
                isError = true,
                errorMessage = "Incorrect password"
            )

            Spacer(modifier = Modifier.height(Dimension.SIZE_16))

            BsbInputField(
                value = "Disabled text",
                onValueChange = { },
                label = "Disabled Field",
                enabled = false
            )
        }
    }
}
