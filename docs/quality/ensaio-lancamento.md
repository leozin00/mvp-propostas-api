# Ensaio de lançamento (homologação)

Rodar **uma vez em homologação** como usuário real. Só depois apontar DNS de produção e abrir o cadastro.

Contas Firebase de teste **somente em homolog**. Não usar o seed `leonardo@empresa.com` em produção.

## Checklist

1. [ ] Criar conta Free, confirmar e-mail, bater limite (10 clientes ou 5 orçamentos), ver CTA Pro.
2. [ ] Criar conta Pro pelo cadastro (`/register?plan=pro`), pagar no Mercado Pago (sandbox em stg).
3. [ ] Publicar proposta, abrir o link `/p/{token}` no celular, baixar PDF, aprovar.
4. [ ] Entrar no admin, ver a conta e o MRR.
5. [ ] Conferir Sentry sem eventos de teste (`APP_DEBUG_SENTRY_TEST_ENABLED=false`).
6. [ ] Health `GET /api/v1/health` = 200 após cold start.
7. [ ] Webhook MP HTTPS recebido (`subscription_preapproval`).
8. [ ] Só então DNS (`www`, `app`, `admin`, `api`) e cadastro aberto.

## O que já foi ensaiado localmente (2026-09-02)

Ambiente: API `:8080`, app `:4200`, LP `:4300`, admin `:4400`, seed demo ligado.

| Item | Resultado |
|------|-----------|
| Health | `GET /api/v1/health` 200 |
| PDF público | `GET /api/v1/public/proposals/demo-aurora/pdf` → `%PDF-`; botão “Baixar PDF” na página `/p/demo-aurora` |
| LP marca e preços | Hero “Gestão de Propostas”; Free R$ 0; Pro R$ 24,90; CTA `register?plan=pro` |
| Termos / privacidade | `/termos` e `/privacidade` com título correto |
| Cadastro Pro | `/register?plan=pro` com rádio Pro marcado |
| Página pública | rota `/p/:token` (alias `/public/proposals/:token`) |
| Admin | `/admin` sem sessão → `/login` |
| Playwright | 9 passou (LP/app/admin); login autenticado pulado sem `E2E_EMAIL` |
| Botão “Já paguei” | oculto quando `environment.production === true` |
| Split remoto / DNS / webhook público | script e workflows prontos; falta `git-filter-repo`, `gh`, contas GCP/Neon e domínio |

Cold start da API em Cloud Run (10–30 s) ainda não foi medido — não há serviço stg provisionado.
