# 09 — Front-end e UX

## Rotas
- /login
- /register
- /dashboard
- /clients
- /clients/new
- /clients/:id
- /proposals
- /proposals/new
- /proposals/:id
- /proposals/:id/edit
- /public/proposals/:token

## Layout privado
Sidebar:
- Dashboard.
- Clientes.
- Propostas.
- Configurações.

## Dashboard
Cards:
- Total de propostas.
- Rascunhos.
- Enviadas.
- Aprovadas.
- Recusadas.
- Valor total aprovado.

Tabela:
- Cliente.
- Título.
- Valor.
- Status.
- Validade.
- Criada em.
- Ações.

## Clientes
- Listar.
- Buscar por nome.
- Criar.
- Editar.
- Excluir.
- Paginação simples.

## Propostas
- Criar.
- Visualizar.
- Editar apenas DRAFT.
- Duplicar.
- Publicar.
- Compartilhar.
- Excluir.

## Página pública
Deve ser mobile-first.
O cliente provavelmente abrirá o link via WhatsApp no celular.

Mostrar:
- Logo/nome da empresa.
- Título.
- Cliente.
- Descrição.
- Itens.
- Subtotal.
- Desconto.
- Total.
- Validade.
- Observações.
- Aprovar.
- Recusar.

Após aprovação:
`Proposta aprovada com sucesso.`

Após recusa:
`Obrigado pelo retorno.`

## Compartilhamento
Copiar link e WhatsApp já existem. **TODO:** gerar PDF da proposta como opção ao WhatsApp (download/anexo). Envio automático de e-mail continua fora do MVP.
