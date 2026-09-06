# Especificação — Landing Page do SaaS de Orçamentos

## 1. Objetivo

Criar uma Landing Page (LP) moderna, rápida, responsiva e orientada à conversão para o SaaS de criação e gestão de orçamentos destinado inicialmente a autônomos e pequenas empresas.

A Landing Page deve ter como objetivo principal:

1. Explicar rapidamente o problema que o produto resolve.
2. Demonstrar o valor do SaaS.
3. Gerar confiança.
4. Levar o visitante a criar uma conta gratuita.
5. Apresentar claramente as diferenças entre o plano Gratuito e o plano Pro.
6. Ser preparada para aquisição orgânica via Google.
7. Funcionar muito bem em dispositivos móveis.
8. Não parecer uma página genérica de SaaS criada por template.

A ação principal da LP deve ser:

> **Criar minha conta grátis**

A ação secundária pode ser:

> **Ver como funciona**

---

# 2. Público-alvo

O público inicial deve ser formado principalmente por:

- Autônomos.
- Prestadores de serviços.
- Freelancers.
- MEIs.
- Pequenas empresas.
- Profissionais que fazem orçamentos pelo WhatsApp.
- Profissionais que utilizam Excel, Word ou documentos improvisados.
- Profissionais que precisam enviar orçamentos rapidamente para seus clientes.

Nichos que podem ser trabalhados posteriormente:

- Eletricistas.
- Pintores.
- Técnicos de informática.
- Fotógrafos.
- Designers.
- Social media.
- Encanadores.
- Técnicos e profissionais de manutenção.
- Profissionais de instalação.
- Prestadores de serviços em geral.

A LP principal deve permanecer abrangente, mas a arquitetura deve permitir posteriormente criar Landing Pages específicas por nicho.

Exemplos:

- `/eletricista`
- `/pintor`
- `/fotografo`
- `/tecnico-informatica`
- `/prestador-de-servicos`

---

# 3. Posicionamento

Não posicionar o produto simplesmente como:

> "Sistema para criar orçamentos."

Isso é genérico.

O posicionamento deve enfatizar o resultado:

> **Crie orçamentos profissionais em poucos minutos e envie para seus clientes.**

Outras possibilidades de copy:

> **Pare de perder tempo fazendo orçamento no Excel.**

> **Crie, envie e acompanhe seus orçamentos em um só lugar.**

> **Seu orçamento profissional pronto em poucos minutos.**

> **Mais agilidade para criar orçamentos. Mais profissionalismo para fechar serviços.**

A comunicação deve ser simples e evitar excesso de termos técnicos.

---

# 4. Tom de voz

Utilizar:

- Português do Brasil.
- Linguagem simples.
- Direta.
- Profissional, mas não corporativa.
- Próxima do pequeno empreendedor.
- Orientada a benefício.
- Sem exageros.
- Sem promessas impossíveis.

Evitar:

- Linguagem excessivamente técnica.
- "Revolucionário".
- "Disruptivo".
- "A solução definitiva".
- "Transforme completamente seu negócio".
- Jargões corporativos.
- Textos enormes acima da dobra.

O usuário deve entender o produto em aproximadamente 5 segundos.

---

# 5. Princípio principal de UX

A Landing Page deve responder rapidamente às seguintes perguntas:

1. O que é?
2. Para quem é?
3. Qual problema resolve?
4. Como funciona?
5. Quanto custa?
6. Existe plano grátis?
7. É fácil começar?

Se o visitante precisar procurar essas respostas, a LP precisa ser melhorada.

---

# 6. Estrutura da Landing Page

Implementar as seguintes seções, nesta ordem:

1. Header
2. Hero
3. Prova visual do produto
4. Benefícios
5. Como funciona
6. Para quem é
7. Recursos
8. Comparativo Gratuito vs Pro
9. FAQ
10. CTA final
11. Footer

---

# 7. Header

O header deve ser simples.

Elementos:

- Logo.
- Link "Recursos".
- Link "Preços".
- Link "FAQ".
- Botão "Entrar".
- Botão principal "Criar conta grátis".

No mobile:

- Logo.
- Menu hamburger.
- CTA de cadastro destacado.

O header pode utilizar `position: sticky`, desde que isso não prejudique a experiência mobile.

O botão principal deve permanecer visualmente destacado.

