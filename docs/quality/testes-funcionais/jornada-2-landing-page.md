# Jornada 2 — Landing page (aquisição)

Critérios de aceite para testes funcionais da LP pública, usados como orientação para o Playwright.

- **LP:** `http://localhost:4300`
- **App de destino:** `http://localhost:4200` (`/register` e `/login`)
- **Escopo:** navegação, conteúdo, CTAs, preços, FAQ, SEO e acessibilidade

---

## Pré-condições

| Item | Valor |
|------|-------|
| LP | `npm start` em `landing-page/` na porta 4300 |
| App | Não precisa estar rodando para validar os `href` dos CTAs; precisa para testar a navegação completa |
| Seções esperadas | hero, prévia do produto, benefícios, como funciona, público, recursos, comparativo, preços, FAQ, CTA final, rodapé |

---

## Convenções para o Playwright

- Seções âncora possuem `id`: `#topo`, `#recursos`, `#precos`, `#faq`. Usar esses ids para validar navegação interna.
- O FAQ usa `aria-expanded` no botão de cada pergunta — validar o estado por atributo, não por presença visual.
- As animações de entrada dependem de scroll. Rolar até a seção antes de asserções de visibilidade.
- Eventos de analytics são emitidos como `CustomEvent` no `window` com o nome `lp:event`. Capturar via `page.evaluate` registrando um listener antes da interação.
- Os CTAs são links externos para o app. Validar o atributo `href` em vez de depender da navegação real quando o app estiver fora do ar.

---

## Cenário 1 — Carregamento inicial

**Dado** que acesso a raiz da LP  
**Quando** a página termina de carregar  
**Então** vejo o hero e o cabeçalho fixo

Critérios de aceite:

- [ ] A página responde em `/` com status 200.
- [ ] O título da aba é "Criador de orçamentos online | Gestão de Propostas".
- [ ] Existe exatamente um `h1` na página.
- [ ] O cabeçalho é fixo no topo e permanece visível ao rolar.
- [ ] A imagem do hero carrega (sem `naturalWidth` igual a zero).
- [ ] Nenhum erro é registrado no console durante o carregamento.

---

## Cenário 2 — Navegação por âncoras

- [ ] "Recursos" no menu leva à seção `#recursos` e a seção fica visível na viewport.
- [ ] "Preços" leva a `#precos`.
- [ ] "FAQ" leva a `#faq`.
- [ ] O logo no cabeçalho retorna ao topo (`#topo`).
- [ ] O deslocamento respeita o cabeçalho fixo: o título da seção alvo não fica escondido atrás dele.

---

## Cenário 3 — CTAs de conversão

**Dado** que estou em qualquer ponto da página  
**Quando** clico em um CTA principal  
**Então** sou levado ao cadastro do app

Critérios de aceite:

- [ ] O CTA do cabeçalho aponta para `/register` do app.
- [ ] O CTA do hero aponta para `/register`.
- [ ] O CTA do plano Gratuito aponta para `/register`.
- [ ] O CTA do plano Pro aponta para `/register?plan=pro`.
- [ ] O CTA final aponta para `/register`.
- [ ] "Entrar" no cabeçalho aponta para `/login`.
- [ ] Com o app rodando, clicar em "Criar conta grátis" abre a tela de cadastro do app.
- [ ] Nenhum CTA aponta para `#` ou para URL vazia.

---

## Cenário 4 — Seção de preços

- [ ] O plano Gratuito exibe "R$ 0" e o plano Pro exibe "R$ 24,90".
- [ ] O plano Pro está destacado como recomendado.
- [ ] Os itens listados em cada plano correspondem ao conteúdo definido em `data/pricing.ts`.
- [ ] A tabela comparativa exibe todas as linhas de recursos com os valores de Free e Pro.
- [ ] Nenhum preço aparece com placeholder do tipo `{{ }}` ou texto vazio.

---

## Cenário 5 — FAQ

**Dado** que a seção de FAQ está visível  
**Quando** clico em uma pergunta  
**Então** a resposta é exibida

Critérios de aceite:

- [ ] Todas as perguntas iniciam fechadas (`aria-expanded="false"`).
- [ ] Clicar em uma pergunta abre a resposta e marca `aria-expanded="true"`.
- [ ] Abrir uma segunda pergunta fecha a anterior.
- [ ] Clicar na pergunta aberta a fecha novamente.
- [ ] A pergunta é acionável pelo teclado (Enter e Espaço).

---

## Cenário 6 — Menu mobile

**Dado** viewport de 390 × 844  
**Quando** abro o menu

Critérios de aceite:

- [ ] O menu de navegação desktop fica oculto e o botão de menu aparece.
- [ ] O botão alterna `aria-expanded` entre `false` e `true`.
- [ ] O menu aberto lista Recursos, Preços, FAQ e Entrar.
- [ ] Clicar em um link do menu fecha o menu e navega para a seção.
- [ ] O CTA "Criar conta" permanece acessível no cabeçalho mobile.
- [ ] A página não gera rolagem horizontal em nenhuma seção.

---

## Cenário 7 — Analytics

- [ ] O evento `landing_page_view` é emitido no carregamento.
- [ ] Clicar em um CTA de cadastro emite `cta_click` com a `location` correspondente.
- [ ] Clicar em "Entrar" emite `login_click`.
- [ ] Ao rolar até a seção de preços, `pricing_view` é emitido.
- [ ] Clicar no CTA do plano Pro emite `pro_cta_click`.
- [ ] Abrir uma pergunta do FAQ emite `faq_open` com o texto da pergunta.

---

## Cenário 8 — SEO e metadados

- [ ] Existe `meta name="description"` preenchida.
- [ ] Existe `link rel="canonical"`.
- [ ] As tags Open Graph (`og:title`, `og:description`, `og:url`, `og:image`) estão presentes.
- [ ] `meta name="robots"` permite indexação.
- [ ] `meta name="theme-color"` está alinhada à cor primária da marca.
- [ ] O idioma do documento é `pt-BR`.

---

## Cenário 9 — Acessibilidade e movimento

- [ ] Todas as imagens têm `alt` descritivo ou são marcadas como decorativas.
- [ ] A hierarquia de títulos não pula níveis (`h1` → `h2` → `h3`).
- [ ] O foco é visível ao navegar por Tab, em todos os links e botões.
- [ ] Todos os alvos de toque têm altura mínima de 44px no mobile.
- [ ] Com `prefers-reduced-motion: reduce`, as animações de entrada não são executadas e o conteúdo aparece imediatamente.

---

## Checklist de fechamento da jornada

- [ ] Todos os cenários passam em desktop (1280px) e mobile (390px).
- [ ] Nenhum CTA quebrado ou apontando para destino incorreto.
- [ ] Nenhum erro no console.
- [ ] O conteúdo renderizado corresponde ao definido em `data/content.ts` e `data/pricing.ts`.
