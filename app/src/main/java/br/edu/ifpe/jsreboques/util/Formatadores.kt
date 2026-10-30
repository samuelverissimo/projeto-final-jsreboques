package br.edu.ifpe.jsreboques.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val localeBr: Locale = Locale.forLanguageTag("pt-BR")

/** 489000 -> "4.890,00" */
fun Long.paraNumeroBr(): String =
    NumberFormat.getNumberInstance(localeBr).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(this / 100.0)

/** 489000 -> "R$ 4.890,00" */
fun Long.paraReais(): String = "R$ " + paraNumeroBr()

/** 489000 -> "4890,00" (para preencher campos de edição) */
fun Long.centsParaCampo(): String {
    val reais = this / 100
    val centavos = (this % 100).toString().padStart(2, '0')
    return "$reais,$centavos"
}

/** "4.890,00" | "4890,5" | "4890.50" | "4890" -> centavos (Long). Inválido/<=0 -> null. */
fun String.reaisParaCents(): Long? {
    val t = trim().replace("R$", "").replace(" ", "")
    if (t.isEmpty()) return null
    val normalizado = when {
        t.contains(',') -> t.replace(".", "").replace(',', '.')
        t.count { it == '.' } == 1 && t.substringAfter('.').length <= 2 -> t
        else -> t.replace(".", "")
    }
    val cents = try {
        BigDecimal(normalizado).movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact()
    } catch (e: Exception) {
        null
    }
    return cents?.takeIf { it > 0 }
}

fun String.somenteDigitos(): String = filter { it.isDigit() }

/** Mascara de exibicao para CPF/CNPJ */
fun String.mascararDocumento(): String {
    val d = somenteDigitos()
    val fim = d.takeLast(2)
    return when (d.length) {
        11 -> "***.***.***-$fim"
        14 -> "**.***.***/****-$fim"
        else -> if (d.isEmpty()) "—" else "••••$fim"
    }
}

fun String.ehCnpj(): Boolean = somenteDigitos().length == 14

fun Long.paraDataHora(): String = SimpleDateFormat("dd MMM • HH:mm", localeBr).format(Date(this))

fun Long.paraDataCompleta(): String = SimpleDateFormat("dd MMM yyyy", localeBr).format(Date(this))

fun mesAnoAtual(): String =
    SimpleDateFormat("MMMM 'de' yyyy", localeBr).format(Date()).replaceFirstChar { it.uppercase() }

fun dataPorExtenso(): String =
    SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", localeBr).format(Date())
        .replaceFirstChar { it.uppercase() }

fun inicioDoMes(): Long = Calendar.getInstance().apply {
    set(Calendar.DAY_OF_MONTH, 1)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

fun String.iniciais(): String =
    split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
