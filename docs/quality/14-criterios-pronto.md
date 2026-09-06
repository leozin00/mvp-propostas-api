# 14 — Definition of Done do MVP 1

O MVP 1 está pronto para testes reais quando:

- [x] Usuário consegue criar conta (Firebase e-mail/senha e Google).
- [x] E-mail de cadastro é verificado antes da API privada.
- [x] Usuário pode escolher Free ou Pro no cadastro.
- [x] Usuário consegue fazer login.
- [x] CRUD de clientes (criar, editar, excluir com regra de propostas).
- [x] CRUD de propostas com itens; totais calculados no backend.
- [x] Rascunho, publicação e link público.
- [x] Cliente visualiza a proposta sem login; visualização registrada.
- [x] Cliente aprova ou recusa; expirada ou já decidida não muda.
- [x] Dashboard com indicadores da API.
- [x] PDF da proposta (app autenticado e página pública).
- [x] Ownership protegido; senhas no Firebase (não na API).
- [x] Secrets fora do código (`.env` local / GitHub Environments).
- [x] Testes Playwright da LP, smokes de app/admin e PDF público em `e2e/`; gate de merge no CI.
- [x] API empacotável em container (`backend/Dockerfile`); health check.
- [x] CI executa build e testes.
- [x] Deploy de homologação documentado (`docs/deploy.md`).

Fora deste DoD (MVP 2 / futuro):

- Loader HTTP global via interceptor.
- Logo da empresa, catálogo de produtos, custom claim `admin: true`.
- Motivo de recusa, foto do vendedor.
- Pagamento na aprovação da proposta.

## Princípio

O MVP não precisa ser completo. Precisa ser pequeno, honesto na LP, seguro e capaz de validar se alguém paga o Pro.