---

# 8. Hero

Esta é a seção mais importante.

Estrutura:

### Eyebrow

Exemplo:

> ORÇAMENTOS PROFISSIONAIS PARA SEU NEGÓCIO

### Headline

Preferência:

> **Crie orçamentos profissionais em poucos minutos.**

Alternativas:

> **Pare de perder tempo fazendo orçamentos no Excel.**

> **Seu orçamento pronto para enviar ao cliente.**

### Subheadline

Exemplo:

> Crie orçamentos personalizados, envie para seus clientes e acompanhe suas propostas em um só lugar. Comece grátis.

### CTA principal

> **Criar minha conta grátis**

### CTA secundário

> **Ver como funciona**

### Microcopy abaixo do CTA

Exemplos:

> Comece grátis. Sem cartão de crédito.

ou

> Crie sua conta gratuitamente e faça seu primeiro orçamento.

Não utilizar afirmações que não sejam verdadeiras. Se o produto exigir cartão, remover essa informação.

---

# 9. Hero visual

O lado direito do Hero deve apresentar uma representação realista da plataforma.

Preferencialmente:

- Screenshot real do dashboard.
- Screenshot de um orçamento.
- Mockup do orçamento sendo visualizado em desktop/mobile.
- Pequenas animações discretas.

Evitar imagens genéricas de banco de imagens.

O visitante precisa visualizar:

> "É assim que meu orçamento vai ficar."

A imagem deve transmitir:

- Organização.
- Profissionalismo.
- Simplicidade.
- Modernidade.

Adicionar elementos visuais secundários apenas se contribuírem para a compreensão.

---

# 10. Prova visual do produto

Logo após o Hero, apresentar uma seção mostrando o produto em funcionamento.

Título:

> **Do orçamento à aprovação, sem complicação.**

Mostrar exemplos de:

- Dashboard.
- Criação de orçamento.
- Lista de clientes.
- Visualização do orçamento.
- Status do orçamento.
- PDF.
- Compartilhamento.

Se ainda não houver dados reais, utilizar dados fictícios claramente controlados pelo próprio produto.

Não inventar avaliações ou clientes.

---

# 11. Benefícios

Não listar apenas funcionalidades.

Priorizar benefícios.

Exemplo:

### Orçamentos profissionais

> Apresente seus serviços de forma organizada e passe mais confiança para seus clientes.

### Mais rapidez

> Crie novos orçamentos sem precisar começar tudo do zero.

### Envie facilmente

> Prepare seu orçamento para compartilhar com o cliente de forma rápida.

### Tudo organizado

> Tenha seus orçamentos e clientes centralizados em um só lugar.

### Acompanhe suas propostas

> Saiba quais orçamentos estão pendentes, aprovados ou recusados.

### Acesse de qualquer lugar

> Consulte seus orçamentos pelo computador ou celular.

Os benefícios devem ser curtos e escaneáveis.

---

# 12. Seção "Como funciona"

Utilizar 3 ou 4 passos.

### 01 — Cadastre-se

> Crie sua conta gratuitamente.

### 02 — Monte seu orçamento

> Adicione cliente, serviços, valores e condições.

### 03 — Envie para seu cliente

> Gere e compartilhe seu orçamento de forma profissional.

### 04 — Acompanhe

> Tenha controle sobre seus orçamentos e saiba o que aconteceu com cada proposta.

Essa seção deve transmitir a sensação de facilidade.

---

# 13. Seção "Para quem é"

Criar cards ou lista visual.

Título:

> **Feito para quem vive de serviços.**

Exemplos:

- Eletricistas.
- Pintores.
- Técnicos.
- Designers.
- Fotógrafos.
- Freelancers.
- Profissionais de manutenção.
- Pequenas empresas.

CTA opcional:

> **Ver como funciona para meu negócio**

Posteriormente, essa seção pode levar para Landing Pages específicas de cada nicho.

---

# 14. Recursos

Apresentar funcionalidades importantes.

Exemplos:

- Criação de orçamentos.
- Cadastro de clientes.
- Produtos e serviços.
- Templates.
- PDF.
- Compartilhamento.
- Status do orçamento.
- Histórico.
- Dashboard.
- Personalização da empresa.
- Logo.
- Controle de validade.
- Condições de pagamento.

Não apresentar funcionalidades que ainda não existem.

