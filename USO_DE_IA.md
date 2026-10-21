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

### 2.5. Entidade Cliente
- **Autor:** Pedro Henrique
- **Data:** 21/10/2026
- **Ferramenta:** Gemini no Android Studio
- **Prompt utilizado:** Estou trabalhando em um projeto Android em Kotlin com Room e preciso criar/organizar a entidade Cliente.

Tarefas:
1. Verifique se existe a classe ClienteEntity no projeto. Ela deve ficar no arquivo ClienteEntity.kt, dentro do pacote data/local/entity/. Se estiver em outro arquivo ou pacote, mova o código para esse local e ajuste os imports e o package.
2. Se a classe ainda não existir, crie a ClienteEntity como uma data class do Room, com:
   - @Entity(tableName = "clientes")
   - @PrimaryKey(autoGenerate = true) val id: Int = 0
   - Campos: nome (String), email (String), telefone (String)
   (ajuste os campos se o projeto já tiver outros definidos)
3. Mostre o código final completo de ClienteEntity.kt com o package correto.
4. Gere um registro para o arquivo USO_DE_IA.md neste formato:

   ## Entidade Cliente
   - Autor: Pedro Henrique
   - Data: 21/10/2026
   - Ferramenta: Gemini no Android Studio
   - Prompt utilizado: (copie aqui o texto deste prompt)
   - O que foi gerado/alterado: (resuma em 1 ou 2 linhas)

Não altere nenhum outro arquivo além de ClienteEntity.kt e USO_DE_IA.md.
- **O que foi gerado/alterado:** A classe ClienteEntity foi movida para um arquivo próprio e atualizada com o campo telefone, mantendo a compatibilidade com o banco de dados.

---

## 3. Diretrizes de Uso da IA
1. Todo o código gerado foi revisado, testado e validado via compilação e testes unitários no Android Studio.
2. Cada membro da equipe é responsável por registrar seus próprios prompts e contribuições específicas nos commits das suas respectivas entidades.
