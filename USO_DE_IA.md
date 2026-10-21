# Registro do Uso de Inteligência Artificial — JS Reboques

Este documento registra como ferramentas de Inteligência Artificial (Android Studio Bot / Gemini / Claude) foram utilizadas no desenvolvimento do projeto **JS Reboques**.

---

## 1. Responsável
- **Desenvolvedor Principal:** Samuel
- **Membros da Equipe:** Samuel, Pedro Henrique, João Pedro, João Pietro

---

## 2. Prompts e Interações com a IA

### 2.1. Arquitetura e Configuração do Gradle
- **Prompt:**
  > "Configurar o Gradle (build.gradle.kts e libs.versions.toml) com Room, Retrofit, Navigation Compose e KSP para um aplicativo offline-first."
- **Resultado:**
  > Configuração do version catalog (`libs.versions.toml`), plugins do Android/Kotlin/KSP e suporte a AndroidX/Jetpack Compose.

### 2.2. Modelo de Dados e Banco de Dados Room
- **Prompt:**
  > "Gerar as entidades do Room para Reboque, Estoque, Cliente, Venda, Compra, AjusteEstoque e Usuario com relacionamentos de Chave Estrangeira (ForeignKey) e transações atômicas no OperacaoDao."
- **Resultado:**
  > Criação dos modelos e DAOs com suporte a `@Transaction` para garantir que vendas acimas do estoque disponivel lancem exceções e sofram rollback automático.

### 2.3. Interface do Usuário com Jetpack Compose
- **Prompt:**
  > "Desenvolver as telas do app em Jetpack Compose mantendo a identidade visual (#213D5E) com fluxo de navegacao entre Painel, Reboques, Estoque, Clientes, Nova Venda, Nova Compra e Historico."
- **Resultado:**
  > Implementação de componentes reutilizáveis, formatação monetária em Reais (centavos em Long) e fluxo responsivo de telas.

### 2.4. Resolução de Erros de Build e Sincronização
- **Prompt:**
  > "Corrigir erro de compilação no alias de plugins do Gradle e erro de carregamento de artefatos de build do APK."
- **Resultado:**
  > Declaração correta de aliases no `libs.versions.toml`, execução de limpa/recompilação (`clean assembleDebug`) e correção das propriedades do Gradle.

### 2.5. Gestão de Vendas e Fluxo de Estoque (João Pedro)
- **Prompt:**
  > "Implementar a VendaEntity com suporte a cálculos monetários em centavos (Long) e chaves estrangeiras para Reboques e Clientes. Criar o fluxo de registro de venda que valide o estoque disponível antes de finalizar a transação."
- **Resultado:**
  > Entidade Venda configurada e integrada ao `OperacaoDao` com suporte a `@Transaction` para garantir que vendas não ocorram sem estoque suficiente.

---

## 3. Diretrizes de Uso da IA
1. Todo o código gerado foi revisado, testado e validado via compilação e testes unitários no Android Studio.
2. Cada membro da equipe é responsável por registrar seus próprios prompts e contribuições específicas nos commits das suas respectivas entidades.
