# Prompt para Cursor - Implementar Tela Home do Sistema de Aluguel de Carros

## 🎯 Objetivo
Criar a página inicial (home) pública do sistema de aluguel de carros, seguindo o design moderno e tecnológico da referência visual fornecida, adaptado à identidade visual do projeto.

---

## 📋 Contexto Técnico

### Stack Atual
- **Backend**: Java 17 + Micronaut Framework
- **Template Engine**: Thymeleaf
- **CSS**: Arquivo `src/main/resources/public/css/app.css` (já existe)
- **JavaScript**: Vanilla JS em `src/main/resources/public/js/`
- **Views**: `src/main/resources/views/`

### Paleta de Cores do Projeto
```css
--brand-navy: #07111f;
--brand-slate: #102e4a;
--brand-blue: #1d4ed8;
--brand-cyan: #4fd1c5;
--brand-amber: #f59e0b;
```

### Arquivos Existentes
- Controller: `src/main/java/sistemaaluguelcarros/controller/HomeController.java`
- View: `src/main/resources/views/home.html`
- CSS: `src/main/resources/public/css/app.css`

---

## 🎨 Design da Página Home

### Estrutura da Página

#### 1. **Header/Navbar** (Sticky)
```
┌─────────────────────────────────────────────────────────────┐
│ [LOGO] AutoDrive    Home  Frota  Como Funciona  Contato    │
│                                          [Entrar] [Cadastrar]│
└─────────────────────────────────────────────────────────────┘
```

**Especificações:**
- Fundo: `rgba(255, 255, 255, 0.95)` com `backdrop-filter: blur(10px)`
- Altura: 80px
- Sticky no topo
- Logo: Usar o arquivo `assets/images/AutoDriveLOGO.png`
- Links de navegação: cor `--brand-slate`, hover `--brand-blue`
- Botões:
  - "Entrar": outline azul, transparente
  - "Cadastrar": fundo `--brand-blue`, texto branco

#### 2. **Hero Section** (Seção Principal)
```
┌─────────────────────────────────────────────────────────────┐
│                                                               │
│  [Texto à esquerda]              [Imagem de carro à direita] │
│                                                               │
│  ALUGUEL DE VEÍCULOS PREMIUM                                 │
│                                                               │
│  Mobilidade premium,                                         │
│  com inteligência                                            │
│  e presença.                                                 │
│                                                               │
│  Alugue veículos extraordinários com tecnologia,             │
│  agilidade e atenção em cada detalhe.                        │
│                                                               │
│  [Explore Frota →]  [Como funciona]                          │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

**Especificações:**
- Layout: Grid 2 colunas (50/50)
- Altura mínima: 600px
- Fundo: Gradiente `linear-gradient(135deg, var(--brand-navy) 0%, var(--brand-slate) 50%, var(--brand-blue) 100%)`
- Texto:
  - Eyebrow: "ALUGUEL DE VEÍCULOS PREMIUM" (uppercase, pequeno, opacidade 0.8)
  - Título principal: "Mobilidade premium," (branco, bold 900, 3.5rem)
  - Destaque: "com inteligência" (cor `--brand-amber`, bold 900, 3.5rem)
  - Continuação: "e presença." (branco, bold 900, 3.5rem)
  - Subtítulo: texto descritivo (branco com opacidade 0.9, 1.1rem)
- Imagem:
  - Foto de carro de luxo (sedan executivo, ângulo 3/4)
  - Posicionamento: à direita, levemente sobreposto
  - Usar imagem de alta qualidade
- Botões:
  - "Explore Frota": fundo `--brand-amber`, texto branco, com ícone de seta
  - "Como funciona": outline branco, transparente

#### 3. **Features Bar** (Barra de Benefícios)
```
┌─────────────────────────────────────────────────────────────┐
│  [⚡ Ícone]          [🚗 Ícone]         [🎧 Ícone]          │
│  Aprovação rápida   Frota selecionada  Atendimento         │
│  Digital e sem      Veículos premium   Suporte dedicado    │
│  burocracia         e com seguro       24/7                 │
└─────────────────────────────────────────────────────────────┘
```

**Especificações:**
- Fundo: `--brand-navy` ou `#1a1a1a`
- Padding: 60px vertical
- Layout: Grid 4 colunas
- Cada item:
  - Ícone: 48px, cor `--brand-cyan`
  - Título: branco, bold 700
  - Descrição: branco com opacidade 0.7

