PRIMEIRO PROMPT DO BOILERPLATE QUE NÓS MESMOS EDITAREMOS.


Você deve preparar o boilerplate inicial de um projeto Android chamado **JS Reboques**.

IMPORTANTE: neste momento, NÃO crie nenhuma entidade, regra de negócio, DAO, ViewModel, interface Retrofit ou banco de dados implementado. O objetivo é somente criar a infraestrutura/casca inicial do projeto, deixando o aplicativo compilável e funcionando.

## Contexto do projeto

O projeto é um sistema Android de gerenciamento para uma loja de reboques.

Posteriormente teremos entidades como reboques, clientes, vendas, compras e estoque, mas essas entidades serão criadas separadamente pelos integrantes da equipe em commits individuais.

Por isso, nesta etapa, NÃO crie nenhuma dessas entidades.

## Tecnologias obrigatórias

Configure o projeto utilizando:

* Kotlin
* Jetpack Compose
* Material 3
* Navigation Compose
* Room Database
* KSP
* Retrofit 2
* Gson Converter
* Kotlin Coroutines
* Flow

Room e Retrofit devem ser apenas configurados como dependências no Gradle neste momento. Não implemente banco de dados, entidades, DAO ou chamadas de API.

## Identificação do projeto

Nome do aplicativo:

JS Reboques

Application ID:

`br.edu.ifpe.jsreboques`

Use Kotlin DSL (`build.gradle.kts`).

## Estrutura de pacotes

Crie a seguinte estrutura:

```text
br.edu.ifpe.jsreboques
│
├── data
│   ├── local
│   ├── remote
│   └── repository
│
├── model
│
└── ui
    ├── theme
    ├── navigation
    └── features
```

A pasta `model` deve permanecer vazia nesta etapa.

As pastas `data/local`, `data/remote`, `data/repository` e `ui/features` também não precisam conter entidades ou regras de negócio.

## Tema

Mantenha o tema base do Jetpack Compose.

Utilize:

* Material 3
* Cor principal: `#213d5e`
* Cor complementar: `#bedeff`

Mantenha os arquivos:

```text
ui/theme/Color.kt
ui/theme/Type.kt
ui/theme/Theme.kt
```

## Navigation

Crie:

```text
ui/navigation/NavTarget.kt
ui/navigation/NavGraph.kt
```

`NavTarget.kt` deve possuir somente uma rota:

```text
HOME
```

Não crie rotas para Reboques, Clientes, Vendas, Compras ou Estoque ainda.

O `NavGraph.kt` deve possuir um `NavHost` básico contendo apenas a tela inicial.

## MainActivity

Configure a `MainActivity.kt` para:

1. Utilizar o tema do aplicativo.
2. Criar o NavGraph/NavHost.
3. Exibir apenas uma tela inicial.
4. A tela inicial deve mostrar o texto:

`JS Reboques`

Não implemente nenhuma funcionalidade de gerenciamento ainda.

## Gradle

Configure os arquivos `build.gradle.kts` necessários para o projeto.

Adicione os plugins e dependências necessários para:

* Jetpack Compose
* Material 3
* Navigation Compose
* Room
* KSP
* Retrofit 2
* Gson Converter
* Coroutines

Use versões compatíveis entre si e com a versão do Android Gradle Plugin/Kotlin já utilizada pelo projeto.

Não altere versões desnecessariamente se o projeto já possuir versões modernas e compatíveis.

## Restrições IMPORTANTES

NÃO criar:

* Entidades Room
* `@Entity`
* DAO
* `@Dao`
* `RoomDatabase`
* AppDatabase
* Interface Retrofit
* API Service
* Repository com lógica de negócio
* ViewModel
* Hilt
* Koin
* Casos de uso
* Regras de negócio
* Telas de cadastro
* Telas de venda
* Telas de estoque
* Telas de clientes
* Telas de compras

Também não adicione funcionalidades que não foram solicitadas.

## Resultado esperado

Ao terminar, o projeto deve:

1. Compilar sem erros.
2. Abrir normalmente no Android Studio.
3. Executar no emulador/dispositivo.
4. Mostrar a tela inicial com "JS Reboques".
5. Possuir o Navigation Compose funcionando com uma única rota HOME.
6. Possuir as dependências de Room, Retrofit, Gson, Coroutines e Flow configuradas.
7. Possuir a estrutura de diretórios solicitada.
8. Estar pronto para que, em uma próxima etapa, cada integrante adicione sua própria entidade.

Antes de modificar arquivos, analise a estrutura atual do projeto e preserve configurações que já estejam corretas.

