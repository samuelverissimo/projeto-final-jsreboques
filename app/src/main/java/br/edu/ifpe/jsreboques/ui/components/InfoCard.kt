package br.edu.ifpe.jsreboques.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.ui.theme.ErrorCardBg
import br.edu.ifpe.jsreboques.ui.theme.ErrorCardText
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBg
import br.edu.ifpe.jsreboques.ui.theme.InfoCardBorder
import br.edu.ifpe.jsreboques.ui.theme.InfoCardText
import br.edu.ifpe.jsreboques.ui.theme.SuccessCardBg
import br.edu.ifpe.jsreboques.ui.theme.SuccessCardText
import br.edu.ifpe.jsreboques.ui.theme.WarningCardBg
import br.edu.ifpe.jsreboques.ui.theme.WarningCardText

enum class InfoCardType {
    INFO, WARNING, ERROR, SUCCESS
}

@Composable
fun InfoCard(
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    type: InfoCardType = InfoCardType.INFO
) {
    val (bgColor, textColor, borderColor, icon) = when (type) {
        InfoCardType.INFO -> InfoCardColors(InfoCardBg, InfoCardText, InfoCardBorder, Icons.Outlined.Info)
        InfoCardType.WARNING -> InfoCardColors(WarningCardBg, WarningCardText, Color.Transparent, Icons.Outlined.Warning)
        InfoCardType.ERROR -> InfoCardColors(ErrorCardBg, ErrorCardText, Color.Transparent, Icons.Outlined.Warning)
        InfoCardType.SUCCESS -> InfoCardColors(SuccessCardBg, SuccessCardText, Color.Transparent, Icons.Outlined.CheckCircle)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(16.dp))
                } else Modifier
            )
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            if (!title.isNullOrEmpty()) {
                Text(
                    text = title,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = message,
                color = textColor,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

private data class InfoCardColors(
    val bg: Color,
    val text: Color,
    val border: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
