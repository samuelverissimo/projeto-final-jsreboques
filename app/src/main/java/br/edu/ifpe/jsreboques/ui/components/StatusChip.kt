package br.edu.ifpe.jsreboques.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.ui.theme.StatusBaixoBg
import br.edu.ifpe.jsreboques.ui.theme.StatusBaixoDot
import br.edu.ifpe.jsreboques.ui.theme.StatusBaixoText
import br.edu.ifpe.jsreboques.ui.theme.StatusNormalBg
import br.edu.ifpe.jsreboques.ui.theme.StatusNormalDot
import br.edu.ifpe.jsreboques.ui.theme.StatusNormalText
import br.edu.ifpe.jsreboques.ui.theme.StatusZeroBg
import br.edu.ifpe.jsreboques.ui.theme.StatusZeroDot
import br.edu.ifpe.jsreboques.ui.theme.StatusZeroText

enum class StatusEstoqueType {
    NORMAL, BAIXO, ZERO
}

fun getStatusType(quantidade: Int): StatusEstoqueType {
    return when {
        quantidade >= 3 -> StatusEstoqueType.NORMAL
        quantidade in 1..2 -> StatusEstoqueType.BAIXO
        else -> StatusEstoqueType.ZERO
    }
}

@Composable
fun StatusChip(
    quantidade: Int,
    modifier: Modifier = Modifier,
    showQuantity: Boolean = true
) {
    val statusType = getStatusType(quantidade)
    val (bgColor, textColor, dotColor, statusLabel) = when (statusType) {
        StatusEstoqueType.NORMAL -> Quadruple(
            StatusNormalBg,
            StatusNormalText,
            StatusNormalDot,
            "Normal"
        )
        StatusEstoqueType.BAIXO -> Quadruple(
            StatusBaixoBg,
            StatusBaixoText,
            StatusBaixoDot,
            "Baixo"
        )
        StatusEstoqueType.ZERO -> Quadruple(
            StatusZeroBg,
            StatusZeroText,
            StatusZeroDot,
            "Zero"
        )
    }

    val labelText = if (showQuantity) {
        "$quantidade un. • $statusLabel"
    } else {
        statusLabel
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = labelText,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
