# 12 — Instruções para o Cursor Agent

Você é um agente de desenvolvimento responsável por implementar um SaaS de propostas e orçamentos.

## Objetivo
Construir um MVP funcional, seguro, simples e fácil de evoluir.

## Regras gerais
- Priorize simplicidade.
- Não implemente funcionalidades fora do escopo.
- Analise as regras antes de codificar.
- Não invente requisitos.
- Não adicione complexidade sem justificativa.
- Preserve padrões existentes.
- Não reescreva módulos funcionais sem necessidade.

## Arquitetura
- Usar monólito modular.
- Não criar microserviços.
- Não usar Redis, Kafka, RabbitMQ ou Kubernetes no MVP.
- Não implementar event-driven architecture.

## Backend
- Backend é a fonte de verdade.
- Nunca confiar em cálculos do Front-end.
- Nunca confiar em userId enviado pelo Front-end.
- Identificar usuário pelo JWT.
- Validar ownership em toda operação privada.
- Usar BigDecimal para dinheiro.
- Validar todas as transições de status.
- Validar todos os inputs.

## Segurança
- Hash seguro de senha.
- Nunca armazenar senha em texto puro.
- JWT com expiração.
- Secrets em variáveis de ambiente.
- HTTPS em produção.
- CORS restritivo.
- Não expor stack trace.
- Não expor passwordHash.
- Tokens públicos imprevisíveis.
- Proteger endpoints públicos contra abuso.
- Considerar LGPD.

## Propostas
- DRAFT pode ser editada.
- Publicada não pode ser editada.
- Alteração exige duplicação.
- Proposta expirada não pode ser aprovada ou recusada.
- Decisão final não pode ser revertida no MVP.
- Exclusão deve usar soft delete.

## Desenvolvimento
Antes de implementar:
1. Ler a especificação.
2. Identificar dependências.
3. Verificar código existente.
4. Definir menor alteração necessária.
5. Implementar.
6. Testar.
7. Revisar segurança.
8. Validar critérios de aceite.

## Critérios para finalizar uma tarefa
- Compila.
- Testes passam.
- Sem warnings críticos conhecidos.
- Ownership validado.
- Inputs validados.
- Erros tratados.
- Sem secrets hardcoded.
- Sem funcionalidades fora do escopo.

## Regra de escopo
Se uma solicitação estiver fora do MVP:
- Não implementar automaticamente.
- Informar que está fora do escopo.
- Sugerir registrar no backlog futuro.

## Ordem
Seguir o backlog:
1. Setup.
2. Banco.
3. Autenticação.
4. Clientes.
5. Propostas.
6. Itens.
7. Publicação.
8. Página pública.
9. Aprovação/recusa.
10. Dashboard.
11. Segurança.
12. Testes.
13. Docker.
14. CI/CD.