Depois de realizar as alterações, verifique se o projeto compila e corrija qualquer erro causado pelas alterações.

Não crie entidades nesta etapa.


SEGUNDO PROMPT (ENTIDADES):

Agora vamos implementar as entidades/modelos iniciais do sistema **JS Reboques**.

O boilerplate inicial do projeto já foi criado. Nesta etapa, quero adicionar somente as entidades/modelos que serão utilizados posteriormente pelo sistema.

## Contexto

O JS Reboques é um sistema Android para gerenciamento interno de uma loja de reboques.

O sistema precisa controlar:

* vendas realizadas;
* clientes;
* estoque de reboques;
* compras realizadas pela empresa;
* usuários que acessam o aplicativo.

Utilize o **Room Database** já configurado no projeto.

## Entidades que devem ser criadas

Crie exatamente estas 5 entidades:

1. `Venda`
2. `Cliente`
3. `EstoqueReboque`
4. `Compra`
5. `Usuario`

Todas devem ficar no pacote:

`br.edu.ifpe.jsreboques.model`

## 1. Entidade Venda

Crie `Venda.kt`.

Campos:

* `id`: Long, chave primária, com geração automática.
* `clienteId`: Long, identificando o cliente relacionado à venda.
* `estoqueReboqueId`: Long, identificando o reboque vendido.
* `data`: String, armazenando a data da venda.
* `quantidade`: Int.
* `valorUnitario`: Double.
* `valorTotal`: Double.

Utilize `@Entity` e `@PrimaryKey(autoGenerate = true)`.

## 2. Entidade Cliente

Crie `Cliente.kt`.

Campos:

* `id`: Long, chave primária, com geração automática.
* `nome`: String.
* `email`: String.
* `whatsapp`: String.

Utilize `@Entity` e `@PrimaryKey(autoGenerate = true)`.

## 3. Entidade EstoqueReboque

Crie `EstoqueReboque.kt`.

Essa entidade representa os reboques disponíveis no estoque da empresa.

Campos:

* `id`: Long, chave primária, com geração automática.
* `nome`: String.
* `marca`: String.
* `modelo`: String.
* `categoria`: String.
* `valorCompra`: Double.
* `valorVenda`: Double.
* `quantidade`: Int.
* `descricao`: String.

Utilize `@Entity` e `@PrimaryKey(autoGenerate = true)`.

## 4. Entidade Compra

Crie `Compra.kt`.

Essa entidade representa as compras de reboques realizadas pela empresa.

Campos:

* `id`: Long, chave primária, com geração automática.
* `estoqueReboqueId`: Long, identificando o reboque comprado.
* `data`: String.
* `quantidade`: Int.
* `valorUnitario`: Double.
* `valorTotal`: Double.

Utilize `@Entity` e `@PrimaryKey(autoGenerate = true)`.

## 5. Entidade Usuario

Crie `Usuario.kt`.

Essa entidade representa os usuários autorizados a utilizar o aplicativo.

Campos:

* `id`: Long, chave primária, com geração automática.
* `nome`: String.
* `usuario`: String.
* `senha`: String.
* `tipo`: String.

Utilize `@Entity` e `@PrimaryKey(autoGenerate = true)`.

## Regras importantes

Nesta etapa, faça SOMENTE a criação das entidades.

NÃO crie:

* DAOs;
* `@Dao`;
* `RoomDatabase`;
* `AppDatabase`;
* Repositories;
* ViewModels;
* Use Cases;
* telas;
* telas de cadastro;
* Navigation adicional;
* APIs;
* Retrofit interfaces;
* regras de negócio;
* autenticação;
* relacionamentos Room com `@Relation`;
* Foreign Keys;
* lógica de estoque;
* lógica de venda;
* lógica de compra.

Os campos `clienteId` e `estoqueReboqueId` devem ser apenas valores `Long` nesta etapa. Os relacionamentos serão implementados posteriormente.

## Organização

Todas as entidades devem ficar em:

`br.edu.ifpe.jsreboques.model`

Não crie novos pacotes desnecessariamente.

Mantenha o restante do projeto como está.

## Objetivo

Ao final, o projeto deve possuir:

```text
model/
├── Venda.kt
├── Cliente.kt
├── EstoqueReboque.kt
├── Compra.kt
└── Usuario.kt
```

As cinco classes devem estar anotadas corretamente para serem utilizadas pelo Room posteriormente.

Não implemente nenhuma outra funcionalidade.

Depois de criar os arquivos, verifique se o projeto continua compilando e corrija somente erros relacionados a essa implementação.
