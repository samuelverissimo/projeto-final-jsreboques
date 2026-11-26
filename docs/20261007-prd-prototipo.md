# 🚀 Product Requirement Document (PRD) — JS Reboques

---

## 1. Visão Geral do Produto

* **Nome do Produto:** JS Reboques
* **Pacote (`applicationId`):** `br.edu.ifpe.jsreboques`
* **Versão Inicial:** 1.0 (versionCode 1)
* **Equipe (MaineProg):** João Pedro, João Pietro, Pedro Henrique e Samuel Veríssimo
* **Prazo Final:** 10/12/2026

### Pitch

O **JS Reboques** é um aplicativo Android focado em centralizar e simplificar a gestão operacional de lojas de reboques. Ele permite controle completo sobre catálogo de produtos, estoque, fluxo de compras, registro de vendas e gestão de clientes em uma única plataforma local com sincronização em nuvem.

---

## 2. Problema & Oportunidade

Lojas de reboques costumam gerenciar estoque, fornecedores, clientes e histórico de vendas em papéis, planilhas avulsas ou de forma descentralizada. Isso ocasiona:

* Erros e divergências de estoque;
* Dificuldade no acompanhamento do histórico de operações financeiras/comerciais;
* Lentidão no atendimento ao cliente final.

**A Solução:** Centralizar as operações diárias da loja em um aplicativo móvel rápido, intuitivo e persistente localmente (Offline-First).

---

## 3. Arquitetura Técnica & Stack Tecnológica

Atendendo estritamente às diretrizes e restrições estabelecidas, a arquitetura adota o padrão **MVVM com Offline-First**, utilizando o Room como *Single Source of Truth* (SSOT).

```
                 +---------------------------------------------+
                 |            UI Layer (Jetpack Compose)       |
                 | - LazyColumn (Listagens)                    |
                 | - Componentes de Tela Única / Formulários   |
                 +----------------------+----------------------+
                                        |
                                        v
                 +---------------------------------------------+
                 |               ViewModel Layer               |
                 | - Exposição de UI State (StateFlow)         |
                 +----------------------+----------------------+
                                        |
                                        v
                 +---------------------------------------------+
                 |              Repository Layer               |
                 | - Single Source of Truth (SSOT)             |
                 | - Regras de negócio e orquestração de dados  |
                 +-------------------+-------------------------+
                                     |
              +----------------------+----------------------+
              |                                             |
              v                                             v
+---------------------------+                 +---------------------------+
|    Room Database (Local)  | <--- Sync ----- |     Retrofit API Client   |
| - Tabela Reboques         |                 | - BaseURL / Service API   |
| - Tabela Clientes         |                 | - Endpoints REST          |
| - Tabela Vendas           |                 +---------------------------+
| - Tabela Compras          |
| - Tabela Estoque          |
| - Tabela Usuários         |
+---------------------------+
```

### Tecnologias Permitidas

* **Linguagem:** Kotlin
* **Arquitetura:** MVVM (Model-View-ViewModel) + Repository Pattern
* **UI Framework:** Jetpack Compose (utilizando `LazyColumn` com `key` para listas)
* **Persistência Local:** Room Database (com suporte a Foreign Keys e Transactions)
* **Comunicação de Rede:** Retrofit + OkHttp
* **Estilização / Identidade Visual:**
  * Cor Primária: `#213D5E` (Azul Escuro)
  * Cor Complementar: `#BEDEFF` (Azul Claro)

---

## 4. Estrutura do Banco de Dados (Room Entities)

> **Nota de Boas Práticas:** Valores monetários são armazenados em centavos (`Long`) para evitar problemas de precisão flutuante (`Double`). As datas são armazenadas em *timestamps* (epoch millis) para facilitar ordenação, filtragem e formatação.

```kotlin
// 1. Entity: Reboque
@Entity(tableName = "reboques")
data class ReboqueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val marca: String,
    val modelo: String,
    val categoria: String,
    val descricao: String = "",
    val valorCompraCents: Long, // valor em centavos (ex: R$ 150,00 -> 15000)
    val valorVendaCents: Long
)

// 2. Entity: Estoque (ou integrado em ReboqueEntity)
@Entity(
    tableName = "estoques",
    foreignKeys = [
        ForeignKey(
            entity = ReboqueEntity::class,
            parentColumns = ["id"],
            childColumns = ["reboqueId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EstoqueEntity(
    @PrimaryKey val reboqueId: Long,
    val quantidade: Int
)

// 3. Entity: Cliente
@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val email: String,
    val whatsapp: String,
    val documento: String = ""
)

// 4. Entity: Venda
@Entity(
    tableName = "vendas",
    foreignKeys = [
        ForeignKey(
            entity = ReboqueEntity::class,
            parentColumns = ["id"],
            childColumns = ["reboqueId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("reboqueId"), Index("clienteId")]
)
data class VendaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reboqueId: Long,
    val clienteId: Long,
    val quantidade: Int,
    val valorUnitarioCents: Long,
    val valorTotalCents: Long,
    val dataHoraTimestamp: Long = System.currentTimeMillis()
)

// 5. Entity: Compra
@Entity(
    tableName = "compras",
    foreignKeys = [
        ForeignKey(
            entity = ReboqueEntity::class,
            parentColumns = ["id"],
            childColumns = ["reboqueId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("reboqueId")]
)
data class CompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reboqueId: Long,
    val quantidade: Int,
    val valorUnitarioCents: Long,
    val valorTotalCents: Long,
    val dataHoraTimestamp: Long = System.currentTimeMillis()
)

// 6. Entity: Usuario
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val username: String,
    val passwordHash: String,
    val perfil: String // Ex: "ADMIN", "OPERADOR"
)
```

