# Jornada 3 — Painel administrativo (SaaS)

Critérios de aceite para testes funcionais do painel do dono do produto, usados como orientação para o Playwright.

> **Status:** painel em `admin/` (porta 4400) + `GET /api/v1/admin/metrics` e `/accounts`. Acesso por allowlist `ADMIN_EMAILS` (custom claim Firebase antes de produção).

- **App:** `admin/` no monorepo (futuro repositório próprio; não fica no front de orçamentos)
- **URL local:** `http://localhost:4400/admin` (métricas) · `http://localhost:4400/admin/clientes` (contas) · login em `/login`
- **API:** `GET /api/v1/admin/metrics` · `GET /api/v1/admin/accounts`
- **Escopo:** acesso restrito, métricas do negócio, listagem de contas

---

## Decisões que precisam estar fechadas antes dos testes

| Tópico | Situação |
|--------|----------|
| Mesmo repositório ou app separado | **Decidido:** Admin em repositório próprio. Split: backend, LP, Admin, front de orçamentos (`docs/status-projeto.md`) |
| Como o papel de admin é atribuído | **Agora:** e-mail em `ADMIN_EMAILS`. **Antes de produção:** custom claim Firebase `admin: true` |
| Origem do MRR | Contas Pro × R$ 24,90 (`MercadoPagoProperties.PRO_AMOUNT`) |
| Critério de "usuário ativo" | `last_login` dentro do período selecionado (7/30/90 dias) |

---

## Pré-condições

| Item | Valor |
|------|-------|
| Conta admin | Usuário Firebase com o papel de administrador |
| Conta comum | Usuário Firebase sem o papel, para validar bloqueio |
| Dados | Ao menos duas contas, uma no plano Free e outra no Pro, com clientes e propostas |

---

## Convenções para o Playwright

- Usar duas sessões distintas (contextos separados) para admin e usuário comum, evitando login e logout repetidos.
- As métricas vêm da API. Validar consistência comparando com a resposta de `GET /api/v1/admin/metrics`, não com valores fixos no teste.
- Aguardar o loader desaparecer antes de ler os indicadores.
- Testes de acesso negado devem verificar tanto a interface quanto o status HTTP da API.

---

## Cenário 1 — Controle de acesso

**Dado** que estou autenticado como usuário comum  
**Quando** acesso `/admin`  
**Então** o acesso é negado

Critérios de aceite:

- [ ] Usuário sem sessão que acessa `/admin` é redirecionado para `/login`.
- [ ] Usuário comum autenticado não acessa `/admin` e vê mensagem de acesso negado ou é redirecionado para `/dashboard`.
- [ ] A API retorna `403` para `GET /api/v1/admin/metrics` com token de usuário comum.
- [ ] A API retorna `401` para a mesma rota sem token.
- [ ] Usuário admin acessa `/admin` normalmente.
- [ ] O link para o painel só aparece na navegação quando a conta é admin.
- [ ] Perder o papel de admin remove o acesso na próxima renovação de token.

---

## Cenário 2 — Métricas do painel

**Dado** que estou autenticado como admin  
**Quando** acesso `/admin`  
**Então** vejo os indicadores do negócio

Critérios de aceite:

- [ ] Enquanto carrega, apenas o loader é exibido; os cartões não aparecem antes dos dados.
- [ ] Os indicadores abaixo são exibidos com valor numérico, sem `NaN`, `undefined` ou vazio:

| Métrica | Descrição |
|---------|-----------|
| Total de usuários | Contas cadastradas (Free + Pro) |
| Usuários ativos | Conforme o critério definido |
| Contas Free / Pro | Distribuição por plano |
| Novos usuários | Cadastros no período selecionado |
| Assinaturas e cancelamentos | Movimentação do plano Pro no período |
| MRR | Receita recorrente mensal |
| Total de orçamentos | Propostas de todas as contas |
| Total de clientes | Contatos cadastrados por todas as contas |

- [ ] A soma de contas Free e Pro é igual ao total de usuários.
- [ ] Usuários ativos nunca excede o total de usuários.
- [ ] Valores monetários são exibidos em pt-BR (`R$ 1.234,56`).
- [ ] Sem dados no período, os indicadores mostram zero, não estado quebrado.

---

## Cenário 3 — Filtro de período

- [ ] O painel oferece seleção de período (7, 30, 90 dias e **Tudo**, sem limite de data).
- [ ] Trocar o período dispara nova consulta e atualiza os indicadores dependentes de janela temporal.
- [ ] Métricas acumuladas, como total de usuários, não mudam com o filtro (exceto em **Tudo**, quando as temporais passam a ser o acumulado).
- [ ] O período selecionado permanece após recarregar a página, ou volta ao padrão de forma previsível.

---

## Cenário 4 — Listagem de contas

- [ ] A lista exibe nome, e-mail, plano, data de cadastro e situação da conta.
- [ ] A busca filtra por nome e e-mail **sem botão Filtrar** (atualiza sozinha).
- [ ] O filtro por plano restringe corretamente a Free ou Pro e atualiza a lista na hora.
- [ ] Cabeçalhos da tabela ordenam a lista (nome, e-mail, plano, cadastro, situação).
- [ ] "Redefinir senha", alteração de plano e cancelamento abrem um modal de confirmação (não o `alert`/`confirm` do navegador).
- [ ] Alteração de plano (Free ↔ Pro) pede confirmação e persiste na API.
- [ ] Cancelamento de conta pede confirmação, desativa o acesso e não permite cancelar a própria conta admin.
- [ ] A paginação avança e retrocede, com botões desabilitados nos limites.
- [ ] Lista vazia após filtro exibe estado vazio explicativo.
- [ ] O painel não expõe hash de senha, token do Firebase nem dados de pagamento.

---

## Cenário 5 — Privacidade e segurança

- [ ] O admin não visualiza o conteúdo das propostas de outras contas, apenas os totais agregados.
- [ ] Nenhuma resposta da API de admin inclui `passwordHash` ou `firebaseUid`.
- [ ] As rotas de admin não aparecem na documentação pública sem autenticação.
- [ ] Ações administrativas destrutivas, se existirem, pedem confirmação explícita.

---

## Cenário 6 — Erros e responsividade

- [ ] Com a API indisponível, o painel exibe mensagem de erro em vez de tela em branco.
- [ ] Falha em uma métrica não impede a exibição das demais, quando forem consultas independentes.
- [ ] Em viewport de 390px, os cartões empilham e não há rolagem horizontal.
- [ ] A tabela de contas vira cards no mobile ou tem rolagem horizontal contida.

---

## Checklist de fechamento da jornada

- [ ] Acesso negado validado para usuário sem sessão e para usuário comum, na interface e na API.
- [ ] Todos os indicadores conferem com a resposta da API.
- [ ] Nenhum dado sensível exposto nas respostas.
- [ ] Nenhum erro no console durante a execução.
