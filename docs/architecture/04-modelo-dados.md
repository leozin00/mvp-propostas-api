# 04 — Modelo de Dados

## User
Campos:
- id UUID.
- name.
- email.
- passwordHash.
- createdAt.
- updatedAt.
- active.

Regras:
- Email único.
- Email normalizado para lowercase.
- Senha nunca em texto puro.
- Usuário inativo não autentica.

## Client
Campos:
- id UUID.
- userId.
- name.
- email.
- phone.
- document.
- companyName.
- createdAt.
- updatedAt.

Regras:
- Cliente pertence a um usuário.
- Usuário não acessa clientes de outro usuário.
- name obrigatório.
- Demais dados opcionais.
- CPF/CNPJ não precisa ser validado no MVP.

## Proposal
Campos:
- id UUID.
- userId.
- clientId.
- title.
- description.
- status.
- validUntil.
- subtotal.
- discount.
- total.
- publicToken.
- createdAt.
- updatedAt.
- sentAt.
- viewedAt.
- approvedAt.
- rejectedAt.
- deletedAt.

Status:
- DRAFT.
- SENT.
- VIEWED.
- APPROVED.
- REJECTED.
- EXPIRED.

## ProposalItem
Campos:
- id UUID.
- proposalId.
- description.
- quantity.
- unitPrice.
- total.

Regra:
`total = quantity * unitPrice`.

O backend é a fonte de verdade para todos os cálculos.
