# Status do Projeto

> **Documento vivo do MVP.** Consultar **sempre** antes de informar status ao usuário ou iniciar uma nova tarefa.
> Atualizar este arquivo ao concluir, pausar ou mudar o escopo de qualquer entrega relevante.

**Última atualização:** 5 de setembro de 2026 — split em 4 repos locais em `mvp-propostas/{api,app,lp,admin}`

---

## Resumo rápido

| Item | Valor |
|------|--------|
| **Fase atual** | Código em 4 repos; falta provisionar homologação (GCP/Neon/DNS) |
| **Branch ativa** | `main` em cada repo (`mvp-propostas-api`, `-app`, `-lp`, `-admin`) |
| **Último commit** | ver `git log -1` no repo em que estiver |
| **PR aberto** | — |
| **Repositório** | Quatro privados em `leozin00/`: `mvp-propostas-api`, `mvp-propostas-app`, `mvp-propostas-lp`, `mvp-propostas-admin`. Pasta local: `mvp-propostas/{api,app,lp,admin}` |
| **Stack** | Angular 21 + Tailwind CSS 4 · Spring Boot 4 · PostgreSQL · Docker Compose |
| **Apps frontend** | `frontend/` (orçamentos, 4200) · `landing-page/` (LP, 4300) · `admin/` (painel, 4400) |
| **Branding** | **Gestão de Propostas** · cor `#0b2b6f` · ver `docs/branding.md` |
| **Planos** | Free + Pro R$ 24,90/mês — ver `docs/monetization.md` |
| **Mercado Pago** | App **GestaoPropostas** (ID `4673848288729614`) · produto assinatura · site MLB |
| **MP homologação** | App **GestaoPropostasSandbox** (ID `334690360835903`) criada **dentro da conta vendedor de teste** `3642253921`; comprador de teste `3642253919` |

### O que funciona hoje (integrado à API)

- Health check (`/api/v1/health`)
- Login / cadastro no **Firebase Auth** (e-mail/senha e Google no projeto `gestao-propostas-mvp`)
- **API protegida** com ID token Firebase (`Authorization: Bearer`); usuário interno criado/vinculado na primeira chamada
- Dashboard com métricas reais (`/api/v1/analytics/dashboard`)
- Analytics do perfil (`/api/v1/analytics/profile`)
- Notificações de status no header (`/api/v1/analytics/notifications`) — badge só para notificações **novas** (controle por usuário no `localStorage`)
- Navegação padronizada com `app-back-link` em detalhe/form de clientes e propostas
- CRUD de **clientes** (`/api/v1/clients`) — lista, detalhe, form, exclusão (bloqueada se houver propostas)
- CRUD de **propostas** com itens, duplicar e publicar (`/api/v1/proposals`) — edição só em rascunho
- **Cadastro inline de cliente** ao criar nova proposta (cliente criado na API ao salvar/publicar)
- Compartilhar proposta via WhatsApp + copiar link (telefone do cliente vindo da API)
- **Página pública da proposta** (`GET /api/v1/public/proposals/{token}`) — lê proposta publicada por token; marca `SENT` → `VIEWED` no primeiro acesso
- **Aprovar / recusar** na página pública (`POST .../approve` e `POST .../reject`) — só em `SENT`/`VIEWED`; proposta expirada não responde
- Sentry em desenvolvimento (frontend + backend)
- Seed de dados demo no banco (`V3__seed_demo_data.sql` + `V4__seed_proposal_items.sql`)
- **Limites Free** validados na API: 10 clientes cadastrados e 5 orçamentos por ciclo mensal da conta; CTA de upgrade no frontend
- **Painel admin** (`admin/` :4400): métricas e listagem de contas via `/api/v1/admin/*`; acesso por `ADMIN_EMAILS`
- **PDF** da proposta (app autenticado e página pública)
- **Verificação de e-mail** no cadastro e escolha Free/Pro
- **LP** alinhada ao produto (link/PDF/aprovação no Free; Pro = volume ilimitado)

