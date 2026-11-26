package br.edu.ifpe.jsreboques.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary

@Composable
fun FooterNote(
    modifier: Modifier = Modifier,
    text: String = "Ambiente demonstrativo • Dados simulados"
) {
    Text(
        text = text,
        color = JsTextSecondary,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    )
}
