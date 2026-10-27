package com.nbs.banking.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

enum class BsbSavingInfoCardVariant {
    BALANCE,
    SAVING,
    COMPACT,
    ACCOUNT
}

data class BsbSavingInfoCardIcons(
    @DrawableRes val copy: Int? = null,
    @DrawableRes val visible: Int? = null,
    @DrawableRes val hidden: Int? = null,
    @DrawableRes val arrowRight: Int? = null,
    @DrawableRes val selected: Int? = null,
    @DrawableRes val decoration: Int? = null
)

@Composable
fun BsbSavingInfoCard(
    accountNumber: String,
    balance: String = "",
    modifier: Modifier = Modifier,
    variant: BsbSavingInfoCardVariant = BsbSavingInfoCardVariant.BALANCE,
    accountName: String = "",
    accountType: String = "Tabungan",
    highlighted: Boolean = false,
    balanceVisible: Boolean = true,
    isDisplayed: Boolean = true,
    selected: Boolean = false,
    hiddenBalanceSymbol: String = "●●●●●●●",
    currencyPrefix: String = "Rp ",
    icons: BsbSavingInfoCardIcons = BsbSavingInfoCardIcons(),
    onCopyClick: (() -> Unit)? = null,
    onBalanceVisibilityChange: ((Boolean) -> Unit)? = null,
    onOtherAccountsClick: (() -> Unit)? = null,
    onDetailClick: (() -> Unit)? = null,
    onCardClick: (() -> Unit)? = null
){
    val colors = Bsb.Color.baseColor
    var isBalanceVisible by rememberSaveable(accountNumber, balanceVisible) {
        mutableStateOf(balanceVisible)
    }
    val accent = colors.primary2
    val background = when {
        variant == BsbSavingInfoCardVariant.ACCOUNT && !isDisplayed -> colors.gray50
        highlighted -> colors.primary7
        else -> Color.White
    }
    val border = when (variant) {
        BsbSavingInfoCardVariant.BALANCE -> BorderStroke(Dimension.SIZE_1, accent)
        BsbSavingInfoCardVariant.SAVING -> BorderStroke(
            Dimension.SIZE_1,
            if (highlighted) accent else colors.gray300
        )
        BsbSavingInfoCardVariant.COMPACT -> BorderStroke(
            Dimension.SIZE_1,
            if (highlighted) accent else colors.gray100
        )
        BsbSavingInfoCardVariant.ACCOUNT -> if (isDisplayed) {
            BorderStroke(Dimension.SIZE_1, colors.gray300)
        } else {
            null
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onCardClick != null) {
                    Modifier.clickable(role = Role.Button, onClick = onCardClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(Dimension.SIZE_20),
        color = background,
        border = border
    ) {
        when (variant){
            BsbSavingInfoCardVariant.BALANCE -> {
                Column(
                    modifier = Modifier.padding(Dimension.SIZE_16),
                    verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_12)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stringResource(R.string.label_account)} $accountNumber",
                            modifier = Modifier.weight(1f),
                            style = Bsb.Font.text4,
                            color = colors.gray950,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        BsbSavingInfoCardAction(
                            text = stringResource(R.string.action_copy),
                            iconRes = icons.copy,
                            onClick = onCopyClick,
                            textColor = colors.gray400,
                            iconColor = colors.gray400,
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_4)) {
                        Text(
                            text = stringResource(R.string.label_current_balance),
                            style = Bsb.Font.text1,
                            color = colors.gray300
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isBalanceVisible) {
                                Text(
                                    text = balance,
                                    modifier = Modifier.weight(1f),
                                    style = Bsb.Font.heading1,
                                    color = colors.primary1,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = colors.gray950)) {
                                            append(currencyPrefix)
                                        }
                                        withStyle(SpanStyle(color = colors.primary1)) {
                                            append(hiddenBalanceSymbol)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    style = Bsb.Font.heading1,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            val visibilityIcon = if (isBalanceVisible) icons.visible else icons.hidden
                            if (visibilityIcon != null) {
                                Icon(
                                    painter = painterResource(id = visibilityIcon),
                                    contentDescription = if (isBalanceVisible) {
                                        stringResource(R.string.text_hide_balance)
                                    } else {
                                        stringResource(R.string.tampilkan_saldo)
                                    },
                                    modifier = Modifier
                                        .size(Dimension.SIZE_24)
                                        .clickable(role = Role.Button) {
                                            isBalanceVisible = !isBalanceVisible
                                            onBalanceVisibilityChange?.invoke(isBalanceVisible)
                                        },
                                    tint = colors.gray300
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        thickness = Dimension.SIZE_1,
                        color = colors.gray300
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = accountType,
                            style = Bsb.Font.text6,
                            color = colors.gray300
                        )
                        BsbSavingInfoCardAction(
                            text = stringResource(R.string.other_account),
                            iconRes = icons.arrowRight,
                            iconTrailing = true,
                            onClick = onOtherAccountsClick,
                            textColor = colors.primary1,
                            iconColor = colors.primary1
                        )
                    }
                }
            }

            BsbSavingInfoCardVariant.SAVING -> {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (icons.decoration != null) {
                        Image(
                            painter = painterResource(id = icons.decoration),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop,
                            colorFilter = if (highlighted) {
                                ColorFilter.tint(colors.primary7, BlendMode.Multiply)
                            } else {
                                null
                            }
                        )
                    }


                    Column(
                        modifier = Modifier.padding(Dimension.SIZE_16),
                        verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_12)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.label_current_saving),
                                modifier = Modifier.weight(1f),
                                style = Bsb.Font.text3,
                                color = colors.gray500
                            )
                            Text(
                                text = accountType,
                                style = Bsb.Font.text3,
                                color = colors.gray950
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_4)
                            ) {
                                Text(
                                    text = balance,
                                    style = Bsb.Font.heading2,
                                    color = colors.gray950,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = accountNumber,
                                    style = Bsb.Font.text4,
                                    color = colors.gray500
                                )
                            }
                            BsbSavingInfoCardAction(
                                text = stringResource(R.string.action_copy),
                                iconRes = icons.copy,
                                onClick = onCopyClick,
                                textColor = colors.primary1,
                                iconColor = colors.primary1
                            )
                        }
                    }
                }
            }

            BsbSavingInfoCardVariant.COMPACT -> {
                Column(
                    modifier = Modifier.padding(Dimension.SIZE_16),
                    verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_4)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.label_current_balance),
                            modifier = Modifier.weight(1f),
                            style = Bsb.Font.text4,
                            color = colors.gray950
                        )
                        Text(
                            text = accountType,
                            style = Bsb.Font.text3,
                            color = colors.primary1
                        )
                    }
                    Text(
                        text = balance,
                        style = Bsb.Font.text3,
                        color = colors.gray950,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = listOfNotNull(
                            accountName.takeIf { it.isNotBlank() },
                            accountNumber
                        ).joinToString(" - "),
                        style = Bsb.Font.caption1,
                        color = colors.gray700,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            BsbSavingInfoCardVariant.ACCOUNT -> {
                Column(
                    modifier = Modifier.padding(Dimension.SIZE_16),
                    verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_8)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = accountName,
                            modifier = Modifier.weight(1f),
                            style = Bsb.Font.text3,
                            color = colors.gray950,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = accountType,
                            style = Bsb.Font.text6,
                            color = colors.gray500
                        )
                    }

                    Text(
                        text = accountNumber,
                        style = Bsb.Font.text1,
                        color = colors.gray950
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimension.SIZE_8),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(Dimension.SIZE_12)
                                .background(
                                    color = if (isDisplayed) colors.green500 else colors.orange500,
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = if (isDisplayed) stringResource(R.string.action_show) else stringResource(R.string.action_hide),
                            style = Bsb.Font.button1,
                            color = if (isDisplayed) colors.green600 else colors.orange500
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selected && icons.selected != null) {
                            Icon(
                                painter = painterResource(id = icons.selected),
                                contentDescription = stringResource(R.string.label_choosen_account),
                                modifier = Modifier.size(Dimension.SIZE_24),
                                tint = Color.Unspecified
                            )
                        } else {
                            Box(modifier = Modifier.size(Dimension.SIZE_24))
                        }
                        BsbSavingInfoCardAction(
                            text = stringResource(R.string.action_see_detail),
                            iconRes = icons.arrowRight,
                            iconTrailing = true,
                            onClick = onDetailClick,
                            textColor = colors.primary1,
                            iconColor = colors.primary1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BsbSavingInfoCardAction(
    text: String,
    @DrawableRes iconRes: Int?,
    onClick: (() -> Unit)?,
    textColor: Color,
    iconColor: Color,
    iconTrailing: Boolean = false
) {
    val actionModifier = if (onClick != null) {
        Modifier.clickable(role = Role.Button, onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = actionModifier,
        horizontalArrangement = Arrangement.spacedBy(Dimension.SIZE_8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!iconTrailing && iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(Dimension.SIZE_20),
                tint = iconColor
            )
        }
        Text(
            text = text,
            style = Bsb.Font.button2,
            color = textColor
        )
        if (iconTrailing && iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(Dimension.SIZE_20),
                tint = iconColor
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 430, heightDp = 1340)
@Composable
private fun BsbSavingInfoCardPreview() {
    val icons = BsbSavingInfoCardIcons(
        copy = R.drawable.ic_copy,
        visible = R.drawable.ic_visibility,
        hidden = R.drawable.ic_visibility_off,
        arrowRight = R.drawable.ic_arrow_forward,
        selected = R.drawable.ic_check_circle,
        decoration = R.drawable.bg_saving_decoration
    )

    Surface {
        Column(
            modifier = Modifier.padding(Dimension.SIZE_16),
            verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_24)
        ) {
            BsbSavingInfoCard(
                accountNumber = "198887777",
                balance = "Rp 1.245.000,00",
                icons = icons
            )

            BsbSavingInfoCard(
                accountNumber = "198887777",
                balance = "Rp 1.245.000,00",
                balanceVisible = false,
                icons = icons
            )

            BsbSavingInfoCard(
                accountNumber = "198887777",
                balance = "Rp 1.000.000,00",
                variant = BsbSavingInfoCardVariant.SAVING,
                icons = icons
            )

            BsbSavingInfoCard(
                accountNumber = "198887777",
                balance = "Rp 1.000.000,00",
                variant = BsbSavingInfoCardVariant.SAVING,
                highlighted = true,
                icons = icons
            )

            BsbSavingInfoCard(
                accountNumber = "198887777",
                balance = "Rp 1.000.000,00",
                accountName = "ROBET DAVIS CHANIAGO",
                variant = BsbSavingInfoCardVariant.COMPACT,
                icons = icons
            )

            BsbSavingInfoCard(
                accountNumber = "198887777",
                balance = "Rp 1.000.000,00",
                accountName = "ROBET DAVIS CHANIAGO",
                variant = BsbSavingInfoCardVariant.COMPACT,
                highlighted = true,
                icons = icons
            )

            BsbSavingInfoCard(
                accountNumber = "198887777",
                accountName = "ROBERT DAVIS CHANIAGO",
                variant = BsbSavingInfoCardVariant.ACCOUNT,
                selected = true,
                icons = icons
            )

            BsbSavingInfoCard(
                accountNumber = "198887777",
                accountName = "ROBERT DAVIS CHANIAGO",
                variant = BsbSavingInfoCardVariant.ACCOUNT,
                isDisplayed = false,
                icons = icons
            )
        }
    }
}