### O que ainda é mock no frontend

- Nada no fluxo principal. Logo da empresa e catálogo de produtos são **MVP 2**.

---

## Histórico de alterações

Registrar aqui cada entrega relevante (mais recente primeiro).

| Data | Branch / commit | Entrega |
|------|-----------------|---------|
| 2026-09-05 | 4 repos `main` | Extração do monorepo: pastas locais `api/`, `app/`, `lp/`, `admin/` com GitHub privado cada uma |
| 2026-09-02 | `feature/mercadopago-checkout-sandbox` | Limites Free enforced na API (10 clientes, 5 orçamentos/ciclo) + usage no billing e CTA/banner no frontend |
| 2026-09-02 | `355ff90` | Área admin: app Angular na porta 4400 + `GET /api/v1/admin/metrics` e `/accounts`; allowlist `ADMIN_EMAILS`; `last_login`; custom claim Firebase fica para produção |
| 2026-09-02 | — | Decisão: separar em repositórios distintos (backend, LP, Admin, front de orçamentos). TODO: gerar PDF como opção ao envio por WhatsApp |
| 2026-08-26 | `feature/mercadopago-checkout-sandbox` | Limites Free enforced: 10 clientes e 5 orçamentos/ciclo mensal da conta, com CTA de upgrade para Pro |
| 2026-08-26 | `feature/mercadopago-checkout-sandbox` | Checkout sandbox homologado: fluxo Perfil → Assinar Pro → confirmação no Mercado Pago concluído com contas de teste |
| 2026-08-26 | `feature/crud-clientes-propostas` (local) | Homologação isolada MP **funcionando**: app `GestaoPropostasSandbox` (`334690360835903`) criada dentro da conta vendedor de teste `3642253921`; `preapproval` aceito com vendedor e comprador de teste |
| 2026-08-26 | `feature/crud-clientes-propostas` (local) | Fix: e-mail do usuário de teste deriva do **nickname** (`TESTUSER123` → `test_user_123@testuser.com`), não do User ID — `MercadoPagoClient.resolveTestPayerEmail` consulta `GET /users/{id}` |
| 2026-08-26 | `feature/crud-clientes-propostas` (local) | Homologação isolada MP: OAuth dev para token do vendedor de teste (`MERCADOPAGO_SANDBOX_ACCESS_TOKEN`) + comprador via `MERCADOPAGO_TEST_PAYER_USER_ID` |
| 2026-08-26 | `feature/crud-clientes-propostas` (local) | Fix checkout 400: em sandbox com token TEST- do painel, `payer_email` usa e-mail real do app (MP rejeita @testuser.com quando vendedor é conta real) |
| 2026-08-26 | `feature/crud-clientes-propostas` (local) | Homologação MP: redirect usa `init_point`; botão "Já paguei" documentado como temporário (só local) |
| 2026-08-25 | `feature/crud-clientes-propostas` (local) | App Mercado Pago **GestaoPropostas**; checkout Pro (preapproval R$ 24,90/mês), webhook, sync e CTA no perfil |
| 2026-08-25 | — | Gateway **Mercado Pago** escolhido para checkout Pro; MCP configurado em `.cursor/mcp.json` |
| 2026-08-25 | — | Teste funcional manual da jornada 1 (Playwright MCP): cadastro → cliente → proposta → publicar → aprovar na página pública — fluxo principal aprovado |
| 2026-08-24 | `feature/crud-clientes-propostas` · `a68b3f3` | `app-back-link` em detalhe/form; ações no dashboard; badge de notificações só para não lidas; critérios de teste funcional das 3 jornadas; correção do loader nas listagens |
| 2026-08-24 | `feature/crud-clientes-propostas` · `f4f2eac` | Auth na API: valida ID token Firebase, provisiona usuário interno (`firebase_uid`), protege rotas privadas; interceptor Bearer no frontend |
| 2026-08-24 | `feature/crud-clientes-propostas` · `1383f50` | Firebase Auth no frontend (e-mail/senha e Google); cor primária `#0b2b6f`; login Apple fora do MVP |
| 2026-08-24 | `feature/crud-clientes-propostas` · `8be668d` | CRUD real de clientes e propostas (API + telas); cadastro inline de cliente na nova proposta; correção de race condition em detalhe de cliente/proposta; desconto R$/% compacto; testado com Playwright |
| 2026-08-24 | `feature/landing-page` · `24abcb2` | LP: imagem de negociação no hero (com mock do orçamento) e animações discretas ao entrar no viewport |
| 2026-08-23 | `feature/landing-page` | Novo app Angular em `landing-page/` (porta 4300): LP de aquisição com seções da spec, preços reais, CTAs para `/register` e `/login` do app |
| 2026-08-23 | `feature/tailwind-setup` · `fdaa751` | Status do projeto como documento vivo |
| 2026-08-23 | `feature/tailwind-setup` · `6980caa` | Notificações de status no sino do header; endpoint `GET /analytics/notifications`; layout mobile (cards nas listagens, overflow corrigido); compartilhar proposta via WhatsApp + copiar link; duplicar proposta; desconto em % no formulário; `cursor: pointer` global em botões |
| 2026-08-23 | `feature/tailwind-setup` · `7ef2f83` | Correção do setup Tailwind (PostCSS, `styles.css`); accordion no perfil com propostas aprovadas por mês |
| 2026-08-23 | `feature/tailwind-setup` · `6cf704e` | Migração das páginas principais de SCSS para Tailwind CSS |
| 2026-08-22 | `feature/frontend-setup` · `3676cfc` | Sentry no frontend e backend (ambiente development) |
| 2026-08-22 | `feature/frontend-setup` · `1814e80` | Integração dashboard e perfil com API de analytics; componente `app-loader`; interceptor de erro de sistema |
| 2026-08-22 | `feature/frontend-setup` · `6b7c612` | Backend Spring Boot inicial + health check no frontend |
| 2026-08-20 | `feature/frontend-setup` · `f2ac878` | Telas do frontend com dados mockados e polish visual |
| 2026-08-20 | `main` · `78d15ca` | Documentação do produto, arquitetura, backlog e fundação Angular |

