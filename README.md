# Mônica: A Casa Inteligente
Imagine chegar em casa e ela já saber o que você precisa. Mônica é o painel central da Smart Home 2050: cadastra e monitora os dispositivos conectados da casa, organiza rotinas do dia a dia como iluminação, rega e climatização, e controla quem pode acessar o quê — com permissões diferentes para o morador e para convidados. Tudo em um só lugar, feito pra quem vive ali.

## "Just Monika." 💚

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

**Você precisa de:** Docker, Java 17+ e (opcional) um celular para conectar o WhatsApp.

**1. Suba o banco de dados e a Evolution API (WhatsApp)**
```bash
docker compose up -d
```

**2. Inicie a Mônica**
```bash
cd backend/casa
./mvnw spring-boot:run
```
A API fica em `http://localhost:8080`. Na primeira execução, a Mônica cria um administrador de demonstração:
`admin@monica.local` / `admin123` *(troque a senha e o telefone em `application.properties` antes de usar de verdade)*.

**3. Cadastre um dispositivo**
```bash
curl -u admin@monica.local:admin123 -X POST http://localhost:8080/api/dispositivos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Luz da Sala","tipo":"ILUMINACAO","comodo":"Sala","acessoConvidado":true}'
```

**4. Agende uma rotina**
```bash
curl -u admin@monica.local:admin123 -X POST http://localhost:8080/api/rotinas \
  -H "Content-Type: application/json" \
  -d '{"nome":"Acender a sala","horario":"18:30","diasSemana":"SEG,TER,QUA,QUI,SEX","acao":"LIGAR","dispositivoId":1}'
```
Quer ver funcionando agora? Dispare a rotina na hora: `POST /api/rotinas/1/executar`.

**5. Conecte o WhatsApp (opcional)**
Crie a instância em `POST /api/admin/whatsapp/instancia`, leia o QR Code em `GET /api/admin/whatsapp/qrcode` e escaneie com o WhatsApp. Teste em `POST /api/admin/whatsapp/teste`.

## 🧰 Tecnologias

**Java 17 · Spring Boot (Web, Security, Data JPA, Scheduled) · PostgreSQL · Docker · Evolution API (WhatsApp)**
Interface web: React + Tailwind CSS *(em desenvolvimento)*. Protótipo no Figma e gestão no Trello.

## 📚 Quer saber mais?

Requisitos, modelagem de dados, fluxograma e arquitetura estão na [Documentação do Sistema](./docs/DOCUMENTACAO.md)