#### 4. **Seção "Encontre o seu próximo carro"**
```
┌─────────────────────────────────────────────────────────────┐
│  Encontre o seu próximo carro          Ver toda a frota → │
│                                                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ [Foto]   │  │ [Foto]   │  │ [Foto]   │  │ [Foto]   │   │
│  │ Sedans   │  │ SUVs     │  │ Esportivos│  │ Elétricos│   │
│  │ Executivos│  │ Premium  │  │          │  │          │   │
│  │          │  │          │  │          │  │          │   │
│  │ 👤 5  🧳 4│  │ 👤 5  🧳 5│  │ 👤 2  🧳 2│  │ 👤 5  🧳 4│   │
│  │ A partir de│  │ A partir de│  │ A partir de│  │ A partir de│   │
│  │ R$ 549/dia│  │ R$ 659/dia│  │ R$ 1.299  │  │ R$ 599/dia│   │
│  │     [→]  │  │     [→]  │  │     [→]  │  │     [→]  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
```

**Especificações:**
- Fundo: branco ou `--bg`
- Padding: 100px vertical
- Título: `--brand-slate`, bold 900, 2.5rem
- Link "Ver toda a frota": `--brand-blue`, com seta
- Cards:
  - Grid 4 colunas (responsivo: 2 em tablet, 1 em mobile)
  - Fundo: `--brand-navy` ou escuro
  - Border-radius: 20px
  - Foto do carro: altura 200px, object-fit cover
  - Texto branco
  - Ícones de passageiros e bagagem
  - Preço em destaque
  - Botão circular com seta no canto inferior direito

#### 5. **Seção "Experiência sem burocracia"**
```
┌─────────────────────────────────────────────────────────────┐
│  [Lado esquerdo - Texto]        [Lado direito - Imagem]     │
│                                                               │
│  EXPERIÊNCIA QUE FLUI                                        │
│                                                               │
│  Experiência                    [Foto: Pessoa com carro      │
│  sem burocracia.                 em ambiente moderno]        │
│                                                               │
│  Os pedidos são simples e                                    │
│  rápidos, tudo por aqui.        [Lista de benefícios:]       │
│                                  ✓ Reserva Rápida            │
│  [1] Escolha seu carro          ✓ Seguro premium            │
│  [2] Reserve em minutos         ✓ Devolução facilitada      │
│  [3] Retire e aproveite                                      │
│                                                               │
│  [Como funciona →]                                           │
└─────────────────────────────────────────────────────────────┘
```

**Especificações:**
- Layout: Grid 2 colunas (40/60)
- Fundo: `--bg-accent`
- Padding: 120px vertical
- Lado esquerdo:
  - Eyebrow: "EXPERIÊNCIA QUE FLUI"
  - Título: bold 900, 2.5rem
  - Destaque: "sem burocracia" em `--brand-amber`
  - Steps numerados (1, 2, 3) com círculos
- Lado direito:
  - Foto de alta qualidade
  - Overlay com lista de benefícios
  - Fundo escuro semi-transparente

#### 6. **Seção "Por que escolher AutoDrive?"**
```
┌─────────────────────────────────────────────────────────────┐
│              Por que escolher a AutoDrive?                   │
│                                                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ [Ícone]  │  │ [Ícone]  │  │ [Ícone]  │  │ [Ícone]  │   │
│  │ Segurança│  │ Plantão  │  │ Liberdade│  │ Gestão   │   │
│  │ em 1º    │  │ premium  │  │ total    │  │ inteligente│   │
│  │ lugar    │  │          │  │          │  │          │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│                                                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ [Ícone]  │  │ [Ícone]  │  │ [Ícone]  │  │ [Ícone]  │   │
│  │ Gestão   │  │ Concierge│  │ Ideal para│  │ Mais     │   │
│  │ inteligente│  │ 24/7    │  │ empresas │  │ vantagens│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
```