---

## Ponto atual (detalhado)

### Frontend

| Módulo | Estado | Observação |
|--------|--------|------------|
| Layout privado (sidebar + topbar) | ✅ Pronto | Responsivo; sino de notificações no header |
| Dashboard | ✅ API | Métricas e propostas recentes do backend; ações na lista (desktop); cards mobile |
| Perfil / configurações | ✅ API | Analytics reais; aba Configurações com dados pessoais e empresa editáveis; plano lido da API; **upgrade Pro via Mercado Pago** |
| Clientes (lista, detalhe, form) | ✅ API | CRUD por usuário autenticado; `forkJoin` no detalhe; exclusão bloqueada se houver propostas; `app-back-link` |
| Propostas (lista, detalhe, form) | ✅ API | Itens, desconto R$/%, duplicar, publicar (`public_token`); cadastro inline de cliente na **nova** proposta; `app-back-link` |
| Login / cadastro | ✅ Firebase + API | E-mail/senha e Google; verificação de e-mail; escolha Free/Pro; `/register?plan=pro` |
| Página pública da proposta | ✅ API | Leitura por token; aprovar/recusar; **PDF** |
| Landing page | ✅ V1 honesta | Preços alinhados ao produto; termos e privacidade; CTA Pro com `?plan=pro` |
| Tailwind CSS | ✅ Pronto | `@import 'tailwindcss'` em `styles.css`; verificar com `npm run verify:styles` |
| Notificações | ✅ API | `app-notifications-bell`; badge apenas para notificações não lidas |
| Admin | ✅ API | App `admin/` (porta 4400). Login Firebase; `/admin` com métricas, período 7/30/90 e lista de contas. 403 se o e-mail não estiver em `ADMIN_EMAILS` |

