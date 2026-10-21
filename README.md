# JS Reboques — Gerenciamento de Vendas e Estoque

Este aplicativo foi desenvolvido para gerenciar o estoque e as vendas da **JS Reboques**. Ele utiliza uma arquitetura offline-first com banco de dados Room para garantir a integridade dos dados mesmo sem conexão.

## 🚀 Funcionalidades Principais

- **Gestão de Vendas (Implementado por João Pedro):**
    - Registro de vendas vinculado a Clientes e Reboques.
    - Validação automática de estoque (não permite vender o que não há em estoque).
    - Histórico detalhado de movimentações.
    - Suporte a valores monetários em centavos para evitar erros de precisão.
- **Controle de Estoque:** Monitoramento em tempo real de entradas (compras) e saídas (vendas).
- **Cadastro de Clientes:** Base de dados local para agilizar o processo de venda.

## 🛠️ Tecnologias Utilizadas

- **Kotlin** com Jetpack Compose para a interface.
- **Room Database** para persistência local.
- **Navigation Compose** para fluxo entre telas.
- **Coroutines & Flow** para operações assíncronas e reativas.