**Especificações:**
- Fundo: branco
- Padding: 100px vertical
- Título centralizado: bold 900, 2.5rem
- Grid: 4 colunas, 2 linhas
- Cada card:
  - Ícone: 56px, cor `--brand-blue`
  - Título: bold 700
  - Descrição: cor `--muted`
  - Padding: 30px
  - Hover: leve elevação

#### 7. **Footer**
```
┌─────────────────────────────────────────────────────────────┐
│  [LOGO] AutoDrive                                            │
│  Tecnologia e atendimento que fluem                          │
│  toda a diferença.                                           │
│                                                               │
│  [Instagram] [LinkedIn] [YouTube] [Email]                    │
│                                                               │
│  NAVEGAÇÃO        FROTA          EMPRESAS      RECEBA        │
│  Home             Sedans         Soluções      Newsletter    │
│  Frota            SUVs Premium   Gestão        [Email____]   │
│  Como funciona    Esportivos     Parcerias     [Enviar →]    │
│  Sobre            Elétricos      Benefícios                  │
│  Contato          Ver toda                                   │
│                                                               │
│  © 2024 AutoDrive. Todos os direitos reservados.             │
│                                  Termos de uso | Privacidade │
└─────────────────────────────────────────────────────────────┘
```

**Especificações:**
- Fundo: `--brand-navy`
- Cor do texto: branco com opacidade variada
- Padding: 80px vertical
- Grid: 4 colunas
- Links: hover `--brand-cyan`
- Ícones sociais: 40px, hover scale
- Newsletter: input + botão inline

---

## 📝 Tarefas de Implementação

### 1. Atualizar HomeController.java
```java
@Get
public ModelAndView<Map<String, Object>> index() {
    Map<String, Object> model = new LinkedHashMap<>();
    
    // Estatísticas para a home
    model.put("totalPedidos", pedidoAluguelService.contarTodos());
    model.put("totalAprovados", pedidoAluguelService.contarPorStatus(StatusPedido.APROVADO));
    model.put("totalVeiculos", automovelService.contarTodos());
    
    // Veículos em destaque (4 categorias)
    model.put("veiculosDestaque", automovelService.listarDestaques());
    
    return new ModelAndView<>("home", model);
}
```

### 2. Criar/Atualizar home.html
- Estrutura HTML5 semântica
- Usar Thymeleaf para dados dinâmicos
- Incluir o CSS existente: `<link rel="stylesheet" th:href="@{/css/app.css}">`
- Adicionar classes CSS específicas da home

### 3. Adicionar CSS no app.css
- Criar seção `/* === HOME PAGE === */`
- Estilos para:
  - `.home-navbar`
  - `.home-hero`
  - `.home-features-bar`
  - `.home-fleet-section`
  - `.home-experience-section`
  - `.home-benefits-section`
  - `.home-footer`
  - `.car-card`
  - `.benefit-card`
  - `.step-number`

### 4. Adicionar JavaScript (opcional)
- Scroll suave para âncoras
- Animações on scroll (fade in, slide up)
- Carrossel de veículos (se necessário)

---

## 🎨 Componentes Específicos

### Card de Veículo
```html
<div class="car-card">
  <div class="car-card-image">
    <img th:src="@{/images/cars/sedan-executivo.jpg}" alt="Sedan Executivo">
  </div>
  <div class="car-card-content">
    <h3 class="car-card-category">Sedans Executivos</h3>
    <p class="car-card-description">Conforto e sofisticação para o dia a dia</p>
    <div class="car-card-specs">
      <span class="spec-item">
        <svg><!-- ícone passageiros --></svg>
        <span>5</span>
      </span>
      <span class="spec-item">
        <svg><!-- ícone bagagem --></svg>
        <span>4</span>
      </span>
      <span class="spec-item">
        <svg><!-- ícone câmbio --></svg>
        <span>Automático</span>
      </span>
    </div>
    <div class="car-card-footer">
      <div class="car-card-price">
        <span class="price-label">A partir de</span>
        <span class="price-value">R$ 549<span class="price-period">/dia</span></span>
      </div>
      <a href="/pedidos/novo" class="car-card-button">
        <svg><!-- ícone seta --></svg>
      </a>
    </div>
  </div>
</div>
```

