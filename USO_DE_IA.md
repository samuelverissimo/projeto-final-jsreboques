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

### 2.5. Entidade Cliente (Pedro Henrique)
- **Prompt:** 
  > "Estou trabalhando em um projeto Android em Kotlin com Room e preciso criar/organizar a entidade Cliente..."
- **Resultado:**
  > A classe ClienteEntity foi movida para um arquivo próprio e atualizada com o campo telefone.

---

## 3. Implementação da Entidade Tarefa e Manutenção (João Pedro)
- **Data:** 22/05/2024
- **Ferramenta:** Assistente de IA no Android Studio

### Prompt 1: Resolução de Erro de Sincronização
> "I'm getting the following error while syncing my project. The error is: com.intellij.openapi.externalSystem.model.ExternalSystemException: Could not resolve all dependencies for configuration ':app:debugAndroidTestCompileClasspath'. Fix this issue"

**O que foi feito:**
- Identificação de erro de falta de espaço em disco e travamento de cache do Gradle.
- Sugestão de limpeza manual e interrupção de processos do Gradle Daemon.

### Prompt 2: Camada de Dados para Tarefas
> "Estou fazendo um app Android em Kotlin (matéria de Dispositivos Móveis) e sou responsável pela camada de dados com Room. Entidade: Tarefa. Campos: id (Int, autogerado), titulo (String), descricao (String), concluida (Boolean). Gere: 1. Dependências do Room (com KSP) para o build.gradle 2. A entidade (@Entity, data class) 3. O DAO (@Dao) com: inserir, atualizar, deletar, buscar por id, listar todos (Flow) e deletar todos, usando suspend 4. A classe AppDatabase (singleton)"

**O que foi feito:**
1. **TarefaEntity.kt:** Criada com campos auto-gerados e estrutura Room.
2. **TarefaDao.kt:** Implementado CRUD completo com Flow e Suspend functions.
3. **AppDatabase.kt:** Atualizado para a versão 3, inclusão da nova entidade e implementação do padrão **Singleton** seguro (`@Volatile` e `synchronized`).
4. **TarefaRepository:** Criado em `Repositorios.kt` integrando com a lógica de tratamento de erros (`executarSeguro`) do projeto.
5. **AppContainer.kt:** Registrado o novo repositório para injeção de dependência.

### Prompt 3: Registro de prompts
> "salve todos os prompts na pasta USO DE IA"

---

## 4. Diretrizes de Uso da IA
1. Todo o código gerado foi revisado, testado e validado via compilação e testes unitários no Android Studio.
2. Cada membro da equipe é responsável por registrar seus próprios prompts e contribuições específicas.
