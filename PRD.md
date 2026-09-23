# PRD — JS Reboques

## 1. Visão geral

- **Nome:** JS Reboques
- **Grupo:** MaineProg
- **Integrantes:** João Pedro, João Pietro, Pedro Henrique e Samuel Verissimo
- **Turma:** 3º ano — Ensino Médio
- **Repositório:** projeto-final-jsreboques
- **Entrega final:** 10/12/2026

### Pitch

O JS Reboques é um aplicativo de gerenciamento para lojas de reboques, permitindo controlar produtos, estoque, compras, vendas e clientes em um único sistema.

## 2. Problema

Lojas de reboques precisam administrar produtos, estoque, compras, vendas e informações de clientes. O controle manual ou descentralizado dessas informações pode causar erros de estoque e dificultar a consulta do histórico das operações.

O JS Reboques centraliza essas informações em um aplicativo, facilitando o gerenciamento diário da loja.

## 3. Objetivo do produto

Criar um aplicativo Android simples e funcional que permita ao usuário responsável pela loja:

- Cadastrar e gerenciar reboques.
- Consultar e controlar o estoque.
- Registrar compras.
- Registrar vendas.
- Cadastrar e consultar clientes.
- Consultar o histórico de compras e vendas.
- Centralizar essas operações em uma tela principal de gerenciamento.

## 4. Público-alvo

O sistema será utilizado principalmente por:

- Vendedores de lojas de reboques.
- Administradores.
- Responsáveis pelo gerenciamento da loja.

O uso ocorrerá principalmente durante o atendimento aos clientes e no gerenciamento diário do estabelecimento.

## 5. MVP — Funcionalidades

### F1 — Gerenciamento de reboques

O usuário deve conseguir:

- Cadastrar reboques.
- Consultar reboques.
- Editar reboques.
- Excluir reboques.

Cada reboque deverá armazenar, no mínimo:

- Nome.
- Valor de compra.
- Valor de venda.
- Categoria.
- Modelo.
- Marca.

### F2 — Controle de estoque

O sistema deverá:

- Exibir a quantidade disponível de cada reboque.
- Registrar entradas de estoque.
- Registrar saídas de estoque.
- Atualizar o estoque automaticamente após compras e vendas.
- Impedir vendas em quantidade superior ao estoque disponível.

### F3 — Registro de vendas

O usuário deverá conseguir:

- Registrar uma venda.
- Associar a venda a um cliente.
- Informar o reboque vendido e sua quantidade.
- Atualizar o estoque após a venda.
- Consultar vendas realizadas.

### F4 — Registro de compras

O usuário deverá conseguir:

- Registrar uma compra.
- Informar o reboque adquirido e sua quantidade.
- Atualizar o estoque após a compra.
- Consultar compras realizadas.

### F5 — Clientes

O sistema deverá permitir:

- Cadastrar clientes.
- Consultar clientes.
- Utilizar os clientes no registro das vendas.

### F6 — Tela inicial

A tela inicial deverá funcionar como um painel de gerenciamento, oferecendo acesso às principais áreas:

- Reboques.
- Estoque.
- Compras.
- Vendas.
- Clientes.

### F7 — Login do administrador

Funcionalidade opcional, caso haja tempo para implementação.

### F8 — Histórico de operações

Funcionalidade opcional do MVP, permitindo consultar o histórico de compras e vendas.

## 6. Fluxo principal do usuário

1. O administrador acessa o aplicativo.
2. O sistema apresenta a tela principal.
3. O usuário seleciona uma área de gerenciamento.
4. O usuário consulta ou cadastra as informações desejadas.
5. O sistema salva os dados localmente.
6. Em compras, o estoque é aumentado.
7. Em vendas, o estoque é reduzido.
8. Caso ocorra um erro, o sistema apresenta uma mensagem clara ao usuário.

## 7. Regras de negócio

- Uma venda não pode retirar uma quantidade maior do que a disponível no estoque.
- Uma compra registrada deve aumentar o estoque correspondente.
- Uma venda registrada deve diminuir o estoque correspondente.
- Os dados cadastrados devem permanecer armazenados no dispositivo.
- Operações que apresentarem erro devem informar o usuário através de uma mensagem clara.
- O sistema não deve fechar inesperadamente durante uma operação.

**Mensagem padrão para erros:**
> "Não foi possível realizar a operação. Tente novamente."

## 8. Dados e entidades

O aplicativo utilizará persistência local através do Room.

Principais entidades:

