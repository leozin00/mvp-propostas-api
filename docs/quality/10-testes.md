# 10 — Testes e Qualidade

## Testes obrigatórios
Criar testes para regras críticas.

### Autenticação
- Cadastro válido.
- Email duplicado.
- Senha inválida.
- Login válido.
- Login inválido.

### Ownership
- Usuário acessa próprio recurso.
- Usuário não acessa recurso de outro usuário.
- Usuário não altera recurso de outro usuário.
- Usuário não exclui recurso de outro usuário.

### Propostas
- Criar DRAFT.
- Calcular itens.
- Calcular subtotal.
- Aplicar desconto.
- Impedir total negativo.
- Publicar DRAFT.
- Impedir publicação inválida.
- Impedir edição após publicação.
- Duplicar proposta.
- Soft delete.

### Status
- Validar transições permitidas.
- Bloquear transições inválidas.

### Público
- Token válido.
- Token inválido.
- Proposta excluída.
- Proposta expirada.
- Aprovação válida.
- Recusa válida.
- Impedir aprovação após decisão.

## Antes de finalizar cada tarefa
Validar:
1. Compilação.
2. Testes.
3. Segurança.
4. Isolamento de dados.
5. Validação de inputs.
6. Tratamento de erros.
7. Compatibilidade com o escopo.