### Backend

| Módulo | Estado | Endpoints |
|--------|--------|-----------|
| Health | ✅ | `GET /api/v1/health` |
| Profile | ✅ | `GET /api/v1/profile` · `PUT /personal` · `PUT /business` |
| Analytics | ✅ | `GET /api/v1/analytics/dashboard`, `/profile`, `/notifications` |
| Auth | ✅ | ID token Firebase; `email_verified` nas rotas privadas |
| CRUD clientes | ✅ | `GET/POST/PUT/DELETE /api/v1/clients` |
| CRUD propostas | ✅ | `GET/POST/PUT/DELETE /api/v1/proposals` + `/publish` + `/duplicate` + `GET /{id}/pdf` |
| Billing / checkout Pro | ✅ | `GET/POST /api/v1/billing` · `POST /checkout` · `POST /sync` · webhook `POST /api/v1/webhooks/mercadopago` · usage/limites no GET billing |
| Limites Free | ✅ | 10 clientes; 5 propostas por ciclo mensal da conta (create + duplicate); HTTP 403 `PLAN_LIMIT_*` |
| Link público / aprovação | ✅ | `GET /api/v1/public/proposals/{token}` · `POST .../approve` · `POST .../reject` · `GET .../pdf` |
| Admin | ✅ | `GET /api/v1/admin/metrics` · `GET /api/v1/admin/accounts` · allowlist `ADMIN_EMAILS` |
| Flyway + seed | ✅ | `V1` schema · `V3` dados demo · `V4` itens · `V5` `firebase_uid` · `V6` `subscriptions` · `V7` `last_login` |

### Infra

| Item | Estado |
|------|--------|
| Docker Compose (PostgreSQL) | ✅ |
| Sentry (dev) | ✅ |
| CI | ✅ | `.github/workflows/ci.yml` — API, três fronts, e2e LP/app/admin |
| README monorepo | ✅ |
| Deploy homolog/prod | 📄 | Workflows prontos; secrets GCP/Firebase/Neon ainda não provisionados. Ensaio local: `docs/quality/ensaio-lancamento.md` |

---

## Próximos passos (prioridade sugerida)

1. **Provisionar homologação** — Firebase stg, Neon, Cloud Run, webhook MP, variables dos GitHub Environments (`docs/deploy.md`).
2. **Ensaio em homolog** — `docs/quality/ensaio-lancamento.md`.
3. **Registrar domínio** e apontar `www` / `app` / `admin` / `api`.
4. Arquivar o GitHub `mvp-propostas` (monorepo antigo) quando não for mais necessário.
5. **MVP 2** — logo da empresa, catálogo, claim `admin: true`.

---

## Observações para fechamento do MVP

> Pendências para considerar o MVP **completo**. Atualizar checkboxes conforme o progresso.

### Autenticação e conta

- [x] Login funcional no frontend (Firebase e-mail/senha, erros)
- [x] Cadastro com validações, verificação de e-mail e escolha de plano
- [x] Integração com Google (OAuth no frontend)
- [ ] Login com Apple — **fora do MVP**
- [x] Recuperação de senha (e-mail Firebase)
- [x] Logout e rotas privadas no frontend
- [x] Guards e interceptor no frontend conectados ao backend
- [x] Backend validando ID token do Firebase
- [x] Verificação de e-mail no cadastro (Firebase + API `email_verified`)

> Login/cadastro autenticam no Firebase. A API valida o ID token e provisiona o usuário interno. Conta com o e-mail do seed (`leonardo@empresa.com`) herda os dados demo.

### Área de perfil e análises

