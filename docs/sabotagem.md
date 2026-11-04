# Relatório dos Testes de Sabotagem — Os Quatro Estados da Tela

Este documento registra as cinco simulações de falha realizadas de propósito no aplicativo **JS Reboques** para validar a resiliência e a tratativa dos quatro estados da tela (`Carregando`, `Vazio`, `Erro` e `Sucesso`), conforme exigido na Atividade 07.

---

## 📋 Tabela de Testes de Sabotagem

| Sabotagem | Como provocar | O que apareceu na tela | Resultado no App |
| :--- | :--- | :--- | :--- |
| **1. Lista Vazia** | Apagar os registros ou iniciar busca sem resultados | Exibição do componente `MensagemCentral` com o texto "Você ainda não cadastrou nada. Toque no botão abaixo para começar." e botão para adicionar | ✅ **Aprovado** (Sem tela branca) |
| **2. Sem Internet** | Ativar o modo avião durante a sincronização de clientes (Retrofit / `IOException`) | Exibição de `MensagemCentral` com "Sem conexão. Verifique a internet e tente novamente." e botão "Tentar novamente" | ✅ **Aprovado** (Erro amigável capturado) |
| **3. Carregando Visível** | Adicionar um `delay(2000)` temporário no método `carregar()` do ViewModel | Exibição do indicador de progresso girando (`CircularProgressIndicator`) centralizado | ✅ **Aprovado** (Estado `Carregando` visível) |
| **4. Campo Obrigatório Vazio** | Tentar salvar um formulário de cadastro sem preencher o nome ou título | Exibição do aviso de erro no campo ("Obrigatório") e impedimento de salvamento | ✅ **Aprovado** (Validação sem crash) |
| **5. Erro do Servidor / Exceção** | Forçar uma exceção genérica (`throw Exception("Falha simulada")`) no DAO/API | Captura pelo catch `Exception` exibindo "Não foi possível carregar os dados." e botão "Tentar novamente" | ✅ **Aprovado** (Rede de segurança ativa) |

---

## 🔍 Análise do Comportamento

1. **Garantia de Exaustividade (`when (val atual = estadoUi)`)**:
   A utilização da `sealed interface UiState<out T>` sem a instrução `else ->` garante que todas as telas tratam exatamente os 4 estados em tempo de compilação.
2. **Uso do Botão "Tentar Novamente"**:
   Ao clicar no botão "Tentar novamente", a função `carregar()` do ViewModel é acionada novamente, redefinindo o estado para `UiState.Carregando` antes de realizar nova busca.
3. **Limpeza de Código**:
   Todas as sabotagens temporárias (como `delay(2000)` e exceções forçadas) foram removidas antes do commit final.
