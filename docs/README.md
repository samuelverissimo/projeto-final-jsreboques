<img width="1280" height="720" alt="jsreboques-logo (2)" src="https://github.com/user-attachments/assets/428e09aa-5c60-4733-8d52-ac3b423057a8" />

# JS Reboques — Gerenciamento de Vendas e Estoque
Projeto final da matéria de Desenvolvimento para Dispositivos Móveis, ministrada pelo docente Hélio Bentzen. 
Projeto desenvolvido pela equipe **MaineProg**, composto pelos integrantes: **Samuel Veríssimo, Pedro Henrique, João Pedro e João Pietro**.

Postulantes na turma B do 3º ano do ensino médio.

---

Link do Figma: https://www.figma.com/design/SnhULfcoE15uEXtuwAe9NK/Untitled?node-id=0-1&t=WjCEubKgeaSU7BgW-1

Este aplicativo foi desenvolvido para gerenciar o estoque e as vendas da **JS Reboques**. Ele utiliza uma arquitetura offline-first com banco de dados Room e padronização dos **Quatro Estados da Tela** (`Carregando`, `Vazio`, `Erro` e `Sucesso`) no Jetpack Compose.

---

## 👥 Divisão de Responsabilidades da Equipe (MaineProg)

- **João Pedro**:
  - **Gestão de Vendas & Operações**: Registro de vendas vinculado a Clientes e Reboques, validação de estoque em tempo real (não permite vender o que não há em estoque), histórico detalhado de movimentações e suporte a valores em centavos.
- **Pedro Henrique**:
  - **Clientes & Integração de Rede (API / Retrofit)**: Camada de rede para a entidade Cliente (API, DTO, Mappers), sincronização remota, formatação e mascaramento de CPF/CNPJ e WhatsApp.
- **João Pietro**:
  - **Compras & Fornecedores**: Registro de compras de reboques com incremento automático de estoque, cadastro e gestão de fornecedores e garantia de integridade referencial com Foreign Keys no Room.
- **Samuel Veríssimo**:
  - **Reboques, Estoque, Tarefas & Arquitetura dos Quatro Estados da Tela**: Implementação da `UiState` selada, gerenciamento de ViewModels para Reboques, Estoque e Tarefas, criação do componente `MensagemCentral` e garantia do tratamento exaustivo no Compose sem cláusulas `else`.

---

## 🚀 Funcionalidades Principais

- **Gestão de Vendas**: Registro de vendas com baixa automática de saldo.
- **Controle de Estoque**: Monitoramento em tempo real de entradas (compras), saídas (vendas) e ajustes manuais.
- **Cadastro de Clientes e Fornecedores**: Base de dados local e integrada via API Retrofit.
- **Acompanhamento de Tarefas**: Gestão de pendências e entregas da fábrica.
- **Arquitetura Resiliente**: Tratamento dos 4 estados da tela em todas as visões (`Carregando`, `Vazio`, `Erro` e `Sucesso`), evitando telas em branco ou congelamentos.

---

## 🛠️ Tecnologias Utilizadas

- **Kotlin** com Jetpack Compose para a interface reativa.
- **Room Database** para persistência local e transações atômicas (`@Transaction`).
- **Retrofit 2 & OkHttp** para comunicação com APIs RESTful.
- **Navigation Compose** para fluxo entre telas.
- **Coroutines & Flow** para estados assíncronos e reativos via `StateFlow` e `collectAsStateWithLifecycle`.
