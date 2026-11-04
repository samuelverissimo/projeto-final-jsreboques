package br.edu.ifpe.jsreboques.util

sealed interface UiState<out T> {
    data object Carregando : UiState<Nothing>
    data class Sucesso<T>(val dados: T) : UiState<T>
    data class Erro(val mensagem: String) : UiState<Nothing>
    data object Vazio : UiState<Nothing>
}
