# Jornada 1 — Gestão de orçamentos (app)

Critérios de aceite para testes funcionais end-to-end da plataforma, usados como orientação para o Playwright.

- **App:** `http://localhost:4200`
- **API:** `http://localhost:8080`
- **Escopo:** login, clientes, propostas, publicação, página pública e perfil

---

## Pré-condições

| Item | Valor |
|------|-------|
| Banco | PostgreSQL via `docker compose up -d` com migrations `V1`–`V5` aplicadas |
| Backend | `./mvnw spring-boot:run` na porta 8080 |
| Frontend | `npm start` na porta 4200 |
| Conta de teste | Usuário Firebase com e-mail `leonardo@empresa.com` (herda os dados do seed) |
| Tokens públicos do seed | `demo-aurora` (VIEWED), `demo-norte` (SENT), `demo-cafe` (APPROVED), `demo-linha` (REJECTED) |

> Cada execução deve começar em estado limpo previsível. Se o teste criar dados, ele deve removê-los ao final ou usar nomes com sufixo aleatório.

---

## Convenções para o Playwright

- Preferir seletores por papel e texto acessível: `getByRole('button', { name: 'Publicar' })`, `getByLabel('Buscar clientes')`.
- Nunca usar classes utilitárias do Tailwind como seletor — elas mudam com o design.
- O loader das listagens tem `role="status"` e `aria-busy="true"`. Aguardar o desaparecimento dele antes de consultar a lista, e não usar `waitForTimeout`. Loader HTTP global **não** faz parte do MVP 1.
- Diálogos nativos (`confirm`) são usados em exclusão, publicação, duplicação, aprovação e recusa. Registrar o handler **antes** de disparar a ação.
- Valores monetários são renderizados em pt-BR (`R$ 8.500,00`). Comparar por texto normalizado ou por regex.

---

## Cenário 1 — Login e proteção de rotas

**Dado** que não há sessão ativa  
**Quando** acesso `/dashboard`  
**Então** sou redirecionado para `/login` com `?redirect=/dashboard`

Critérios de aceite:

- [ ] Acesso a rota privada sem sessão redireciona para `/login` preservando o destino em `redirect`.
- [ ] Login com e-mail e senha válidos leva ao destino original (ou `/dashboard`).
- [ ] Login com credenciais inválidas mantém o usuário em `/login` e exibe mensagem de erro legível, sem expor código do Firebase.
- [ ] Usuário autenticado que acessa `/login` ou `/register` é redirecionado para `/dashboard`.
- [ ] "Sair da conta" no perfil encerra a sessão e leva a `/login`; voltar no navegador não recupera a área privada.
- [ ] Requisições à API incluem o header `Authorization: Bearer <idToken>`.
- [ ] Resposta `401` da API desloga o usuário e redireciona para `/login`.

---

## Cenário 2 — Dashboard

**Dado** que estou autenticado  
**Quando** acesso `/dashboard`  
**Então** vejo os indicadores calculados pela API

Critérios de aceite:

- [ ] Enquanto carrega, apenas o loader é exibido; os cartões de indicadores não aparecem antes dos dados.
- [ ] Os quatro indicadores são exibidos com valores numéricos, sem `NaN`, `undefined` ou `null`.
- [ ] A lista de propostas recentes mostra título, cliente e status.
- [ ] Clicar em uma proposta recente abre o detalhe correspondente.
- [ ] "Nova proposta" leva a `/proposals/new`.

---

## Cenário 3 — CRUD de clientes

### 3.1 Listagem

- [ ] Durante o carregamento apenas o loader aparece; a tabela/cards só renderizam depois.
- [ ] O contador do cabeçalho corresponde ao número de clientes filtrados.
- [ ] A busca filtra por nome, e-mail e empresa, e reinicia a paginação na página 1.
- [ ] Busca sem resultados exibe o estado vazio "Nenhum cliente encontrado".
- [ ] A paginação avança e retrocede, e os botões ficam desabilitados nos limites.

### 3.2 Criação

**Dado** que estou em `/clients/new`  
**Quando** informo apenas o nome e salvo  
**Então** o cliente é criado e apareço na listagem

- [ ] Salvar com nome vazio bloqueia o envio e exibe erro no campo.
- [ ] Cliente criado aparece na listagem sem recarregar a página manualmente.
- [ ] E-mail inválido é rejeitado pela validação do formulário.

### 3.3 Edição e detalhe

- [ ] O detalhe carrega dados do cliente e suas propostas juntos, sem estado intermediário com campos vazios.
- [ ] Campos não preenchidos exibem `—` em vez de vazio.
- [ ] Editar e salvar persiste a alteração e reflete no detalhe.

### 3.4 Exclusão

- [ ] Excluir pede confirmação antes de chamar a API.
- [ ] Cliente sem propostas é excluído e some da listagem.
- [ ] Cliente com propostas não é excluído e a mensagem de erro da API é exibida na tela.

