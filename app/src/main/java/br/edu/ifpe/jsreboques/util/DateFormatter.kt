package br.edu.ifpe.jsreboques.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val localePtBr = Locale.forLanguageTag("pt-BR")

    fun formatShortDate(timestamp: Long): String {
        // Ex: "07 out. • 16:20"
        val sdf = SimpleDateFormat("dd MMM • HH:mm", localePtBr)
        return sdf.format(Date(timestamp))
    }

    fun formatShortDayAndMonth(timestamp: Long): String {
        // Ex: "08 out. 2026"
        val sdf = SimpleDateFormat("dd MMM yyyy", localePtBr)
        return sdf.format(Date(timestamp))
    }

    fun formatFullDateWithWeekday(timestamp: Long): String {
        // Ex: "Quinta-feira, 08 de outubro de 2026"
        val sdf = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", localePtBr)
        val result = sdf.format(Date(timestamp))
        return result.replaceFirstChar { if (it.isLowerCase()) it.titlecase(localePtBr) else it.toString() }
    }

    fun formatMonthYearUpper(timestamp: Long): String {
        // Ex: "OUTUBRO / 2026"
        val sdf = SimpleDateFormat("MMMM / yyyy", localePtBr)
        return sdf.format(Date(timestamp)).uppercase(localePtBr)
    }

    fun formatMonthYearTitle(timestamp: Long): String {
        // Ex: "Outubro de 2026"
        val sdf = SimpleDateFormat("MMMM 'de' yyyy", localePtBr)
        val result = sdf.format(Date(timestamp))
        return result.replaceFirstChar { if (it.isLowerCase()) it.titlecase(localePtBr) else it.toString() }
    }
}
