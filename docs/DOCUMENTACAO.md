# 🏠 Documentação do Projeto - Casa Inteligente 2050: Mônica

> Painel central de gerenciamento inteligente para residências, com controle de acesso, automação de rotinas e monitoramento de dispositivos em tempo real.

---

## 1. Visão Geral do Produto

O projeto **Mônica** foi concebido para ser o painel central de uma casa inteligente em 2050. O sistema centraliza o cadastro e monitoramento de dispositivos conectados, automatiza rotinas diárias (como iluminação, rega e climatização) e gerencia permissões de acesso diferenciadas entre moradores (administradores) e convidados (usuários comuns).

---

## 2. Escopo e Funcionalidades Principais

### 🔒 Autenticação e Gestão de Acessos
- **Perfil Administrador (Morador):** Acesso total ao sistema, cadastro/remoção de dispositivos, criação e edição de rotinas automatizadas e gerenciamento de permissões de usuários.
- **Perfil Usuário Comum (Convidado):** Acesso limitado para acionamento pontual de dispositivos permitidos pelo administrador, sem acesso a configurações avançadas ou criação de rotinas.

### 📱 Gestão de Dispositivos Conectados
- Cadastro de novos dispositivos da casa.
- Visualização do status em tempo real (ligado/desligado, temperatura, nível, etc.).
- Histórico de uso e acionamento.

### ⏰ Agendamento e Automação de Rotinas
- Agendamento de rotinas para rega de plantas, iluminação por horário e climatização ambiente.
- Execução automática de tarefas em segundo plano.

### 💬 Notificações e Central de Alertas
- Envio de notificações e alertas operacionais via WhatsApp para o morador.

---

## 3. Arquitetura e Tecnologias (Stack)

### Back-End
- **Linguagem:** Java 17+
- **Framework:** Spring Boot
  - *Spring Web:* Exposição de APIs RESTful.
  - *Spring Security:* Autenticação e autorização via JWT / Sessão.
  - *Spring Data JPA:* Mapeamento objeto-relacional e persistência de dados.
  - *Spring @Scheduled:* Automação e execução de tarefas agendadas.

### Front-End
- **Biblioteca/Framework:** React (ou Vue.js)
- **Estilização:** Tailwind CSS
- **Consumo:** Cliente HTTP (Axios/Fetch) consumindo a API REST.

### Banco de Dados & Infraestrutura
- **Banco de Dados:** PostgreSQL (Banco relacional).
- **Containerização:** Docker & Docker Compose.
- **Integração Externa:** Evolution API (Envio de mensagens via WhatsApp rodando via Docker).

---

## 4. Requisitos do Sistema

### Requisitos Funcionais (RF)
- **RF01:** O sistema deve permitir o cadastro e login de usuários com identificação de perfis (Admin e Comum).
- **RF02:** O sistema deve permitir que o Admin cadastre, edite, liste e remova dispositivos conectados.
- **RF03:** O sistema deve permitir o agendamento de rotinas automatizadas pelo perfil Admin.
- **RF04:** O sistema deve restringir o acesso a recursos administrativos quando um Usuário Comum estiver autenticado.
- **RF05:** O sistema deve disparar notificações via WhatsApp para o Admin ao acionar rotinas estratégicas.

### Requisitos Não-Funcionais (RNF)
- **RNF01:** A interface deve ser responsiva e intuitiva (UX/UI centrada no usuário morador).
- **RNF02:** As senhas de acesso devem ser salvas no banco de dados de forma criptografada (BCrypt).
- **RNF03:** Os dados devem ser persistidos em banco de dados relacional PostgreSQL.
- **RNF04:** A aplicação deve validar os tipos e formatos dos dados de entrada antes de processar as requisições.

---

## 5. Próximas Etapas da Documentação

- [ ] **Modelagem de Dados (DER / ERD):** Mapeamento de tabelas, atributos e relacionamentos.
- [ ] **Fluxograma do Sistema:** Mapeamento completo do fluxo de navegação e requisições HTTP da aplicação.
- [ ] **Protótipo Figma:** Links e previews das telas finalizadas.