---

## Cenário 4 — Criação de proposta

**Dado** que estou em `/proposals/new`  
**Quando** preencho cliente, título, validade e ao menos um item  
**Então** consigo salvar a proposta como rascunho

Critérios de aceite:

- [ ] Salvar sem cliente, título ou validade bloqueia o envio e marca os campos inválidos.
- [ ] "+ Cadastrar novo cliente" troca o seletor por formulário inline com Nome, Telefone e E-mail.
- [ ] Ao salvar com cadastro inline, o cliente é criado na API e vinculado à proposta.
- [ ] "Selecionar cliente existente" volta ao seletor sem perder os demais campos do formulário.
- [ ] "Adicionar item" cria uma nova linha; remover item retira a linha correspondente.
- [ ] O total da linha corresponde a `quantidade × valor unitário`.
- [ ] O alternador de desconto oferece as opções em reais e em percentual (`aria-label` "Desconto em reais" / "Desconto em percentual").
- [ ] Com desconto percentual, valores acima de 100 são rejeitados.
- [ ] Os totais exibidos após salvar são os que a API retornou, não os calculados no navegador.
- [ ] A proposta nasce com status Rascunho.

---

## Cenário 5 — Detalhe, publicação e compartilhamento

- [ ] Proposta em rascunho exibe "Editar" e "Publicar"; publicada não exibe "Editar".
- [ ] Publicar pede confirmação, muda o status para Enviada e gera o link público.
- [ ] "Copiar link" copia a URL pública e mostra o retorno "Link copiado!".
- [ ] "Enviar WhatsApp" monta a mensagem com título, cliente e link, usando o telefone do cliente quando cadastrado.
- [ ] "Baixar PDF" baixa o PDF da proposta (sem logo da empresa).
- [ ] "Duplicar" cria uma nova proposta em rascunho, com novo identificador e sem os carimbos de envio, visualização ou decisão.
- [ ] Tentar acessar `/proposals/:id/edit` de uma proposta publicada não permite salvar alterações.

---

## Cenário 6 — Página pública da proposta

**Dado** um token de proposta publicada  
**Quando** acesso `/p/:token` (ou o alias `/public/proposals/:token`) sem estar autenticado  
**Então** vejo a proposta completa

Critérios de aceite:

- [ ] A página abre sem sessão e sem redirecionar para login.
- [ ] Emissor, título, cliente, validade, itens, subtotal, desconto e total são exibidos.
- [ ] O primeiro acesso a uma proposta Enviada muda o status para Visualizada.
- [ ] Em proposta respondível, os botões "Aprovar proposta" e "Recusar proposta" estão habilitados.
- [ ] Aprovar pede confirmação e, após confirmar, o status na tela passa a Aprovada e os botões somem.
- [ ] Recusar segue o mesmo fluxo e resulta em Recusada.
- [ ] Proposta já aprovada ou recusada exibe o aviso de status e não permite nova decisão.
- [ ] Proposta expirada exibe a mensagem de expiração e não permite aprovar nem recusar.
- [ ] Token inexistente exibe "Proposta indisponível" em vez de erro genérico.
- [ ] "Baixar PDF" gera o arquivo da proposta pública.

---

## Cenário 7 — Perfil e configurações

- [ ] A aba Análises exibe métricas reais e o accordion de propostas aprovadas por mês.
- [ ] A aba Configurações carrega dados pessoais e da empresa preenchidos.
- [ ] Salvar dados pessoais mostra estado de carregamento no botão e confirmação de sucesso na seção.
- [ ] E-mail já usado por outra conta é rejeitado com a mensagem vinda da API.
- [ ] Salvar dados da empresa persiste após recarregar a página.
- [ ] O nome e as iniciais na barra lateral refletem a conta autenticada.

---

## Cenário 8 — Isolamento de dados

- [ ] Uma conta nova (e-mail que não é o do seed) enxerga listas vazias de clientes e propostas.
- [ ] Recursos de outra conta não aparecem em listagens nem podem ser abertos por URL direta.
- [ ] Chamada à API com token de outra conta para um recurso alheio não retorna os dados.

---

## Cenário 9 — Responsividade e erros

- [ ] Em viewport de 390px, listagens usam cards e não geram rolagem horizontal.
- [ ] O menu lateral abre e fecha no mobile.
- [ ] Com a API indisponível, o app mostra a tela de erro de sistema em vez de quebrar.
- [ ] Mensagens de erro da API aparecem na interface em português, sem stack trace.

---

## Checklist de fechamento da jornada

- [ ] Todos os cenários acima passam em Chromium.
- [ ] Nenhum teste depende de `waitForTimeout`.
- [ ] Os testes não deixam dados residuais no banco.
- [ ] Nenhum erro não tratado no console durante a execução.