- Reboque
- Estoque
- Compra
- Venda
- Cliente
- Usuário

Os dados serão armazenados no próprio dispositivo, sem necessidade de servidor externo no MVP.

## 9. Tecnologia

- **Plataforma:** Android
- **Tecnologias principais:** Kotlin, Jetpack Compose, Android Jetpack, Room
- **Banco de dados:** Room com persistência local
- **Application ID:** `br.edu.ifpe.jsreboques`
- **Versão inicial:** 1.0 — versionCode 1

## 10. Interface e identidade visual

O aplicativo deverá utilizar:

- **Nome:** JS Reboques
- **Cor principal:** `#213d5e` — azul escuro
- **Cor complementar:** `#bedeff` — azul claro
- **Ícone:** representação de um reboque visto de lado
- Interface simples, organizada e adequada ao uso durante o atendimento em uma loja.

## 11. Fora do escopo

Não fazem parte do produto:

- Pagamentos pelo aplicativo.
- Venda direta de reboques pelo aplicativo.
- Chat ou atendimento em tempo real.
- Sistema de entrega.
- Integração com sistemas externos de pagamento.
- Gerenciamento de múltiplas lojas.

## 12. Critérios de aceite

O MVP será considerado pronto quando:

- [ ] O usuário conseguir acessar o sistema.
- [ ] A tela principal apresentar as áreas de gerenciamento.
- [ ] For possível cadastrar, editar e excluir reboques.
- [ ] Os dados dos reboques forem armazenados corretamente.
- [ ] O estoque puder ser consultado.
- [ ] Compras atualizarem o estoque.
- [ ] Vendas atualizarem o estoque.
- [ ] O sistema impedir vendas acima do estoque disponível.
- [ ] Clientes puderem ser cadastrados e consultados.
- [ ] O histórico de vendas e compras estiver disponível, conforme a implementação do MVP.
- [ ] Erros sejam tratados com mensagens claras.
- [ ] O aplicativo possua nome, ícone e identidade visual próprios.
- [ ] O APK seja testado por pelo menos duas pessoas externas ao grupo sem explicação prévia.
- [ ] README.md, docs/USO_DE_IA.md e AGENTS.md estejam preenchidos.
- [ ] Cada arquivo alterado possua o comentário de fronteira do integrante responsável.

## 13. Plano de desenvolvimento

A prioridade de implementação será:

1. Estrutura inicial do aplicativo.
2. Banco de dados com Room.
3. Cadastro e gerenciamento de reboques.
4. Controle de estoque.
5. Registro de compras.
6. Registro de vendas.
7. Cadastro de clientes.
8. Tela principal.
9. Histórico de operações.
10. Login do administrador, se houver tempo.
11. Integração das funcionalidades.
12. Testes e correções.
13. Geração e teste do APK.
14. Finalização da documentação.

Caso o prazo seja reduzido, as prioridades serão cadastro de reboques, estoque e registro de vendas.

## 14. Riscos e planos de contingência

| Risco | Plano de contingência |
|---|---|
| Problemas com Room | Simplificar entidades e operações do banco |
| Falta de tempo | Priorizar funcionalidades essenciais do MVP |
| Problemas de integração | Reduzir o número de telas e centralizar operações |
| Erros no estoque | Implementar e testar primeiro entradas e saídas |
| Problemas nos testes | Testar cada funcionalidade individualmente antes da integração |

## 15. Uso de IA no desenvolvimento

A IA poderá auxiliar no desenvolvimento, mas:

- O código gerado deverá ser revisado pelo grupo.
- Nenhum integrante deverá aceitar código que não compreenda.
- Alterações importantes deverão ser lidas e compreendidas antes da aprovação.
- A IA não deverá adicionar funcionalidades fora do escopo sem autorização.
- Alterações no banco deverão ser revisadas pelo responsável pelo Room.
- Toda funcionalidade deverá ser testada antes de ser considerada concluída.
- Nenhuma chave de API deverá ser inserida em prompts.
- O integrante responsável deverá apresentar sua implementação ao restante do grupo.

## 16. Definição de pronto

Uma funcionalidade será considerada concluída quando estiver:

- Implementada.
- Integrada ao aplicativo.
- Testada individualmente.
- Testada em conjunto com as demais funcionalidades quando necessário.
- Revisada pelo integrante responsável.
- Com tratamento de erros.
- Documentada quando necessário.

O produto final deverá funcionar como um sistema local de gerenciamento de uma loja de reboques, atendendo às funcionalidades essenciais definidas neste PRD.