### CSS do Card
```css
.car-card {
  background: var(--brand-navy);
  border-radius: 20px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.car-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-lg);
}

.car-card-image {
  height: 220px;
  overflow: hidden;
}

.car-card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.car-card:hover .car-card-image img {
  transform: scale(1.05);
}

.car-card-content {
  padding: 24px;
  color: white;
}

.car-card-category {
  font-size: 1.35rem;
  font-weight: 700;
  margin: 0 0 8px;
}

.car-card-description {
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 20px;
  font-size: 0.95rem;
}

.car-card-specs {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.spec-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
}

.car-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.car-card-price {
  display: flex;
  flex-direction: column;
}

.price-label {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 4px;
}

.price-value {
  font-size: 1.75rem;
  font-weight: 900;
  color: var(--brand-cyan);
}

.price-period {
  font-size: 1rem;
  font-weight: 400;
}

.car-card-button {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--brand-blue);
  display: grid;
  place-items: center;
  transition: background 0.2s ease, transform 0.2s ease;
}

.car-card-button:hover {
  background: var(--brand-cyan);
  transform: scale(1.1);
}
```

---

## 🖼️ Imagens Necessárias

### Criar pasta: `src/main/resources/public/images/`

#### Subpastas:
- `images/cars/` - Fotos dos veículos
- `images/hero/` - Imagem principal do hero
- `images/experience/` - Foto da seção de experiência

#### Imagens específicas:
1. **Hero**: `hero-car.png` - Sedan de luxo, fundo transparente ou limpo
2. **Categorias de carros**:
   - `sedan-executivo.jpg` - Honda Civic, Toyota Corolla
   - `suv-premium.jpg` - Jeep Compass, Hyundai Creta
   - `esportivo.jpg` - Carro esportivo
   - `eletrico.jpg` - Carro elétrico moderno
3. **Experiência**: `experience-photo.jpg` - Pessoa com carro em ambiente moderno

---

## ✅ Checklist de Implementação

- [ ] Atualizar `HomeController.java` com dados dinâmicos
- [ ] Criar estrutura HTML completa em `home.html`
- [ ] Implementar navbar sticky com logo
- [ ] Criar hero section com gradiente e imagem
- [ ] Implementar features bar com ícones
- [ ] Criar grid de cards de veículos
- [ ] Implementar seção de experiência
- [ ] Criar seção de benefícios
- [ ] Implementar footer completo
- [ ] Adicionar todos os estilos CSS
- [ ] Garantir responsividade (mobile, tablet, desktop)
- [ ] Adicionar animações suaves
- [ ] Testar todos os links
- [ ] Validar acessibilidade (alt texts, aria-labels)
- [ ] Otimizar imagens
- [ ] Testar performance

---

## 🚀 Comandos para Testar

```bash
# Compilar e executar
./gradlew run

# Acessar no navegador
http://localhost:8080
```

---

## 💡 Dicas de Implementação

1. **Reutilize o CSS existente**: O arquivo `app.css` já tem variáveis, botões, cards definidos
2. **Mantenha a consistência**: Use as mesmas classes e padrões do resto do sistema
3. **Responsividade**: Use media queries para mobile (<768px) e tablet (<1024px)
4. **Performance**: Otimize imagens (WebP, lazy loading)
5. **Acessibilidade**: Adicione alt texts, aria-labels, contraste adequado
6. **SEO**: Use tags semânticas (header, nav, main, section, footer)
7. **Animações**: Use `transition` e `transform` para suavidade
8. **Gradientes**: Use os gradientes já definidos no CSS
9. **Ícones**: Use SVG inline ou biblioteca como Heroicons/Feather Icons
10. **Dados dinâmicos**: Use Thymeleaf para estatísticas e veículos do banco

---

**Pronto para implementar! 🚗✨**
