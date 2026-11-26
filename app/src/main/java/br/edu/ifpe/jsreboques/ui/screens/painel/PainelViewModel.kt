package br.edu.ifpe.jsreboques.ui.screens.painel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.ifpe.jsreboques.data.local.relation.ReboqueComEstoque
import br.edu.ifpe.jsreboques.data.repository.JsRepository
import br.edu.ifpe.jsreboques.util.DateFormatter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class PainelUiState(
    val greeting: String = "Bom dia, João",
    val currentDateFormatted: String = "",
    val monthYearUpper: String = "",
    val totalVendasMesCents: Long = 0L,
    val quantidadeVendasMes: Int = 0,
    val quantidadeUnidadesVendidasMes: Int = 0,
    val totalEmEstoque: Int = 0,
    val totalModelosCadastrados: Int = 0,
    val totalAlertaEstoqueBaixo: Int = 0,
    val quantidadeBaixo: Int = 0,
    val quantidadeZerado: Int = 0,
    val reboquesAtencao: List<ReboqueComEstoque> = emptyList()
)

class PainelViewModel(private val repository: JsRepository) : ViewModel() {

    val uiState: StateFlow<PainelUiState> = combine(
        repository.vendasComDetalhes,
        repository.reboquesComEstoque
    ) { vendas, reboques ->
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..11 -> "Bom dia, João"
            in 12..17 -> "Boa tarde, João"
            else -> "Boa noite, João"
        }

        val dateFormatted = DateFormatter.formatFullDateWithWeekday(now)
        val monthYearUpper = DateFormatter.formatMonthYearUpper(now)

        val totalVendasMes = vendas.sumOf { it.venda.valorTotalCents }
        val countVendasMes = vendas.size
        val countUnidadesVendidasMes = vendas.sumOf { it.venda.quantidade }

        val totalEstoque = reboques.sumOf { it.quantidadeEstoque }
        val modelosCount = reboques.size

        val reboquesZerados = reboques.filter { it.quantidadeEstoque == 0 }
        val reboquesBaixo = reboques.filter { it.quantidadeEstoque in 1..2 }
        val alertCount = reboquesZerados.size + reboquesBaixo.size

        val atencaoList = reboques.filter { it.quantidadeEstoque <= 2 }

        PainelUiState(
            greeting = greeting,
            currentDateFormatted = dateFormatted,
            monthYearUpper = monthYearUpper,
            totalVendasMesCents = totalVendasMes,
            quantidadeVendasMes = countVendasMes,
            quantidadeUnidadesVendidasMes = countUnidadesVendidasMes,
            totalEmEstoque = totalEstoque,
            totalModelosCadastrados = modelosCount,
            totalAlertaEstoqueBaixo = alertCount,
            quantidadeBaixo = reboquesBaixo.size,
            quantidadeZerado = reboquesZerados.size,
            reboquesAtencao = atencaoList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PainelUiState()
    )

    class Factory(private val repository: JsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PainelViewModel(repository) as T
        }
    }
}
