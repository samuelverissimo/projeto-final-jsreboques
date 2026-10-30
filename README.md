<img width="1280" height="720" alt="jsreboques-logo (2)" src="https://github.com/user-attachments/assets/428e09aa-5c60-4733-8d52-ac3b423057a8" />

# JS Reboques — Gerenciamento de Vendas e Estoque
Projeto final da matéria de Desenvolvimento para Dispositivos Móveis, ministrada pelo docente Hélio Bentzen. 
Projeto desenvolvido pela equipe MaineProg, composto pelos integrantes: Samuel Veríssimo, Pedro Henrique, João Pedro e João Pietro.

Postulantes na turma B do 3º ano do ensino médio.

--------------------------------------------------------------------------------------------------------------------------------------------------

Link do Figma: https://www.figma.com/design/SnhULfcoE15uEXtuwAe9NK/Untitled?node-id=0-1&t=WjCEubKgeaSU7BgW-1

Este aplicativo foi desenvolvido para gerenciar o estoque e as vendas da **JS Reboques**. Ele utiliza uma arquitetura offline-first com banco de dados Room para garantir a integridade dos dados mesmo sem conexão.

## 🚀 Funcionalidades Principais

- **Gestão de Vendas (Implementado por João Pedro):**
    - Registro de vendas vinculado a Clientes e Reboques.
    - Validação automática de estoque (não permite vender o que não há em estoque).
    - Histórico detalhado de movimentações.
    - Suporte a valores monetários em centavos para evitar erros de precisão.
- **Controle de Estoque:** Monitoramento em tempo real de entradas (compras) e saídas (vendas).
- **Cadastro de Clientes:** Base de dados local para agilizar o processo de venda.

## 🛠️ Tecnologias Utilizadas

- **Kotlin** com Jetpack Compose para a interface.
- **Room Database** para persistência local.
- **Navigation Compose** para fluxo entre telas.
- **Coroutines & Flow** para operações assíncronas e reativas.
