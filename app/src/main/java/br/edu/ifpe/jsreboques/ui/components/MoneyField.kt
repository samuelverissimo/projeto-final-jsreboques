package br.edu.ifpe.jsreboques.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.edu.ifpe.jsreboques.ui.theme.JsBorder
import br.edu.ifpe.jsreboques.ui.theme.JsPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextPrimary
import br.edu.ifpe.jsreboques.ui.theme.JsTextSecondary

@Composable
fun MoneyField(
    valueCents: Long,
    onValueCentsChange: (Long) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val formattedText = if (valueCents == 0L) "" else (valueCents / 100.0).let {
        "%.2f".format(it).replace(".", ",")
    }

    OutlinedTextField(
        value = formattedText,
        onValueChange = { input ->
            val cleanDigits = input.filter { it.isDigit() }
            val newCents = if (cleanDigits.isEmpty()) 0L else cleanDigits.toLong()
            onValueCentsChange(newCents)
        },
        label = { Text(text = label) },
        placeholder = { Text(text = "0,00") },
        prefix = { Text(text = "R$ ") },
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(text = errorMessage) }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JsPrimary,
            unfocusedBorderColor = JsBorder,
            focusedTextColor = JsTextPrimary,
            unfocusedTextColor = JsTextPrimary,
            focusedLabelColor = JsPrimary,
            unfocusedLabelColor = JsTextSecondary
        ),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}
