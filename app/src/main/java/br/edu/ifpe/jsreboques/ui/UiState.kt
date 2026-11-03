// O que faz: Define os quatro estados da tela (Carregando, Vazio, Erro e Sucesso) para padronização no Jetpack Compose.
// Por que existe: Exigido na Atividade 07 para garantir tratamento exaustivo de UI no ViewModel e na camada de apresentação.
// O que quebra se sumir: As telas e os ViewModels perdem a estrutura unificada de estados, gerando telas em branco e inconsistências.

package br.edu.ifpe.jsreboques.ui

/**
 * Interface selada que representa os quatro estados da tela.
 * Carregando: busca em andamento.
 * Vazio: busca concluída sem registros.
 * Erro: falha ao carregar dados.
 * Sucesso: busca com sucesso trazendo a lista de itens.
 */
sealed interface UiState<out T> {
    data object Carregando : UiState<Nothing>
    data object Vazio : UiState<Nothing>
    data class Erro(val mensagem: String) : UiState<Nothing>
    data class Sucesso<T>(val itens: List<T>) : UiState<T>
}
