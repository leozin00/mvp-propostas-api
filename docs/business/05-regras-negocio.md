# 05 — Regras de Negócio

## Cálculos
- Usar BigDecimal.
- Nunca usar float ou double para dinheiro.
- quantity > 0.
- unitPrice >= 0.
- discount >= 0.
- total >= 0.
- Backend recalcula subtotal, total dos itens e total final.
- Nunca confiar em valores calculados enviados pelo Front-end.

## Status
Fluxo permitido:

```text
DRAFT -> SENT
SENT -> VIEWED
SENT -> APPROVED
SENT -> REJECTED
VIEWED -> APPROVED
VIEWED -> REJECTED
SENT -> EXPIRED
```

Não permitir:
- APPROVED -> REJECTED.
- APPROVED -> DRAFT.
- REJECTED -> APPROVED.
- REJECTED -> DRAFT.

## Edição
- DRAFT pode ser editada.
- Proposta publicada não pode ser editada no MVP.
- Para alterar uma proposta publicada, duplicar e editar a nova versão.

## Expiração
- Validar validade no acesso.
- Se a data atual ultrapassar validUntil, tratar como EXPIRED.
- Proposta expirada não pode ser aprovada ou recusada.

## Exclusão
Usar soft delete.
- Recursos excluídos não aparecem nas listagens.
- Link público de proposta excluída retorna 404.

## Duplicação
Ao duplicar:
- Criar nova proposta.
- Novo UUID.
- Novo publicToken.
- Status DRAFT.
- Copiar cliente e itens conforme referência.
- Não copiar timestamps de envio, visualização, aprovação ou recusa.