A LP deve sempre refletir o produto real.

---

# 15. Seção de diferenciação

Criar uma seção com a ideia:

> **Menos tempo fazendo orçamento. Mais tempo cuidando do seu negócio.**

Explicar o problema do método tradicional:

### Antes

- Excel.
- Word.
- Copiar e colar.
- Procurar informações antigas.
- Refazer orçamento.
- Arquivos espalhados.
- Dificuldade para acompanhar propostas.

### Com a plataforma

- Clientes organizados.
- Serviços cadastrados.
- Orçamentos padronizados.
- Processo mais rápido.
- Histórico centralizado.
- Aparência profissional.

Usar uma comparação visual simples.

---

# 16. Pricing

A seção de preços deve ser extremamente clara.

Apresentar dois planos:

## Gratuito

Objetivo:

Permitir que o usuário experimente o produto sem barreira.

Possíveis benefícios:

- Criar orçamentos.
- Cadastro de clientes.
- PDF.
- Recursos básicos.
- Limite mensal de orçamentos.
- Recursos básicos de personalização.

CTA:

> **Começar grátis**

---

## Pro

Objetivo:

Entregar recursos para quem utiliza a plataforma profissionalmente.

Possíveis benefícios:

- Mais orçamentos.
- Recursos avançados.
- Personalização completa.
- Logo da empresa.
- Mais clientes.
- Histórico completo.
- Recursos avançados de acompanhamento.
- Remoção de limitações do plano gratuito.

CTA:

> **Começar com Pro**

Importante:

Os limites e funcionalidades precisam ser definidos com base no produto real. Não inventar recursos apenas para preencher a tabela.

---

# 17. Comparativo Gratuito vs Pro

Criar uma tabela responsiva.

Exemplo estrutural:

| Recurso | Gratuito | Pro |
|---|---:|---:|
| Criar orçamentos | ✓ | ✓ |
| Cadastro de clientes | ✓ | ✓ |
| PDF | ✓ | ✓ |
| Compartilhamento | ✓ | ✓ |
| Histórico de orçamentos | Limitado | ✓ |
| Personalização | Básica | Completa |
| Logo da empresa | — | ✓ |
| Limite de orçamentos | Limitado | Maior/ilimitado |
| Recursos avançados | — | ✓ |
| Suporte prioritário | — | ✓ |

Não utilizar "ilimitado" se existir algum limite técnico ou comercial.

O plano Pro deve ser visualmente destacado.

Adicionar um selo:

> **Mais escolhido**

somente se houver evidência real de que esse é o plano mais escolhido.

---

# 18. Estratégia de conversão do Pricing

Não apresentar apenas preço.

Mostrar valor.

Exemplo:

> **Comece grátis e descubra uma forma mais simples de criar seus orçamentos.**

Para o Pro:

> **Para quem já faz orçamentos com frequência e quer mais recursos para organizar o negócio.**

O CTA deve ser orientado à ação:

> Começar grátis

> Testar Pro

Evitar:

> Comprar agora

O produto é SaaS e o usuário deve sentir que está iniciando uma experiência, não fazendo uma compra complexa.

---

# 19. FAQ

Criar perguntas relacionadas às principais objeções.

Sugestões:

### O que é a plataforma?

### Preciso instalar algum programa?

### Posso usar pelo celular?

### Posso criar uma conta gratuitamente?

### Quantos orçamentos posso criar no plano gratuito?

### Posso personalizar meus orçamentos?

### Posso colocar minha logo?

### Consigo gerar PDF?

### Posso enviar o orçamento pelo WhatsApp?

### Posso cancelar o plano Pro?

### Meus dados ficam seguros?

As respostas devem ser objetivas.

Não inventar políticas ou garantias que ainda não estejam definidas.

---

# 20. CTA final

Antes do Footer, criar uma seção forte.

Exemplo:

> **Pronto para deixar seus orçamentos mais profissionais?**

Subtexto:

> Crie sua conta gratuitamente e faça seu primeiro orçamento em poucos minutos.

Botão:

> **Criar minha conta grátis**

Adicionar uma segunda frase curta:

> Sem complicação. Comece em poucos minutos.

---

# 21. Copywriting e gatilhos

Utilizar princípios de copywriting sem exageros.

## Clareza

O usuário precisa entender o produto rapidamente.

