# Deploy — homologação e produção

Custo inicial: Firebase Hosting (Spark) + Cloud Run (scale-to-zero, 512 MB) + Neon (free). Cold start da API de 10–30 s na primeira request é esperado e aceitável no MVP.

## Ambientes

| | Homologação (`staging`) | Produção |
|--|-------------------------|----------|
| Firebase | projeto `gestao-propostas-stg` (criar) | `gestao-propostas-mvp` |
| Hosting | 3 sites: `www`, `app`, `admin` | iguais |
| API | Cloud Run `api-stg` | Cloud Run `api-prod` |
| Postgres | Neon branch `staging` | Neon branch `production` |
| Mercado Pago | app sandbox `334690360835903` | `GestaoPropostas` `4673848288729614` |
| Spring profile | `staging` | `prod` |
| `KEEP_DEMO_SEED` | `false` | `false` |
| `SPRINGDOC_ENABLED` | `false` | `false` |
| `APP_REQUIRE_EMAIL_VERIFIED` | `true` | `true` |
| `APP_DEBUG_*` | `false` | `false` |

Local: `KEEP_DEMO_SEED=true` (padrão). Seed Flyway (`V3`/`V4`) permanece no histórico; `DemoSeedCleanup` apaga o demo quando `KEEP_DEMO_SEED=false`.

## GitHub Environments

Criar `staging` e `production` em cada repo (no monorepo, os dois environments no mesmo repositório).

Os workflows `.github/workflows/deploy-api.yml` e `deploy-hosting.yml` **não publicam** até as variables existirem (`GCP_PROJECT_ID`, `FIREBASE_PROJECT_ID`). CI de build/teste em `ci.yml` roda sempre.

### Variables (API)

`GCP_PROJECT_ID`, `GCP_REGION` (ex. `southamerica-east1`), `GCP_WORKLOAD_IDENTITY_PROVIDER`, `GCP_SERVICE_ACCOUNT`, `CLOUD_RUN_SERVICE` (`api-stg` / `api-prod`), `SPRING_PROFILE` (`staging` / `prod`), `API_PUBLIC_URL`.

### Secrets (API) — no Cloud Run / Secret Manager, não no YAML

`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `FIREBASE_PROJECT_ID`, `ADMIN_EMAILS`, `MERCADOPAGO_*`, `MERCADOPAGO_WEBHOOK_SECRET`, `MERCADOPAGO_BACK_URL` (HTTPS do app, nunca localhost), `CORS_ALLOWED_ORIGINS`, `SENTRY_DSN`, `SENTRY_ENVIRONMENT`.

`CORS_ALLOWED_ORIGINS` em homolog/prod: origens da LP, do app e do admin (`https://www.…`, `https://app.…`, `https://admin.…`).

### Secrets (Hosting)

`FIREBASE_SERVICE_ACCOUNT` (JSON da service account com permissão de Hosting). Variable `FIREBASE_PROJECT_ID`.

Fronts: file replacements de `environment.production.ts` (`apiUrl`, `appUrl`, `siteUrl`). Homolog pode usar um `environment.staging.ts` depois; no MVP 1 o staging aponta para a URL do Cloud Run stg via o mesmo arquivo até o split.

## Firebase Hosting

Em cada pasta (`frontend/`, `landing-page/`, `admin/`):

```bash
npm ci
npm run build -- --configuration=production
firebase deploy --only hosting --project gestao-propostas-mvp
```

Criar dois projetos Firebase (`…-stg` e `…-prod`). Em cada um: Auth + 3 sites Hosting. Spark cobre o tráfego inicial. Authorized domains do Auth: `localhost`, `app.…`, `admin.…`, `www.…`. Templates de e-mail com o nome **Gestão de Propostas**.

## Webhook Mercado Pago

- URL: `https://<api>/api/v1/webhooks/mercadopago`
- tópicos `subscription_preapproval` e `subscription_authorized_payment`
- secret em `MERCADOPAGO_WEBHOOK_SECRET`
- `MERCADOPAGO_BACK_URL` = `https://app.…/profile`

Nunca tokens `TEST-` em produção. Homolog usa a app sandbox.

## Health

Deploy só é considerado ok com `GET /api/v1/health` respondendo 200. Cloud Run: probe nesse path, start-period ≥ 60 s por causa do cold start.

## Branch protection

Na `main` de cada repo (depois do split; neste monorepo na `main`):

- exigir PR
- exigir CI verde (`api`, `app`, `lp`, `admin`, `e2e-lp`, `e2e-app`, `e2e-admin`)
- não permitir force push

Dependabot já está em `.github/dependabot.yml`.

## Promoção homolog → prod

Mesmo commit/tag, environment diferente. Nunca misturar tokens Mercado Pago de teste em produção.

Ensaio ponta a ponta: `docs/quality/ensaio-lancamento.md`. Só então apontar DNS de produção.
