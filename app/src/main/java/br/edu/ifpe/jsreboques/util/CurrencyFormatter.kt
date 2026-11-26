package br.edu.ifpe.jsreboques.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val localePtBr = Locale.forLanguageTag("pt-BR")

    fun formatCents(cents: Long): String {
        val formatter = NumberFormat.getCurrencyInstance(localePtBr)
        return formatter.format(cents / 100.0)
    }

    fun parseToCents(input: String): Long {
        val digitsOnly = input.filter { it.isDigit() }
        if (digitsOnly.isEmpty()) return 0L
        return digitsOnly.toLong()
    }
}
