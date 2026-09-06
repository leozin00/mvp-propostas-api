# 03 — Stack e Arquitetura

## Stack recomendada

### Front-end
- Angular.
- TypeScript.
- RxJS.
- Reactive Forms.
- Angular Material ou biblioteca UI equivalente.
- HttpClient.
- Guards.
- Interceptors.

### Back-end
- Java 17+.
- Spring Boot.
- Spring Security.
- Spring Data JPA.
- Bean Validation.
- PostgreSQL.
- Flyway.
- Lombok.
- OpenAPI/Swagger.

### Infraestrutura
- Docker.
- Docker Compose.
- GitHub Actions.
- AWS.

## Arquitetura
Usar monólito modular na API. Os **fronts e a API** vão para **repositórios separados** (backend, LP, Admin, front de orçamentos); o código ainda está neste monorepo até o split.

```text
Front de orçamentos · Landing page · Admin
   |
 HTTPS
   v
Spring Boot API (repo backend)
   |
   v
PostgreSQL
```

## Não implementar no MVP
- Microserviços.
- Kafka.
- RabbitMQ.
- Kubernetes.
- Redis.
- Event-driven architecture.

## Princípio
Priorizar simplicidade, clareza, segurança e facilidade de evolução.