## Dor

Explorar dores reais:

- Perder tempo fazendo orçamento.
- Repetir informações.
- Usar planilhas.
- Perder arquivos.
- Não saber o status de uma proposta.
- Enviar documentos pouco profissionais.

## Benefício

Mostrar o resultado:

- Mais rapidez.
- Organização.
- Profissionalismo.
- Facilidade.
- Controle.

## Redução de risco

Mostrar:

- Plano gratuito.
- Facilidade para começar.
- Ausência de complexidade.

Somente afirmar "sem cartão", "sem fidelidade", "cancele quando quiser" etc. se isso for realmente verdade.

## Urgência

Não utilizar falsa urgência.

Evitar:

> "Oferta acaba hoje!"

> "Últimas vagas!"

Em um SaaS, a confiança é mais importante.

---

# 22. SEO

A Landing Page deve ser construída com SEO desde o início.

## Title

Sugestão:

> Criador de Orçamentos Online | [Nome do Produto]

Manter aproximadamente 50–60 caracteres quando possível.

## Meta description

Sugestão:

> Crie orçamentos profissionais online, organize seus clientes e envie suas propostas de forma rápida. Comece gratuitamente.

Manter uma descrição natural e orientada à busca.

## Palavra-chave principal

Definir uma principal, por exemplo:

> criador de orçamentos online

Palavras relacionadas:

- sistema de orçamento.
- gerar orçamento online.
- orçamento profissional.
- criar orçamento.
- orçamento para prestador de serviço.
- modelo de orçamento.
- orçamento grátis.

Não fazer keyword stuffing.

---

# 23. Estrutura semântica

Utilizar corretamente:

- Um único `<h1>`.
- `<h2>` para seções.
- `<h3>` para subseções.
- `<header>`.
- `<main>`.
- `<section>`.
- `<article>` quando fizer sentido.
- `<footer>`.
- `<nav>`.

Não utilizar headings apenas para criar estilo visual.

---

# 24. SEO técnico

Implementar:

- `title`.
- `meta description`.
- `canonical`.
- Open Graph.
- Twitter/X Card.
- `robots`.
- `sitemap.xml`.
- `robots.txt`.
- URLs amigáveis.
- HTML semântico.
- Imagens otimizadas.
- `alt` descritivo.
- Lazy loading para imagens fora do viewport.
- Core Web Vitals favoráveis.
- Compressão de assets.
- Evitar JavaScript desnecessário no carregamento inicial.

Se o framework permitir SSR/SSG, priorizar renderização que favoreça indexação e performance.

---

# 25. Dados estruturados

Adicionar Schema.org quando fizer sentido.

Considerar:

- `SoftwareApplication`.
- `Organization`.
- `WebSite`.
- `FAQPage`, somente quando o conteúdo realmente cumprir as regras aplicáveis.

Não inserir informações falsas no JSON-LD.

Preço, avaliações, número de usuários e outros dados devem corresponder ao produto real.

---

# 26. SEO de imagens

Todas as imagens devem:

- Ser comprimidas.
- Ter dimensões adequadas.
- Utilizar formatos modernos quando possível.
- Possuir `alt` quando necessário.
- Evitar texto importante exclusivamente dentro de imagens.

Exemplo:

```html
<img
  src="/images/dashboard.webp"
  alt="Dashboard de orçamentos mostrando propostas e clientes"
/>
```

---

# 27. Performance

A LP é uma página de aquisição. Performance é prioridade.

Objetivos:

- Carregamento rápido.
- Baixo JavaScript inicial.
- Imagens otimizadas.
- Evitar bibliotecas desnecessárias.
- Evitar animações pesadas.
- Evitar vídeos autoplay.
- Priorizar conteúdo acima da dobra.

Sempre que possível, medir com Lighthouse e Core Web Vitals.

---

# 28. Design visual

A interface deve transmitir:

- Profissionalismo.
- Simplicidade.
- Confiança.
- Modernidade.
- Organização.

Evitar:

- Gradientes exagerados.
- Muitos efeitos.
- Glassmorphism excessivo.
- Animações constantes.
- Cores demais.
- Cards em excesso.
- Visual genérico de template.

Criar uma identidade visual consistente com o produto.

---

# 29. Responsividade

Mobile-first.

A página precisa funcionar perfeitamente em:

