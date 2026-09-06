# 06 — API REST

## Auth
`POST /api/auth/register`
- name
- email
- password

`POST /api/auth/login`
- email
- password

Retorno de login:
```json
{
  "accessToken": "...",
  "tokenType": "Bearer"
}
```

## Clientes
`GET /api/clients`
`POST /api/clients`
`GET /api/clients/{id}`
`PUT /api/clients/{id}`
`DELETE /api/clients/{id}`

## Propostas
`GET /api/proposals`
`POST /api/proposals`
`GET /api/proposals/{id}`
`PUT /api/proposals/{id}`
`DELETE /api/proposals/{id}`
`POST /api/proposals/{id}/publish`
`POST /api/proposals/{id}/duplicate`

## Público
`GET /api/public/proposals/{token}`
`POST /api/public/proposals/{token}/approve`
`POST /api/public/proposals/{token}/reject`

## Regras
- Rotas privadas exigem JWT.
- Rotas públicas não exigem login.
- Validar ownership em toda operação privada.
- Não confiar em userId vindo do cliente.
- Validar todos os inputs.
- Retornar erros consistentes.
