# 11 — Backlog de Implementação

## Fase 1 — Setup
- Criar projeto Angular.
- Criar projeto Spring Boot.
- Configurar PostgreSQL.
- Configurar Flyway.
- Configurar Docker Compose.
- Configurar variáveis de ambiente.
- Configurar estrutura inicial.
- Criar README.

## Fase 2 — Banco
- Migration User.
- Migration Client.
- Migration Proposal.
- Migration ProposalItem.
- Índices necessários.
- Constraints.

## Fase 3 — Autenticação
- Register.
- Login.
- JWT.
- Spring Security.
- Password hashing.
- Guards no Angular.
- HTTP interceptor.

## Fase 4 — Clientes
- Entity.
- Repository.
- Service.
- Controller.
- DTOs.
- Validações.
- Ownership.
- CRUD Front-end.

## Fase 5 — Propostas
- Entity.
- Repository.
- Service.
- Controller.
- DTOs.
- CRUD.
- Ownership.
- Soft delete.

## Fase 6 — Itens
- Adicionar item.
- Remover item.
- Editar item.
- Cálculo BigDecimal.
- Validação.

## Fase 7 — Publicação
- Publicar proposta.
- Gerar token.
- Copiar link.
- Status SENT.
- **TODO:** gerar PDF da proposta como opção ao WhatsApp.

## Fase extra — Repositórios (após o produto do MVP 1 estar estável)
- Separar o monorepo em: backend, landing page, Admin, front de orçamentos.

## Fase 8 — Página pública
- Resolver token.
- Registrar visualização.
- Atualizar VIEWED.
- Exibir proposta.
- Expiração.

## Fase 9 — Decisão
- Aprovar.
- Recusar.
- Motivo opcional.
- Bloquear decisões inválidas.

## Fase 10 — Dashboard
- Indicadores.
- Propostas recentes.
- Valor aprovado.

## Fase 11 — Segurança
- Revisar ownership.
- Revisar CORS.
- Revisar secrets.
- Revisar validações.
- Rate limiting nas rotas públicas.
- Logs.

## Fase 12 — Testes
- Unitários.
- Integração.
- Segurança.
- Fluxos públicos.

## Fase 13 — Docker
- Dockerfile backend.
- Dockerfile frontend.
- Docker Compose.
- Health checks.

## Fase 14 — CI/CD
- Build.
- Testes.
- Build de imagens.
- Push.
- Deploy.

## Regra
Executar uma fase por vez.
Não avançar se a fase atual estiver quebrada.