- 320px.
- 375px.
- 390px.
- 430px.
- Tablet.
- Desktop.
- Monitores grandes.

No mobile:

- CTA grande.
- Textos curtos.
- Cards empilhados.
- Pricing adaptado.
- Tabela comparativa com boa usabilidade.
- Menu simples.
- Imagens redimensionadas.

Nunca depender de hover para revelar informações importantes.

---

# 30. Acessibilidade

Implementar:

- Contraste adequado.
- Navegação por teclado.
- Focus states.
- Labels apropriados.
- `aria-label` quando necessário.
- Botões reais para ações.
- Links reais para navegação.
- Hierarquia semântica.
- Tamanho adequado dos alvos de toque.
- Respeito a `prefers-reduced-motion`.

Não utilizar apenas cor para comunicar estados.

---

# 31. Animações

Utilizar animações discretas.

Exemplos:

- Fade-in ao entrar no viewport.
- Pequena transição nos cards.
- Hover nos botões.
- Microinterações.

Evitar:

- Parallax pesado.
- Elementos piscando.
- Animações contínuas.
- Carrosséis automáticos.
- Efeitos que atrasem o acesso ao conteúdo.

A animação deve melhorar a percepção de qualidade, não competir com a conversão.

---

# 32. CTAs

Utilizar CTA primário de forma consistente.

Principal:

> **Criar minha conta grátis**

Alternativas:

> Começar grátis

> Criar meu primeiro orçamento

Evitar excesso de CTAs diferentes.

Todos os CTAs principais devem levar para o fluxo de cadastro.

Exemplo:

```text
LP
 ↓
/cadastro
 ↓
criação da conta
 ↓
/onboarding
 ↓
primeiro orçamento
```

O fluxo deve ter o menor número possível de etapas.

---

# 33. Instrumentação e métricas

A LP deve ser preparada para medir conversão.

Eventos recomendados:

- `landing_page_view`
- `cta_click`
- `signup_started`
- `signup_completed`
- `first_quote_created`
- `pricing_view`
- `pro_cta_click`
- `faq_open`
- `login_click`

Métricas principais:

### Conversão visitante → cadastro

```text
cadastros / visitantes
```

### Ativação

```text
usuários que criaram primeiro orçamento / novos cadastros
```

### Conversão para Pro

```text
clientes Pro / usuários cadastrados
```

O evento mais importante inicialmente não deve ser apenas o cadastro.

É:

> **Usuário criou o primeiro orçamento.**

Esse é um indicador muito melhor de ativação.

---

# 34. Testes A/B futuros

A arquitetura deve permitir testar:

### Headline

A:

> Crie orçamentos profissionais em poucos minutos.

B:

> Pare de perder tempo fazendo orçamentos no Excel.

### CTA

A:

> Criar minha conta grátis

B:

> Criar meu primeiro orçamento

### Hero

A:

> Screenshot do produto

B:

> Demonstração em vídeo

### Pricing

A:

> Gratuito + Pro

B:

> Pro destacado com comparação.

Não implementar uma infraestrutura complexa de A/B testing no MVP. Apenas estruturar componentes para facilitar alterações futuras.

---

# 35. Estratégia de aquisição futura

A arquitetura da LP deve permitir SEO programático/segmentado.

Criar futuramente páginas como:

```text
/orcamento-eletricista
/orcamento-pintor
/orcamento-fotografo
/orcamento-tecnico-informatica
/orcamento-manutencao
/orcamento-freelancer
```

Cada página deve possuir:

- H1 específico.
- Problemas daquele nicho.
- Benefícios relevantes.
- Exemplos de orçamento.
- FAQ específico.
- CTA.
- Internal linking.
- Meta title específico.
- Meta description específica.

Não duplicar simplesmente o conteúdo da homepage trocando o nome da profissão.

---

# 36. Regras para o Cursor

Ao implementar a Landing Page:

1. Não criar funcionalidades inexistentes no backend.
2. Não inventar depoimentos.
3. Não inventar números de clientes.
4. Não inventar avaliações.
5. Não inventar logos de empresas.
6. Não utilizar claims sem comprovação.
7. Não implementar preços diferentes dos definidos pelo produto.
8. Não criar links quebrados.
9. Não criar CTAs sem destino funcional.
10. Não sacrificar performance por efeitos visuais.
11. Não adicionar dependências desnecessárias.
12. Reutilizar componentes quando fizer sentido.
13. Manter componentes pequenos e coesos.
14. Manter acessibilidade.
15. Manter responsividade.
16. Manter SEO técnico.
17. Evitar duplicação de conteúdo.
18. Não colocar informações críticas somente em imagens.
19. Não utilizar lorem ipsum.
20. Todo texto deve estar em português do Brasil.

