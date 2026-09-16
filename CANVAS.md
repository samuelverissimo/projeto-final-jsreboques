#  Canvas do Projeto Final — App Android

| | |
|---|---|
| **Grupo** | MaineProg |
| **Integrantes** | Joao Pedro, Joao Pietro, Pedro Henrique e Samuel Verissimo |
| **Turma** | 3º ano — Ensino Médio |
| **Repositório** | `https://github.com/samuelverissimo/projeto-final-jsreboques` |
| **Data de preenchimento** | 09/09/2026 |
| **Entrega final** | **10/12/2026** |

---

##  Bloco 1 — Nome e pitch do app

**Nome do app:** JS Reboques

**Pitch:**
> A JS Reboques ajuda quem está procurando comprar um reboque a ver os modelos disponíveis, preços e características sem precisar ir até a loja.

---

##  Bloco 2 — Problema

Hoje, quem quer comprar um reboque só descobre o que a loja tem disponível indo até lá pessoalmente ou entrando em contato direto com um vendedor — não dá pra comparar modelos, preços e características antes disso. O app resolve isso deixando o catálogo acessível na mão do cliente.

---

##  Bloco 3 — Público-alvo

- **Perfil principal:** pessoas maiores de idade, empresas e profissionais que precisam de um reboque para transporte de carga ou equipamentos.
- **Quando/onde usam:** na hora de pesquisar e comparar opções antes de decidir a compra.
- **Pessoa que vai testar:** um cliente ou conhecido da loja com interesse real em comprar um reboque.

---

##  Bloco 4 — Solução em uma tela

- **A tela principal lista:** os reboques disponíveis, com nome, imagem e preço.
- **A ação principal:** tocar num reboque para ver os detalhes.
- **Depois de agir:** o usuário vê preço, capacidade, tamanho e demais características do modelo.

---

##  Bloco 5 — Funcionalidades do MVP

| # | Funcionalidade | Essencial? | Quem faz |
|---|---|---|---|
| F1 |  Catálogo de reboques  | Sim | Samuel e João Pedro |
| F2 |  Página de detalhes do reboque  | Não | Samuel e Pedro |
| F3 |  Contato com o dono da loja  | Sim | Samuel e Pietro |
| F4 |  Página inicial  | Sim | João Pedro e Pietro |
| F5 |  Página “Sobre a loja”  | Não | João Pedro e Pedro |
| F6 |  Página de Feedback  | Não | Pedro e Pietro |

---

##  Bloco 6 — Fora do escopo

- ❌ Pagamento pelo app
- ❌ Entrega do reboque pelo app
- ❌ Chat/atendimento em tempo real com vendedor

---

##  Bloco 7 — Caminho técnico

- [x] **Opção A — Room:** reboques e solicitações salvos no próprio celular.

**Bibliotecas:** Room, Jetpack Compose, Android Jetpack

**`try/catch`:**
- **Pode falhar:** cadastrar, consultar ou excluir uma solicitação de orçamento no banco.
- **Mensagem ao usuário:** "Não foi possível realizar a operação. Tente novamente."

---

##  Bloco 8 — Identidade visual

| Item | Definição |
|---|---|
| Nome exibido (`strings.xml`) | Reboque Fácil |
| Cor principal | `#FF9800` |
| Ícone (512×512) | Reboque visto de lado, remetendo aos produtos da loja |
| `applicationId` | `br.edu.ifpe.jsreboques` |
| Versão inicial | `1.0` (versionCode `1`) |

---

##  Bloco 9 — Equipe, papéis e riscos

| Integrante | Responsável por |
|---|---|
| Joao Pedro | Tela principal e catálogo |
| Joao Pietro | Banco de dados com Room |
| Pedro Henrique | Cores, imagens, ícone |
| Samuel Verissimo | README, testes, APK |

**Riscos:**

| Risco | Plano B |
|---|---|
| Room apresentar erros | Simplificar entidades e operações do banco |
| Não dar tempo de implementar tudo | Priorizar catálogo e visualização de detalhes |

---

##  Bloco 10 — Acordo de trabalho com IA

**Regras para o `AGENTS.md`:**
1. A IA segue as funcionalidades definidas no Canvas e no PRD.
2. Todo código gerado é revisado e entendido pelo grupo antes de aceitar.
3. Nenhuma alteração importante entra sem um integrante ler e compreender o código.

O grupo segue os combinados padrão do curso (revisar antes de aceitar, comentário de fronteira de quem aceitou, revisão conjunta antes de cada marco, nenhuma chave de API no prompt) e, adicionalmente, testa cada funcionalidade antes de qualquer entrega. Quem implementa uma parte apresenta o funcionamento dela para o resto do grupo.

---



##  Bloco 12 — Definição de pronto

- [ ] A tela principal mostra os reboques cadastrados no banco.
- [ ] O usuário consegue abrir um reboque e ver seus detalhes.
- [ ] O usuário consegue registrar uma solicitação de orçamento.
- [ ] O app não fecha sozinho e nunca mostra tela branca — erro sempre vira mensagem clara.
- [ ] App com nome, ícone e cor próprios.
- [ ] Duas pessoas de fora do grupo testaram o `.apk` sem explicação.
- [ ] `README.md`, `docs/USO_DE_IA.md` e `AGENTS.md` preenchidos.
- [ ] Todo arquivo tem o comentário de fronteira de quem mexeu nele.

---

##  Validação do professor

| | |
|---|---|
| **Data** | |
| **Situação** | ( ) Aprovado ( ) Aprovado com ajustes ( ) Refazer |
| **Observações** | |
