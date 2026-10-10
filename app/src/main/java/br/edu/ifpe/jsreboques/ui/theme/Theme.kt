package br.edu.ifpe.jsreboques.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulPrimario,
    secondary = AzulPrimario,
    secondaryContainer = AzulClaro,
    onSecondaryContainer = AzulPrimario,
    background = Fundo,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = AzulSuave,
    onSurfaceVariant = TextoSecundario,
    outline = Borda,
    outlineVariant = Borda,
    error = VermelhoTexto,
    errorContainer = VermelhoFundo
)

// O design usa só o tema claro. Para adicionar modo escuro depois, crie um EsquemaEscuro aqui.
@Composable
fun JsReboquesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        content = content
    )
}