---

## 5. Especificação de Funcionalidades (MVP)

| ID | Funcionalidade | Descrição | Componente UI Principal | Tecnologia |
| --- | --- | --- | --- | --- |
| **F1** | Cadastro e gestão de reboques | CRUD completo de reboques (Nome, Valor Compra/Venda, Categoria, Modelo, Marca). | Form + `LazyColumn` para listagem | Room / Retrofit / MVVM |
| **F2** | Controle de estoque | Consulta de saldos e controle automático de entradas e saídas. | Card Dashboard + `LazyColumn` | Room / MVVM |
| **F3** | Registro de vendas | Seleção de reboque, quantidade e cliente. Atualiza estoque automaticamente. | Form Venda + `LazyColumn` histórico | Form Venda + `LazyColumn` histórico | Room / MVVM |
| **F4** | Registro de compras | Entrada de reboques comprados. Incrementa quantidade em estoque. | Form Compra + `LazyColumn` histórico | Room / MVVM |
| **F5** | Cadastro de clientes | Cadastro e listagem de clientes para vínculo com vendas. | Form Cliente + `LazyColumn` | Room / Retrofit / MVVM |
| **F6** | Painel / Tela Inicial | Centralizador do aplicativo em formato de atalhos e resumo. | Dashboard Grid/List | Jetpack Compose / MVVM |
| **F7** | Login do Administrador *(Opcional)* | Validação simples de usuário administrador com senha criptografada. | Screen Login | Room / MVVM |
| **F8** | Histórico de Operações *(Opcional)* | Listagem do histórico de transações de compra e venda. | `LazyColumn` filtrável | Room / MVVM |

---

## 6. Regras de Negócio Críticas & Tratamento de Erros

1. **Validação de Estoque em Vendas:**
   * O sistema deve impedir qualquer tentativa de registro de venda cuja quantidade solicitada seja maior do que a quantidade disponível em estoque para o reboque.

2. **Atualização do Estoque (Transacional):**
   * Registrar Compra → Incrementa estoque (`quantidadeEstoque + quantidadeComprada`).
   * Registrar Venda → Decrementa estoque (`quantidadeEstoque - quantidadeVendida`).
   * **Atomicidade:** As operações de atualização de estoque e criação de registro (Venda/Compra) devem rodar dentro de uma anotação `@Transaction` no Room DAO.

3. **Resiliência e Tratamento de Exceções (`try/catch`):**
   * Nenhuma falha de operação no banco ou rede pode causar o fechamento (*crash*) do app ou tela branca.
   * Em caso de falha capturada via `try/catch`, apresentar a mensagem padrão obrigatória ao usuário:
   > *"Não foi possível realizar a operação. Tente novamente."*

---

## 7. Escopo & Limites do Projeto

### Fora do Escopo ❌

* Processamento de pagamentos em tempo real (gateway de cartão/PIX).
* Venda direta online para consumidores finais (e-commerce B2C).
* Chat ou comunicação em tempo real entre usuários.
* Rastreamento ou gestão de entregas/logística via GPS.
* Integração com leitores de código de barras externos ou POS físico.
* Suporte a múltiplas lojas, franquias ou filiais descentralizadas.

---

## 8. Guia de Mitigação de Riscos para Desenvolvedores Iniciantes

Dado o perfil da equipe (desenvolvedores iniciantes), as seguintes medidas de proteção técnica devem ser seguidas estritamente:

1. **Padronização do Padrão MVVM:**
   * A UI (Compose) só escuta o `StateFlow` exposto pelo `ViewModel`.
   * Toda lógica de validação fica centralizada no `ViewModel` ou no `Repository`.

2. **Prevenção de Modificações Concorrentes no Banco (Room):**
   * Realizar operações de escrita/atualização usando `@Transaction` no DAO para garantir atomicidade no estoque ao vender/comprar.

3. **Uso Padronizado do `LazyColumn`:**
   * Utilizar a chave única (`key = { it.id }`) nos itens de `LazyColumn` para otimizar o desempenho de re-renderização do Jetpack Compose.

4. **Simplificação do Retrofit:**
   * Isolar as interfaces do Retrofit com tratamento genérico de erros para converter falhas HTTP em chamadas seguras e amigáveis ao usuário.