- [x] Dashboard com dados reais da API
- [x] Métricas por status, conversão, valor aprovado (analytics)
- [x] Propostas aprovadas por mês com accordion no perfil
- [x] Perfil editável (dados pessoais e empresa na API)
- [ ] Foto e preferências do vendedor
- [x] Empresa e plano conectados à API (upgrade Pro no perfil)
- [ ] Evolução temporal com histórico suficiente

### Loader das telas (sem interceptor global)

- [x] Componente `app-loader` reutilizável
- [x] Estados de loading em listagens e formulários
- [x] Loader HTTP global — **fora do MVP 1** (retomar se a UX pedir)

### Integração com WhatsApp

- [x] Botão "Enviar por WhatsApp" (detalhe e listagem de propostas)
- [x] Mensagem com título, cliente e link público (`proposal-share.ts`)
- [x] Deep link `wa.me` com telefone do cliente (quando cadastrado)
- [x] Copiar link com tooltip de confirmação
- [x] Link público **real** após publicação na API (`GET /api/v1/public/proposals/{token}`)
- [x] **Gerar PDF** da proposta (sem logo da empresa neste MVP)
- [ ] Registrar envio no histórico (opcional)
- [ ] WhatsApp Business API — **fora do MVP** (usar deep link)

### Monitoramento (Sentry)

- [x] SDK no frontend (`@sentry/angular`)
- [x] SDK no backend (Java)
- [x] Ambiente development configurado
- [x] DSN por ambiente (staging, prod) — mesmo projeto Sentry nos 4 apps; environment `production` nos file replacements
- [ ] Source maps em produção
- [ ] Alertas e dashboards

### Área de admin (SaaS)

- [x] Decidir: **repositório dedicado** (não fica no front de orçamentos)
- [x] App `admin/` no monorepo (porta 4400; extração para repo próprio no split)
- [x] Dashboard admin com métricas (usuários, MRR, planos, etc.)
- [x] Endpoints `/api/v1/admin/*` e allowlist `ADMIN_EMAILS`
- [ ] Custom claim Firebase `admin: true` (antes de produção)

### Checkout de pagamentos

| Fluxo | Prioridade | Status |
|-------|------------|--------|
| Assinatura Pro (usuário → plataforma) | MVP | ✅ Checkout sandbox (falta token local + webhook público) |
| Pagamento na aprovação (cliente → usuário) | Pós-MVP | ❌ Não iniciar |

Detalhes: `docs/monetization.md` e seção completa abaixo (mantida para referência).

---

## Visão geral do produto

SaaS para autônomos e pequenas empresas criarem, publicarem e acompanharem propostas comerciais.

**Fluxo principal:** criar proposta → publicar → cliente visualiza via link → aprova ou recusa → usuário acompanha status.

**Referências de escopo:**
- Produto: `docs/product/`
- API: `docs/architecture/06-api.md`
- Backlog: `docs/quality/11-backlog-implementacao.md`
- Critérios de pronto: `docs/quality/14-criterios-pronto.md`
- Testes funcionais por jornada: `docs/quality/testes-funcionais/`
- Agente: `docs/quality/12-instrucoes-cursor-agent.md`

---

## O que já foi feito (consolidado)

### Documentação
- [x] Visão, escopo, arquitetura, modelo de dados, API, frontend, regras, fluxos, qualidade, instruções do agente

### Frontend
- [x] Angular 21 standalone, rotas lazy-loaded
- [x] Tailwind CSS 4 nas páginas principais
- [x] Layout privado responsivo + notificações no header
- [x] Dashboard e perfil com analytics da API
- [x] Telas de clientes e propostas integradas à API (services HTTP; mocks legados não usados nas páginas)
- [x] Formulário de proposta com desconto R$ / % (toggle compacto) e duplicar proposta
- [x] Cadastro inline de cliente na nova proposta (cria via API ao salvar)
- [x] Compartilhar/copiar link e WhatsApp (frontend)
- [x] Login e cadastro com Firebase Auth no frontend (e-mail e Google)
- [x] Interceptor HTTP com ID token nas rotas privadas
- [x] Listagens mobile com cards (sem overflow horizontal)
- [x] Loader em dashboard/perfil
- [x] Interceptor de erro de sistema
- [x] Componente `app-back-link` padronizado em detalhe/form de clientes e propostas
- [x] Badge de notificações só para itens não lidos
- [x] Loader corrigido nas listagens (conteúdo só após carregar)

