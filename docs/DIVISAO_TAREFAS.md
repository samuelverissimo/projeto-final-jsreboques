# Divisão de Tarefas da Equipe — MaineProg

Documento de transparência de divisão de responsabilidades e desenvolvimento do projeto **JS Reboques**.

---

## 👥 Integrantes e Módulos Responsáveis

### 1. João Pedro
- **Módulo Responsável**: Gestão de Vendas & Operações Financeiras
- **Entidades e Arquivos**:
  - `VendaEntity.kt` / `Venda.kt`
  - `NovaVendaScreen.kt`
  - `NovaVendaViewModel.kt`
  - Métodos da `OperacaoDao` referentes a vendas (`registrarVenda`)
- **Contribuições**: Lógica de registro de vendas vinculadas a Clientes e Reboques, validação atômica de estoque (impedindo vender o que não há em estoque) e tratamento financeiro em centavos.

---

### 2. Pedro Henrique
- **Módulo Responsável**: Gestão de Clientes & Camada de Rede (API e Retrofit)
- **Entidades e Arquivos**:
  - `ClienteEntity.kt` / `Cliente.kt`
  - `ClienteApi.kt`, `ClienteDto.kt`, `ClienteMapper.kt`
  - `ClienteRepository.kt`
  - `ClientesScreen.kt`
  - `ClientesViewModel.kt`
- **Contribuições**: Estruturação da camada de rede e sincronização de clientes via Retrofit, mascaramento e formatação de CPF/CNPJ e contatos de WhatsApp.

---

### 3. João Pietro
- **Módulo Responsável**: Compras de Reboques & Cadastro de Fornecedores
- **Entidades e Arquivos**:
  - `CompraEntity.kt` / `Compra.kt`
  - `FornecedorEntity.kt`
  - `NovaCompraScreen.kt` e `NovaCompraViewModel.kt`
  - `FornecedoresScreen.kt` e `FornecedoresViewModel.kt`
- **Contribuições**: Implementação da camada de compras com atualização de estoque, gestão de parceiros fornecedores e documentação de integridade referencial.

---

### 4. Samuel Veríssimo
- **Módulo Responsável**: Gestão de Reboques, Estoque, Tarefas & Arquitetura dos 4 Estados de Tela
- **Entidades e Arquivos**:
  - `UiState.kt`
  - `ReboqueEntity.kt`, `EstoqueEntity.kt`, `TarefaEntity.kt`
  - `ReboquesScreen.kt` e `ReboquesViewModel.kt`
  - `EstoqueScreen.kt` e `EstoqueViewModel.kt`
  - `TarefasScreen.kt` e `TarefaViewModel.kt`
  - `Componentes.kt` (`MensagemCentral`)
- **Contribuições**: Criação da arquitetura unificada dos quatro estados da tela (`Carregando`, `Vazio`, `Erro` e `Sucesso`), implementação dos ViewModels de reboque, estoque e tarefas, garantindo compilação exaustiva sem cláusula `else`.
