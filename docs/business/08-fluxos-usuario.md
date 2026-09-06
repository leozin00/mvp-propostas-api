# 08 — Fluxos de Usuário

## Cadastro
1. Usuário informa nome, email e senha.
2. Backend valida dados.
3. Senha é armazenada com hash.
4. Usuário é criado.
5. Usuário pode autenticar.

## Criação de proposta
1. Selecionar cliente existente ou criar cliente.
2. Informar título.
3. Informar descrição.
4. Definir validade.
5. Adicionar itens.
6. Informar quantidade e valor unitário.
7. Aplicar desconto opcional.
8. Backend calcula valores.
9. Salvar como DRAFT.

## Publicação
1. Usuário abre proposta DRAFT.
2. Clica em publicar.
3. Backend valida proposta.
4. Gera publicToken.
5. Status vira SENT.
6. sentAt é preenchido.
7. Link público fica disponível.

## Visualização pública
1. Cliente acessa token.
2. Backend localiza proposta.
3. Verifica soft delete.
4. Verifica validade.
5. Registra viewedAt na primeira visualização.
6. Atualiza status SENT -> VIEWED.
7. Exibe proposta.

## Aprovação
1. Cliente clica Aprovar.
2. Sistema pede confirmação.
3. Backend valida status e validade.
4. Status vira APPROVED.
5. approvedAt é preenchido.

## Recusa
1. Cliente clica Recusar.
2. Motivo opcional.
3. Backend valida status e validade.
4. Status vira REJECTED.
5. rejectedAt é preenchido.

## Proposta expirada
Se validUntil passou:
- Não permitir aprovação.
- Não permitir recusa.
- Exibir mensagem de expiração.