---

# 37. Arquitetura sugerida de componentes

Adaptar à arquitetura existente do projeto.

Exemplo:

```text
landing-page/
├── components/
│   ├── Header
│   ├── Hero
│   ├── ProductPreview
│   ├── Benefits
│   ├── HowItWorks
│   ├── TargetAudience
│   ├── Features
│   ├── Pricing
│   ├── Comparison
│   ├── FAQ
│   ├── FinalCTA
│   └── Footer
├── pages/
│   └── LandingPage
└── data/
    ├── pricing
    ├── features
    └── faq
```

Não seguir essa estrutura cegamente. Adaptar aos padrões já existentes no projeto.

---

# 38. Critérios de aceite

A Landing Page somente deve ser considerada pronta quando:

- [ ] O visitante entende o produto rapidamente.
- [ ] O Hero possui headline clara.
- [ ] Existe CTA de cadastro gratuito acima da dobra.
- [ ] Existe demonstração visual do produto.
- [ ] Benefícios estão claros.
- [ ] Existe seção "Como funciona".
- [ ] Existe seção de público-alvo.
- [ ] Existe apresentação das funcionalidades.
- [ ] Existe comparação Gratuito vs Pro.
- [ ] Existe FAQ.
- [ ] Existe CTA final.
- [ ] A página funciona em mobile.
- [ ] A página funciona em desktop.
- [ ] Existe meta title.
- [ ] Existe meta description.
- [ ] Existe canonical.
- [ ] Existe Open Graph.
- [ ] Existe estrutura semântica correta.
- [ ] Existe sitemap/robots quando aplicável ao projeto.
- [ ] Imagens estão otimizadas.
- [ ] Não existem links quebrados.
- [ ] CTAs levam ao cadastro.
- [ ] Eventos principais de conversão estão preparados.
- [ ] Não existem informações falsas.
- [ ] Lighthouse deve apresentar bom desempenho.
- [ ] A página pode ser navegada por teclado.
- [ ] Contraste e acessibilidade foram verificados.

---

# 39. Ordem de implementação recomendada

Não tentar fazer tudo de uma vez.

### Etapa 1 — Estrutura

Implementar:

- Header.
- Hero.
- Product Preview.
- Benefícios.
- Como funciona.
- Pricing.
- FAQ.
- CTA.
- Footer.

### Etapa 2 — Design

Refinar:

- Tipografia.
- Espaçamentos.
- Cores.
- Cards.
- Botões.
- Responsividade.
- Estados de interação.

### Etapa 3 — Conversão

Refinar:

- Headline.
- Subheadline.
- CTAs.
- Pricing.
- Objeções.
- FAQ.
- Microcopy.

### Etapa 4 — SEO

Implementar:

- Metadata.
- Semantic HTML.
- Structured Data.
- Open Graph.
- Sitemap.
- Robots.
- Canonical.

### Etapa 5 — Performance

Verificar:

- Bundle.
- Imagens.
- Lazy loading.
- Fontes.
- JavaScript.
- Core Web Vitals.

### Etapa 6 — Analytics

Implementar eventos de:

- CTA.
- Cadastro.
- Primeiro orçamento.
- Pricing.
- Pro.

---

# 40. Regra final de produto

A Landing Page não deve tentar vender todas as funcionalidades do SaaS.

Ela deve vender a próxima ação:

> **Criar uma conta e experimentar.**

A LP deve fazer o visitante pensar:

> "Isso resolve exatamente o problema que tenho."

e depois:

> "É fácil começar."

e finalmente:

> **"Vou testar."**

O principal objetivo da primeira versão é validar:

**Visitante → Cadastro → Primeiro orçamento → Retenção → Conversão para Pro.**

Não otimizar somente para quantidade de cadastros.

O verdadeiro indicador de sucesso é o número de usuários que chegam ao momento de valor do produto: **criar e enviar seu primeiro orçamento.**