### Backend
- [x] Spring Boot 4 + Java 21
- [x] Flyway, PostgreSQL, Docker Compose
- [x] Entidades e seed demo
- [x] Health, profile, analytics (dashboard, perfil, notificações)
- [x] CRUD clientes e propostas (itens, publish, duplicate, soft delete)
- [x] `ResourceNotFoundException` (404) e regras de negócio (rascunho editável, cliente com propostas)
- [x] Sentry (development)
- [x] CORS para `localhost:4200`
- [x] Auth: validação do ID token Firebase + provisionamento de usuário (`firebase_uid`)

### Versionamento
- [x] Git + GitHub
- [x] Branches `feature/frontend-setup`, `feature/tailwind-setup`, `feature/landing-page`, `feature/crud-clientes-propostas`

---

## O que ainda não foi feito

### Setup / repo
- [x] README do monorepo (enquanto o split não acontecer)
- [ ] Split em 4 repositórios (script pronto; execução quando a branch congelar)

### Backlog principal (fases 2–14)
- [x] Autenticação completa (Firebase ID token na API)
- [x] CRUD clientes (API + frontend)
- [x] CRUD propostas e itens (API + frontend)
- [x] Publicação e link público funcional
- [x] Página pública integrada (leitura por token)
- [x] Aprovação e recusa pelo cliente
- [x] Testes Playwright da LP, app e admin em `e2e/` (CI no merge)
- [x] Loader global via interceptor — **fora do MVP 1**
- [ ] Sentry DSN de staging/prod nos deploys
- [x] Área admin (app `admin/` + API; repo próprio no split)
- [ ] Custom claim Firebase para admin (produção)
- [x] Gerar PDF da proposta (opção ao WhatsApp)
- [x] Checkout Pro (sandbox: preapproval + webhook + sync; produção e webhook público pendentes)
- [x] CI (GitHub Actions: build/testes; deploy Cloud Run/Firebase documentado, secrets pendentes)

### Pós-MVP
- [ ] Checkout na aprovação da proposta (cliente paga usuário)

---

## Decisões pendentes

| Tópico | Situação |
|--------|----------|
| Nome do produto / branding | **Gestão de Propostas** (`docs/branding.md`). Domínio `gestaodepropostas.com.br` a registrar. |
| Desconto percentual vs. fixo | **Implementado** no form (% e R$) e enviado como valor fixo à API (backend recalcula totais) |
| Motivo de recusa no modelo | Campo ainda não existe na entidade |
| `VIEWED → EXPIRED` | Ambíguo na documentação |
| Login Google | **Implementado** no Firebase (frontend) + ID token validado na API |
| Split de repositórios | **Decidido:** backend, LP, Admin e front de orçamentos — cada um no seu repo. Hoje o código ainda vive neste monorepo (`admin/` já existe). |
| Papel de admin | **Agora:** allowlist `ADMIN_EMAILS`. **Antes de produção:** custom claim Firebase `admin: true`. |
| PDF da proposta | **Feito (MVP 1, sem logo).** Logo da empresa no PDF = MVP 2. |
| Gateway pagamento Pro | **Mercado Pago** (assinaturas / `preapproval`) — app GestaoPropostas, **homologação** com credenciais `TEST-` |
| Checkout proposta | Fora do MVP |
| Botão "Já paguei — atualizar plano" | **Temporário (só local).** Workaround porque o MP não redireciona para localhost. **Remover da versão final** (já oculto em `environment.production`). |

