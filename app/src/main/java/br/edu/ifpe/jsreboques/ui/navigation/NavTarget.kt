package br.edu.ifpe.jsreboques.ui.navigation

enum class NavTarget(val route: String) {
    PAINEL("painel"),
    REBOQUES("reboques"),
    ESTOQUE("estoque"),
    CLIENTES("clientes"),
    NOVA_VENDA("nova_venda"),
    VENDA_CONCLUIDA("venda_concluida/{vendaId}"),
    NOVA_COMPRA("nova_compra"),
    COMPRA_CONFIRMADA("compra_confirmada/{compraId}"),
    HISTORICO("historico");

    companion object {
        fun createVendaConcluidaRoute(vendaId: Long) = "venda_concluida/$vendaId"
        fun createCompraConfirmadaRoute(compraId: Long) = "compra_confirmada/$compraId"
    }
}
