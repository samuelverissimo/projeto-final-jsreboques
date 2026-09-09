Canva instruções aqui
# 🎯 Canvas do Projeto Final — App Android

|                           |                                                            |
| ------------------------- | ---------------------------------------------------------- |
| **Grupo nº**              | ___                                                        |
| **Integrantes (3 a 4)**   | Joao Pedro, Joao Pietro, Pedro Henrique e Samuel Verissimo |
| **Turma**                 | 3º ano — Ensino Médio                                      |
| **Repositório**           | `https://github.com/____/____`                             |
| **Data de preenchimento** | 09/09/2026                                                 |
| **Entrega final**         | **10/12/2026**                                             |

---

## 🧩 Bloco 1 — Nome e pitch do app

**Nome do app:** Reboque Fácil

**Pitch em uma frase:**

> "O **Reboque Fácil** ajuda pessoas que procuram reboques a encontrar modelos disponíveis e conhecer seus preços e características sem precisar ir pessoalmente à loja."

---

## 😖 Bloco 2 — Problema

* Clientes interessados em comprar um reboque precisam entrar em contato com a loja para descobrir quais modelos estão disponíveis.
* Muitas vezes, não conseguem visualizar facilmente as características e os preços dos reboques antes de visitar a loja.

**Como esse problema é resolvido hoje (sem o app)?**

* O cliente precisa ir até a loja, procurar os produtos ou entrar em contato com o vendedor para consultar modelos, preços e informações.

---

## 👥 Bloco 3 — Público-alvo

* **Perfil principal:** Pessoas maiores de idade, empresas e profissionais que precisam comprar um reboque para transportar cargas ou equipamentos.
* **Quando/onde usam:** Quando estão procurando um reboque para comprar e querem comparar modelos e preços.
* **Uma pessoa real que testaria o app:** Um familiar, conhecido ou cliente da loja que tenha interesse em comprar um reboque.

---

## 💡 Bloco 4 — Solução em uma tela

* **A tela principal lista:** Os reboques disponíveis para venda, com nome, imagem e preço.
* **A ação principal do usuário é:** Selecionar um reboque para visualizar suas informações.
* **Depois de agir, o usuário vê:** Os detalhes do modelo, como preço, capacidade, tamanho e outras características.

---

## ✅ Bloco 5 — Funcionalidades do MVP

| #  | Funcionalidade                         | Essencial? | Quem faz         |
| -- | -------------------------------------- | ---------- | ---------------- |
| F1 | Listar os reboques disponíveis         | Sim        | Joao Pedro       |
| F2 | Visualizar detalhes de um reboque      | Sim        | Joao Pietro      |
| F3 | Cadastrar uma solicitação de orçamento | Sim        | Pedro Henrique   |
| F4 | Excluir uma solicitação de orçamento   | Não        | Samuel Verissimo |

---

## 🚫 Bloco 6 — Fora do escopo

O que o aplicativo não fará nesta entrega:

* ❌ Pagamento diretamente pelo aplicativo.
* ❌ Entrega do reboque pelo aplicativo.
* ❌ Chat ou atendimento em tempo real com vendedores.

---

## ⚙️ Bloco 7 — Caminho técnico

* [x] **Opção A — Room:** dados dos reboques e solicitações salvos no próprio celular.
* [ ] **Opção B — Retrofit:** dados vindos de uma API pública.
* [ ] **Opção C — Desafio:** API + salvar favoritos localmente.

**Se escolheu B ou C — qual API?**

Não se aplica, pois o projeto utilizará Room.

**Bibliotecas que o grupo vai usar:**

* Room
* Jetpack Compose
* Android Jetpack

**Onde entra o `try/catch`?**

* **Pode falhar:** Ao cadastrar, consultar ou excluir uma solicitação de orçamento no banco de dados.
* **O usuário vê a mensagem:** "Não foi possível realizar a operação. Tente novamente."

---

## 🎨 Bloco 8 — Identidade visual

| Item                                   | Definição do grupo                                                     |
| -------------------------------------- | ---------------------------------------------------------------------- |
| **Nome exibido (`strings.xml`)**       | Reboque Fácil                                                          |
| **Cor principal (hex, em `Color.kt`)** | `#FF9800`                                                              |
| **Ideia do ícone (512×512)**           | Um reboque visto de lado, representando os produtos vendidos pela loja |
| **`applicationId`**                    | `br.edu.ifpe.reboquefacil`                                             |
| **Versão inicial**                     | `1.0` (versionCode `1`)                                                |

---

## 👤 Bloco 9 — Equipe, papéis e riscos

