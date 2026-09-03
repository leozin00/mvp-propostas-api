# Gestão de Propostas — API

API REST em **Spring Boot 4** + **Java 21** + **PostgreSQL** + **Flyway**.

Fonte de verdade do produto: `docs/` e `AGENTS.md` (neste monorepo; após o split, neste mesmo repo `mvp-propostas-api`).

Homologação e produção: `docs/deploy.md`. Seed demo só permanece com `KEEP_DEMO_SEED=true` (local).

## Stack

- Java 21
- Spring Boot 4.0
- Spring Web MVC, Data JPA, Security, Validation
- PostgreSQL 16
- Flyway
- Lombok
- SpringDoc OpenAPI (Swagger UI)
- Actuator

## Pré-requisitos

- Java 21+
- Maven 3.9+ (ou `./mvnw`)
- Docker (para PostgreSQL local)

## Subir o banco

Na raiz do monorepo:

```bash
docker compose up -d
```

## Rodar a API

```bash
cd backend
./mvnw spring-boot:run
```

Endpoints úteis:

| URL | Descrição |
|-----|-----------|
| http://localhost:8080/api/v1/health | Health check da API |
| http://localhost:8080/actuator/health | Actuator |
| http://localhost:8080/swagger-ui/index.html | Swagger UI |

## Variáveis de ambiente

Copie `.env.example` (na raiz do monorepo) e ajuste se necessário. Valores padrão em `application.yml`:

| Variável | Default |
|----------|---------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/mvp_propostas` |
| `DB_USERNAME` | `mvp` |
| `DB_PASSWORD` | `mvp` |
| `SERVER_PORT` | `8080` |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` (incluir LP `:4300` e admin `:4400`) |
| `SENTRY_DSN` | DSN do projeto `mvp-propostas-api` no Sentry |
| `SENTRY_ENVIRONMENT` | `development` |
| `SENTRY_TRACES_SAMPLE_RATE` | `1.0` |
| `KEEP_DEMO_SEED` | `true` local; `false` em staging/prod |
| `APP_REQUIRE_EMAIL_VERIFIED` | `true` |
| `APP_DEBUG_SENTRY_TEST_ENABLED` | `false` — `true` só habilita `GET /api/v1/debug/sentry` |
| `APP_DEBUG_MP_OAUTH_ENABLED` | `false` — `true` só local para OAuth sandbox |

> O Spring Boot carrega automaticamente o arquivo `.env` da raiz do monorepo (ou `backend/.env`).

### Erros no frontend vs backend

| Cenário | Quem captura no Sentry |
|---------|------------------------|
| Docker/Postgres desligado e **backend fora do ar** | **Frontend** (`javascript-angular`) — a requisição nem chega ao Java |
| Docker/Postgres desligado e **backend no ar** | **Backend** (`mvp-propostas-api`) — a API responde 500 e envia o erro |
| `GET /api/v1/debug/sentry` | **Backend** — erro forçado para teste |

## Sentry

Monitoramento de erros configurado com a conta pessoal de desenvolvimento:

| App | Projeto Sentry |
|-----|----------------|
| Frontend (Angular) | `javascript-angular` |
| Backend (API) | `mvp-propostas-api` |

### Testar integração

**Backend** — com `APP_DEBUG_SENTRY_TEST_ENABLED=true`:

```bash
curl http://localhost:8080/api/v1/debug/sentry
```

**Frontend** — no console do navegador:

```javascript
throw new Error('Sentry test error from MVP Propostas frontend');
```

Os eventos aparecem em [sentry.io](https://sentry.io) nos projetos acima.

## Estrutura de pacotes

```text
com.mvppropostas
├── config/          # Security, CORS, OpenAPI
├── controller/      # REST controllers
├── dto/             # Request/response DTOs
├── common/          # Exceções e utilitários
└── (fases futuras) entity, repository, service
```

## Migrations

Scripts Flyway em `src/main/resources/db/migration/`:

- `V1__init_schema.sql` — users, clients, proposals, proposal_items

## Testes

```bash
./mvnw test
```

Perfil `test` usa H2 em memória (modo PostgreSQL).

## Próximas fases

Ver `docs/quality/11-backlog-implementacao.md`:

1. Entidades JPA + repositories
2. Autenticação JWT
3. CRUD clientes e propostas
4. Integração com o frontend Angular

## Documentação

- Arquitetura: `docs/architecture/03-stack-arquitetura.md`
- Modelo de dados: `docs/architecture/04-modelo-dados.md`
- API: `docs/architecture/06-api.md`
- Planos: `docs/monetization.md`
