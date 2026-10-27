package com.nbs.banking.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

enum class BsbValidationRuleState {
    DEFAULT,
    PASSED,
    FAILED
}

data class BsbValidationRule(
    val text: String,
    val condition: (String) -> Boolean
)

@Composable
fun BsbValidationRules(
    value: String,
    title: String,
    rules: List<BsbValidationRule>,
    onAllValid: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAllValid = if (value.isEmpty()) false else rules.all { it.condition(value) }

    LaunchedEffect(value, isAllValid) {
        onAllValid(isAllValid)
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = Bsb.Font.caption3,
                color = Bsb.Color.baseColor.gray400
            )
            Spacer(modifier = Modifier.height(Dimension.SIZE_10))
        }

        rules.forEach { rule ->
            val state = if (value.isEmpty()) {
                BsbValidationRuleState.DEFAULT
            } else if (rule.condition(value)) {
                BsbValidationRuleState.PASSED
            } else {
                BsbValidationRuleState.FAILED
            }

            BsbValidationRuleItem(text = rule.text, state = state)
            Spacer(modifier = Modifier.height(Dimension.SIZE_10))
        }
    }
}

@Composable
private fun BsbValidationRuleItem(
    text: String,
    state: BsbValidationRuleState
) {
    val (iconRes, textColor) = when (state) {
        BsbValidationRuleState.DEFAULT -> Pair(
            R.drawable.ic_rules_default,
            Bsb.Color.baseColor.gray950
        )
        BsbValidationRuleState.PASSED -> Pair(
            R.drawable.ic_rules_passed,
            Bsb.Color.baseColor.green700
        )
        BsbValidationRuleState.FAILED -> Pair(
            R.drawable.ic_rules_failed,
            Bsb.Color.baseColor.red500
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(Dimension.SIZE_6))
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(Dimension.SIZE_12)
        )
        Spacer(modifier = Modifier.width(Dimension.SIZE_4))
        Text(
            text = text,
            style = Bsb.Font.caption2,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BsbValidationRulesPreview() {
    val rules = listOf(
        BsbValidationRule(
            text = "Gunakan maksimal 8 karakter",
            condition = { it.length <= 8 }
        ),
        BsbValidationRule(
            text = "Gunakan huruf besar dan kecil",
            condition = { it.any { char -> char.isUpperCase() } && it.any { char -> char.isLowerCase() } }
        ),
        BsbValidationRule(
            text = "Sertakan angka",
            condition = { it.any { char -> char.isDigit() } }
        )
    )

    Column(modifier = Modifier.padding(Dimension.SIZE_16)) {
        BsbValidationRules(
            value = "",
            title = "Aturan ID Pengguna (Default)",
            rules = rules,
            onAllValid = {}
        )
        Spacer(modifier = Modifier.height(Dimension.SIZE_24))
        BsbValidationRules(
            value = "Abc1234",
            title = "Aturan ID Pengguna (Passed)",
            rules = rules,
            onAllValid = {}
        )
        Spacer(modifier = Modifier.height(Dimension.SIZE_24))
        BsbValidationRules(
            value = "abcdefgh",
            title = "Aturan ID Pengguna (Failed)",
            rules = rules,
            onAllValid = {}
        )
    }
}