| Integrante           | Papel principal               | Responsável por                            |
| -------------------- | ----------------------------- | ------------------------------------------ |
| **Joao Pedro**       | Dev / telas                   | Tela principal e catálogo de reboques      |
| **Joao Pietro**      | Dev / dados                   | Banco de dados com Room                    |
| **Pedro Henrique**   | Design e identidade visual    | Cores, imagens, ícone e organização visual |
| **Samuel Verissimo** | Documentação, build e entrega | README, testes e preparação do APK         |

> Todos programam. O "papel" define quem responde por aquela parte, não quem trabalha sozinho.

**Riscos — o que pode dar errado e o plano B:**

| Risco                                              | Plano B                                            |
| -------------------------------------------------- | -------------------------------------------------- |
| Banco de dados Room apresentar erros               | Simplificar as entidades e operações do banco      |
| Não conseguir implementar todas as funcionalidades | Priorizar o catálogo e a visualização dos detalhes |

---

## 🤖 Bloco 10 — Acordo de trabalho com IA

A implementação pode ser feita com o Gemini no Android Studio. O grupo deverá entender e revisar todo código gerado pela IA.

**Três regras que vamos escrever no nosso `AGENTS.md`:**

1. A IA deve seguir as funcionalidades definidas no Canvas e no PRD.
2. Todo código gerado pela IA deve ser revisado e entendido pelo grupo.
3. Nenhuma alteração importante será aceita sem que um integrante leia e compreenda o código.

**Combinados do grupo:**

* [x] Ninguém clica *Accept* no Agent Mode sem ler a mudança inteira.
* [x] Quem aceitou o código escreve o comentário de fronteira do arquivo.
* [x] Antes de cada marco, revisamos juntos: alguém aqui não entende alguma parte?
* [x] Nenhuma chave de API ou senha vai para o prompt.
* [x] Todos devem testar as funcionalidades antes das entregas.

**Como vamos garantir que todos entendem tudo:**

* Quem implementar uma parte deve apresentar o funcionamento do código aos outros integrantes. O grupo fará revisões conjuntas antes dos principais marcos.

---

## 🗓️ Bloco 11 — Marcos até 10/12

| Marco                                                 | Prazo     | Como se comprova no GitHub                           |
| ----------------------------------------------------- | --------- | ---------------------------------------------------- |
| M1 — Canvas preenchido + repositório criado           | 16/09     | `CANVAS.md` no `main`                                |
| M2 — PRD aprovado + telas rascunhadas                 | 30/09     | `PRD.md` + imagens em `docs/`                        |
| M3 — Funcionalidade base rodando                      | 21/10     | Tela principal lista reboques + 1 ação + `try/catch` |
| M4 — Dados completos (Room/Retrofit) e erros tratados | 11/11     | Commits da camada de dados                           |
| M5 — Identidade visual + `.apk` de release testado    | 25/11     | Ícone, cores, `.apk` testado por 2 pessoas de fora   |
| M6 — `.aab` + material de loja + `README.md`          | 02/12     | Pasta `loja/` + `README.md` completo                 |
| **Entrega e apresentação**                            | **10/12** | Tag `v1.0` no repositório                            |

---

## 🏁 Bloco 12 — Definição de pronto

O grupo só considera o app pronto quando **todas** estas frases forem verdadeiras:

* [ ] O app abre e não fecha sozinho depois de 5 minutos de uso.
* [ ] A tela principal mostra os reboques cadastrados no banco de dados.
* [ ] O usuário consegue selecionar um reboque e visualizar seus detalhes.
* [ ] O usuário consegue registrar uma solicitação de orçamento.
* [ ] Quando algo falha, aparece uma mensagem clara — o app não quebra.
* [ ] O app tem nome, ícone e cor próprios.
* [ ] Duas pessoas de fora do grupo instalaram o `.apk` e conseguiram usar sem explicação.
* [ ] O `README.md` explica o que o app faz, com o que foi feito e como gerar o build.
* [ ] O `docs/USO_DE_IA.md` e o `AGENTS.md` estão preenchidos.
* [ ] Cada integrante consegue abrir o projeto e fazer uma mudança pequena sozinho.
* [ ] Todo arquivo nosso tem o comentário de fronteira escrito por nós.

---

## ✍️ Validação do professor

|                 |                                                   |
| --------------- | ------------------------------------------------- |
| **Data**        |                                                   |
| **Situação**    | ( ) Aprovado ( ) Aprovado com ajustes ( ) Refazer |
| **Observações** |                                                   |
