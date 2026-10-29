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

### 2.6. Camada de Integração com API (Cliente)
- **Autor:** Pedro Henrique
- **Data:** 21/10/2026
- **Ferramenta:** Gemini no Android Studio
- **Prompt utilizado:**
  > "Atue como um Engenheiro Android Sênior e Prompt Engineer. Com base na estrutura do projeto JS Reboques, gere a camada de integração com API para a entidade Cliente.
  >
  > ### CONTEXTO TÉCNICO
  > - Pacote Base: br.edu.ifpe.jsreboques
  > - Entidade Local Existente: ClienteEntity (id: Long, nome: String, email: String, telefone: String, documento: String) no pacote .data.local.entity.
  > - Tecnologias: Retrofit 2.11.0, Gson, OkHttp, Coroutines (suspend functions).
  >
  > ### TAREFAS SOLICITADAS
  > 1. Criação de DTO e Mappers:
  >    - Crie `br.edu.ifpe.jsreboques.data.remote.dto.ClienteDto` para representação dos dados da API.
  >    - Implemente funções de extensão em um arquivo `ClienteMapper.kt` para converter `ClienteDto` <-> `ClienteEntity`.
  > 2. Interface Retrofit:
  >    - Crie `br.edu.ifpe.jsreboques.data.remote.api.ClienteApi` com as funções:
  >      - @GET("clientes") -> List<ClienteDto>
  >      - @GET("clientes/{id}") -> ClienteDto
  >      - @POST("clientes") -> ClienteDto (enviando @Body ClienteDto)
  >      - @PUT("clientes/{id}") -> ClienteDto (enviando @Path e @Body)
  >      - @DELETE("clientes/{id}") -> Response<Unit>
  >    - Use `suspend functions` em todas as chamadas.
  > 3. Configuração do Cliente:
  >    - Crie um singleton `br.edu.ifpe.jsreboques.data.remote.RetrofitClient`.
  >    - Configure o OkHttpClient com um `HttpLoggingInterceptor` (adicione a dependência 'com.squareup.okhttp3:logging-interceptor' caso necessário no build.gradle).
  >    - Base URL sugerida: "https://api.jsreboques.com.br/".
  > 4. Registro de Transparência (Documentação):
  >    - Atualize o arquivo AGENTS.md (ou USO_DE_IA.md) na raiz do projeto.
  >    - Adicione uma nova entrada na seção de "Android Studio Bot / Gemini" descrevendo que este prompt foi utilizado para gerar a camada de rede da entidade Cliente, visando padronização DTO/Entity e integração com Retrofit."
- **O que foi gerado/alterado:** Implementação completa da camada de rede para Clientes (DTO, Mapper, Interface API e RetrofitClient) com suporte a logging e Coroutines.

---

## 3. Diretrizes de Uso da IA
1. Todo o código gerado foi revisado, testado e validado via compilação e testes unitários no Android Studio.
2. Cada membro da equipe é responsável por registrar seus próprios prompts e contribuições específicas nos commits das suas respectivas entidades.
