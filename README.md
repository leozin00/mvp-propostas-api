# MVP Propostas — Backend

API REST em **Spring Boot 4** + **Java 21** + **PostgreSQL** + **Flyway**.

> Por enquanto o backend vive neste monorepo (`mvp-propostas/backend`). Futuramente será extraído para um repositório dedicado.

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
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` |

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
