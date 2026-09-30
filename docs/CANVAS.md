# Canvas do Projeto Final — Sistema de Gerenciamento JS Reboques

|                           |                                                               |
| ------------------------- | ------------------------------------------------------------- |
| **Grupo**                 | MaineProg                                                     |
| **Integrantes**           | Joao Pedro, Joao Pietro, Pedro Henrique e Samuel Verissimo    |
| **Turma**                 | 3º ano — Ensino Médio                                         |
| **Repositório**           | `https://github.com/samuelverissimo/projeto-final-jsreboques` |
| **Data de preenchimento** | 09/09/2026                                                    |
| **Entrega final**         | **10/12/2026**                                                |

---

## Bloco 1 — Nome e pitch do app

**Nome do app:** JS Reboques

**Pitch:**

> O JS Reboques é um sistema de gerenciamento para vendedores de uma loja de reboques, permitindo controlar os produtos, estoque, compras, vendas e clientes em um único aplicativo.

---

## Bloco 2 — Problema

O gerenciamento de uma loja de reboques pode envolver o controle de diversos produtos, quantidades em estoque, compras realizadas, vendas e informações dos clientes. Quando essas informações são controladas manualmente ou ficam espalhadas em diferentes locais, podem ocorrer erros no controle do estoque e dificuldade para consultar o histórico das operações.

O sistema resolve esse problema centralizando as informações da loja em um único aplicativo, facilitando o cadastro e gerenciamento dos reboques, o controle de estoque, o registro de compras e vendas e o cadastro de clientes.

---

## Bloco 3 — Público-alvo

* **Perfil principal:** vendedores, administradores e responsáveis pelo gerenciamento de lojas de reboques.
* **Quando/onde usam:** durante o atendimento aos clientes e no gerenciamento diário da loja.
* **Principal necessidade:** consultar produtos, controlar o estoque, registrar compras e vendas e manter os dados dos clientes organizados.
* **Pessoa que vai testar:** um vendedor, responsável ou conhecido de uma loja de reboques.

---

## Bloco 4 — Solução em uma tela

* **A tela principal apresenta:** um painel de gerenciamento com acesso aos reboques, estoque, compras, vendas e clientes.
* **A ação principal:** selecionar uma das áreas do sistema para consultar ou cadastrar informações.
* **Depois de agir:** o usuário consegue visualizar, cadastrar, editar ou gerenciar os dados correspondentes à operação selecionada.

---

## Bloco 5 — Funcionalidades do MVP

| #  | Funcionalidade                         | Essencial? | Quem faz             |
| -- | -------------------------------------- | ---------- | -------------------- |
| F1 | Cadastro e gerenciamento de reboques   | Sim        | Samuel e João Pedro  |
| F2 | Controle de estoque                    | Sim        | João Pietro e Samuel |
| F3 | Registro de vendas                     | Sim        | João Pedro e Pedro   |
| F4 | Registro de compras                    | Sim        | João Pietro e Pedro  |
| F5 | Cadastro e gerenciamento de clientes   | Sim        | Pedro e Pietro       |
| F6 | Tela inicial / painel de gerenciamento | Sim        | João Pedro e Pietro  |
| F7 | Login do administrador                 | Não        | Samuel e Pedro       |
| F8 | Histórico de vendas e compras          | Não        | Samuel e João Pedro  |

---

## Bloco 6 — Fora do escopo

* ❌ Pagamento pelo aplicativo
* ❌ Venda direta de reboques pelo aplicativo
* ❌ Atendimento ou chat em tempo real com clientes
* ❌ Sistema de entrega de reboques
* ❌ Integração com sistemas externos de pagamento
* ❌ Gerenciamento de múltiplas lojas

---

## Bloco 7 — Caminho técnico

* [x] **Opção A — Room:** dados de reboques, estoque, compras, vendas, clientes e usuários salvos no próprio dispositivo.

**Bibliotecas:**

* Room
* Jetpack Compose
* Android Jetpack
* Kotlin

**Banco de dados:**

O aplicativo utilizará o Room para persistência local dos dados.

**Principais entidades:**

* Reboque
* Estoque
* Compra
* Venda
* Cliente
* Usuário

**`try/catch`:**

* **Pode falhar:** cadastrar, consultar, editar ou excluir dados do sistema.
* **Mensagem ao usuário:** "Não foi possível realizar a operação. Tente novamente."