---

## Estrutura atual do repositório

Monorepo de trabalho. **Decisão (2026-09-02):** separar em quatro repositórios.

| Repo alvo | Conteúdo atual | Papel |
|-----------|----------------|--------|
| Backend | `backend/` | API Spring Boot |
| Front de orçamentos | `frontend/` | App autenticado (gestão de clientes e propostas) |
| Landing page | `landing-page/` | Site público de aquisição |
| Admin | `admin/` | Painel interno do SaaS (métricas, contas) |

```
mvp-propostas/                 ← monorepo até o split
├── AGENTS.md
├── docs/                    ✅ inclui status-projeto.md e landing-page/
├── backend/                 ✅ → repo backend
├── frontend/                ✅ → repo front de orçamentos
├── landing-page/            ✅ → repo LP
├── admin/                   ✅ → repo Admin (porta 4400)
├── tasks/                   ❌
└── docker-compose.yml       ✅ PostgreSQL
```

---

## Como manter este documento

**O agente e o time devem:**

1. Ler este arquivo no início de tarefas que alterem escopo, status ou prioridades.
2. Ao concluir uma entrega, atualizar:
   - **Resumo rápido** (branch, commit, data)
   - **Histórico de alterações** (nova linha na tabela)
   - **Ponto atual** (tabelas frontend/backend)
   - Checkboxes em **Observações para fechamento do MVP**
   - **Próximos passos** se a prioridade mudar
3. Não duplicar escopo detalhado — linkar para `docs/product/`, `docs/architecture/`, etc.

**Gatilhos para atualização obrigatória:**
- merge de PR relevante;
- nova integração API ↔ frontend;
- conclusão ou pausa de fase do backlog;
- mudança de branch ativa ou decisão arquitetural.

---

## Referência — detalhes de módulos futuros

<details>
<summary>Área de admin (métricas MVP)</summary>

| Métrica | Descrição |
|---------|-----------|
| Total de usuários | Contas cadastradas (Free + Pro) |
| Usuários ativos | Critério a definir (ex.: 30 dias) |
| Free / Pro | Por plano |
| Novos usuários | Cadastros no período |
| Assinaturas / cancelamentos | Pro no período |
| MRR | Receita recorrente (Pro R$ 24,90/mês) |
| Total de orçamentos | Propostas de todos os usuários |
| Total de clientes cadastrados | Contatos comerciais dos usuários |

</details>

<details>
<summary>Checkout Pro (MVP) e checkout proposta (pós-MVP)</summary>

**Homologação isolada (sem conta real):** o token `TEST-` do painel tem a **conta real** como vendedor, e o MP exige que pagador e recebedor sejam ambos reais ou ambos de teste — daí o 400 `User bad request` ao usar e-mails `@testuser.com`. A solução é logar em `mercadopago.com.br` **com o usuário vendedor de teste**, criar uma aplicação de Assinaturas nessa conta e usar o Access Token dela em `MERCADOPAGO_SANDBOX_ACCESS_TOKEN`. O e-mail do comprador é derivado do **nickname** (`TESTUSER123` → `test_user_123@testuser.com`), nunca do User ID.

**Fluxo 1 — Pro (MVP, homologação):** upgrade Free → Pro no perfil; `POST /api/v1/billing/checkout` cria `preapproval` com Access Token de **teste** (`TEST-…`). O usuário é redirecionado para `init_point` (URL oficial de homologação/produção; `sandbox_init_point` está depreciado). O `back_url` precisa ser HTTPS público (localhost é rejeitado); em local usamos um placeholder e o botão temporário de sync. Em produção o `back_url` deve ser `https://<domínio>/profile`. Webhook público e credenciais `APP_USR-` de produção ainda pendentes.

**Fluxo 2 — Proposta (pós-MVP):** cliente paga usuário na aprovação; retomar após MVP estável e análise de preços/taxas.

</details>
