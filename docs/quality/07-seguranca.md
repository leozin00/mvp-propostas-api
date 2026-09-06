# 07 — Segurança

## Isolamento de dados
Nenhum usuário pode acessar, alterar ou excluir recursos de outro usuário.

O usuário autenticado deve ser identificado pelo JWT, nunca por userId enviado pelo Front-end.

## IDOR
Endpoints privados devem validar ownership.
Quando apropriado, não revelar a existência de recursos de outro usuário; responder 404.

## Autenticação
- JWT com expiração.
- Hash seguro de senha.
- Senha mínima de 8 caracteres.
- Email único e normalizado.
- Nunca retornar senha ou passwordHash.
- Secrets via variáveis de ambiente.

## Tokens públicos
- Token aleatório e imprevisível.
- UUID v4 ou gerador criptograficamente seguro.
- Nunca usar IDs sequenciais como token público.
- Token não deve revelar ID interno.

## API
- HTTPS em produção.
- CORS restritivo.
- Bean Validation.
- Limites de tamanho de campos.
- Rate limiting para endpoints públicos.
- Não expor stack trace em produção.
- Logs sem senhas, tokens ou dados sensíveis.

## XSS
- Tratar campos de usuário como texto.
- Não permitir HTML arbitrário.
- Evitar innerHTML e bypassSecurityTrustHtml sem necessidade.

## LGPD
- Coletar somente dados necessários.
- Informar finalidade.
- Política de privacidade.
- Permitir exclusão da conta.
- Proteger dados em trânsito.
- Não armazenar dados bancários ou de cartão sem necessidade.