---

## Bloco 8 — Identidade visual

| Item                             | Definição                                             |
| -------------------------------- | ----------------------------------------------------- |
| **Nome exibido (`strings.xml`)** | JS Reboques                                           |
| **Cor principal**                | `#213d5e (Azul Escuro)`                               |
| **Cores complementares**         | `#bedeff (Azul Claro)`                                |
| **Ícone (512×512)**              | Reboque visto de lado, remetendo aos produtos da loja |
| **`applicationId`**              | `br.edu.ifpe.jsreboques`                              |
| **Versão inicial**               | `1.0` (versionCode `1`)                               |

---

## Bloco 9 — Equipe, papéis e riscos

| Integrante       | Responsável por                                    |
| ---------------- | -------------------------------------------------- |
| Joao Pedro       | Tela principal, gerenciamento de reboques e vendas |
| Joao Pietro      | Banco de dados com Room e controle de estoque      |
| Pedro Henrique   | Cores, imagens, ícone, clientes e compras          |
| Samuel Verissimo | README, testes, integração e APK                   |

**Riscos:**

| Risco                                  | Plano B                                                                |
| -------------------------------------- | ---------------------------------------------------------------------- |
| Room apresentar erros                  | Simplificar entidades e operações do banco                             |
| Não dar tempo de implementar tudo      | Priorizar cadastro de reboques, estoque e registro de vendas           |
| Problemas na integração entre as telas | Reduzir a quantidade de telas e centralizar as operações principais    |
| Erros no controle de estoque           | Implementar primeiro as operações básicas de entrada e saída           |
| Problemas durante os testes            | Realizar testes individuais de cada funcionalidade antes da integração |

---

## Bloco 10 — Acordo de trabalho com IA

**Regras para o `AGENTS.md`:**

1. A IA segue as funcionalidades definidas no Canvas e no PRD.
2. Todo código gerado é revisado e entendido pelo grupo antes de aceitar.
3. Nenhuma alteração importante entra sem um integrante ler e compreender o código.
4. A IA não deve adicionar funcionalidades que estejam fora do escopo sem autorização do grupo.
5. Alterações no banco de dados devem ser revisadas pelo responsável pelo Room.
6. Toda funcionalidade implementada deve ser testada antes de ser considerada concluída.

O grupo segue os combinados padrão do curso (revisar antes de aceitar, comentário de fronteira de quem aceitou, revisão conjunta antes de cada marco e nenhuma chave de API no prompt) e, adicionalmente, testa cada funcionalidade antes de qualquer entrega. Quem implementar uma parte apresenta o funcionamento dela para o restante do grupo.

---

## Bloco 11 — Definição de pronto

* [ ] O usuário administrador consegue acessar o sistema.
* [ ] A tela principal apresenta as principais áreas de gerenciamento.
* [ ] O usuário consegue cadastrar, editar e excluir reboques.
* [ ] O sistema armazena nome, valor de compra, valor de venda, categoria, modelo e marca dos reboques.
* [ ] O sistema permite consultar a quantidade de reboques em estoque.
* [ ] O usuário consegue registrar compras.
* [ ] Compras registradas atualizam a quantidade do estoque.
* [ ] O usuário consegue registrar vendas.
* [ ] Vendas registradas diminuem a quantidade disponível no estoque.
* [ ] O sistema não permite vender quantidade superior ao estoque disponível.
* [ ] O usuário consegue cadastrar e consultar clientes.
* [ ] O sistema mantém o histórico das vendas realizadas.
* [ ] O sistema mantém o histórico das compras realizadas.
* [ ] O app não fecha sozinho e nunca mostra tela branca — erros sempre viram mensagens claras.
* [ ] App com nome, ícone e cores próprias.
* [ ] Duas pessoas de fora do grupo testaram o `.apk` sem explicação.
* [ ] `README.md`, `docs/USO_DE_IA.md` e `AGENTS.md` preenchidos.
* [ ] Todo arquivo tem o comentário de fronteira de quem mexeu nele.

---

## Validação do professor

|                 |                                                   |
| --------------- | ------------------------------------------------- |
| **Data**        |                                                   |
| **Situação**    | ( ) Aprovado ( ) Aprovado com ajustes ( ) Refazer |
| **Observações** |                                                   |
