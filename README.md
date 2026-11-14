# Mônica: A Casa Inteligente
Imagine chegar em casa e ela já saber o que você precisa. Mônica é o painel central da Smart Home 2050: cadastra e monitora os dispositivos conectados da casa, organiza rotinas do dia a dia como iluminação, rega e climatização, e controla quem pode acessar o quê — com permissões diferentes para o morador e para convidados. Tudo em um só lugar, feito pra quem vive ali.

## "Just Monika." 💚

<p align="center">
  <img src="assets/monica-demo-optimized.gif" alt="Mônica, a assistente da casa inteligente" width="480">
</p>

---

## ✨ O que a Mônica faz

- 💡 **Controla a casa inteira num só painel:** luzes, climatização, irrigação e segurança, com status em tempo real.
- ⏰ **Cuida da rotina sozinha:** "apagar as luzes às 22h", "regar o jardim às 6h". A Mônica verifica os horários a cada minuto e executa por você.
- 💬 **Avisa no seu WhatsApp:** rotina executada, sistema de segurança desligado... você recebe a mensagem direto no celular, sem precisar abrir o painel.
- 🔐 **Cada pessoa no seu lugar:** o morador (Administrador) controla tudo; o convidado só aciona o que o morador liberar.
- 🕘 **Guarda o histórico:** quem ligou, quem desligou, e se foi uma pessoa ou uma rotina.

## 🎯 Objetivo

Mostrar como uma casa pode ser organizada, segura e prática com uma central *self-hosted*: os dados ficam com o morador, e a automação funciona mesmo sem depender de apps de terceiros.

Projeto da categoria **INTELLIGENCE (4º semestre, Ciência da Computação)** da **ExpoTech 2026.2 — Missão 2050: Smart Home & Tecnologias do Futuro** (UniFECAF).

## 👥 Quem usa

| Perfil | O que pode fazer |
|---|---|
| **Administrador (morador)** | Cadastrar e remover dispositivos, criar rotinas, liberar dispositivos para convidados, ver alertas e histórico, ajustar as configurações |
| **Usuário comum (convidado)** | Ligar e desligar apenas os dispositivos liberados pelo morador |

## 🚀 Como usar

**Você precisa de:** Docker, Java 17+, Node.js 18+ e (opcional) um celular para conectar o WhatsApp.

**1. Configure as chaves e suba o banco de dados e a WAHA (WhatsApp)**
```bash
cp .env.example .env        # edite WAHA_API_KEY e a senha do painel da WAHA
docker compose up -d
```
O PostgreSQL sobe na porta `5432` e a WAHA em `http://localhost:3000` (painel em `/dashboard`).

**2. Inicie a Mônica**
```bash
cd backend/casa
./mvnw spring-boot:run
```
A API fica em `http://localhost:8080`. Na primeira execução, a Mônica cria um administrador de demonstração:
`admin@monica.local` / `admin123` *(troque a senha e o telefone em `application.properties` antes de usar de verdade)*.

**3. Inicie o painel web**
```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```
Acesse `http://localhost:5173` e entre com o administrador de demonstração. Pelo painel você cadastra dispositivos, cria rotinas (ou executa uma na hora), libera dispositivos para convidados e acompanha alertas e histórico.

**4. Conecte o WhatsApp (opcional)**
No painel web: **Configurações → Segurança**. A tela mostra o QR Code sozinha quando a sessão estiver aguardando pareamento; abra o WhatsApp no celular em *Aparelhos conectados* e escaneie. Quando o status ficar **Conectado**, clique em *Enviar teste*.

Pela API: `GET /api/admin/whatsapp/status` (estado), `POST /api/admin/whatsapp/instancia` (garante a sessão), `GET /api/admin/whatsapp/qrcode` (QR em base64) e `POST /api/admin/whatsapp/teste`.

> A WAHA Core (gratuita) aceita **uma única sessão, chamada `default`**. Se aparecer erro 401, confira se `WAHA_API_KEY` no `.env` é a mesma usada no `docker compose`.

## 🧰 Tecnologias

**Java 17 · Spring Boot (Web, Security, Data JPA, Scheduled) · PostgreSQL · Docker · WAHA (WhatsApp)**
Interface web: React + Tailwind CSS. Protótipo no Figma e gestão no Trello.

## 📚 Quer saber mais?

Requisitos, modelagem de dados, fluxograma e arquitetura estão na [Documentação do Sistema](./docs/DOCUMENTACAO.md)
