# Prompt para ChatGPT - Design de Interface para Sistema de Aluguel de Carros

## 🎯 Objetivo
Gerar imagens de mockups/designs de interface para um sistema web moderno e tecnológico de aluguel de automóveis, com visual inovador, atual e profissional.

---

## 📋 Contexto do Sistema

### Descrição Geral
Sistema web de aluguel de carros que conecta **clientes** que desejam alugar veículos com **agentes** (empresas e bancos) que analisam a viabilidade financeira e aprovam pedidos. O sistema gerencia todo o fluxo desde o cadastro até a geração de contratos e transferência de propriedade dos veículos.

### Público-Alvo
- **Clientes**: Pessoas físicas que buscam alugar carros
- **Agentes**: Empresas de locação e bancos que analisam e aprovam pedidos

---

## 🎨 Identidade Visual Atual

### Paleta de Cores (USAR ESTAS CORES)
- **Navy** `#07111F` - Base institucional, profundidade, fundos escuros
- **Slate** `#102E4A` - Contraste, elementos secundários
- **Blue** `#1D4ED8` - Ação principal, botões, links, CTAs
- **Cyan** `#4FD1C5` - Destaque visual, elementos de sucesso, frescor
- **Amber** `#F59E0B` - Atenção, progresso, alertas importantes

### Conceito Visual
- **Tecnologia e Confiabilidade**: Tons profundos de azul transmitindo segurança
- **Mobilidade e Fluxo**: Contrastes em ciano para dinamismo
- **Modernidade**: Design clean, minimalista, com muito espaço em branco
- **Profissionalismo**: Interface sofisticada e intuitiva

### Estilo de Design
- **Bordas arredondadas** (border-radius: 14px-28px)
- **Sombras suaves** para profundidade
- **Glassmorphism** em alguns elementos (backdrop-filter: blur)
- **Gradientes sutis** em backgrounds
- **Tipografia**: Inter, sans-serif moderna, pesos variados (400-900)
- **Espaçamento generoso** entre elementos
- **Cards elevados** com sombras
- **Badges e chips** com bordas arredondadas

---

## 🏗️ Arquitetura e Tecnologias

### Stack Tecnológica
- **Backend**: Java 17 + Micronaut Framework
- **Frontend**: Thymeleaf (server-side rendering) + CSS moderno + JavaScript vanilla
- **Banco de Dados**: Azure SQL Server
- **Autenticação**: Sessões HTTP com BCrypt para senhas
- **Build**: Gradle

### Padrão Arquitetural
- **MVC** (Model-View-Controller)
- Separação clara entre apresentação, lógica de negócio e persistência

---

## 📱 Funcionalidades do Sistema

### 1️⃣ Área Pública
- **Página Inicial (Home)**
  - Hero section com gradiente (Navy → Slate → Blue)
  - Apresentação do serviço
  - CTAs para Login e Cadastro
  - Estatísticas do sistema (total de pedidos, aprovados, etc.)

- **Login de Cliente**
  - Formulário com CPF e senha
  - Link para cadastro
  - Design clean e focado

- **Cadastro de Cliente**
  - Formulário com: Nome, CPF, RG, Endereço, Profissão, Senha, Confirmação de Senha
  - Validações em tempo real
  - Máscaras de CPF

### 2️⃣ Área do Cliente (Autenticada)
- **Dashboard do Cliente**
  - Topbar sticky com nome do usuário e botão de logout
  - Navegação entre seções
  - Cards com informações principais

- **Meus Dados**
  - Visualização e edição do cadastro
  - Botão de exclusão de conta (com modal de confirmação)

- **Gestão de Rendimentos**
  - Lista de até 3 rendimentos
  - Formulário para adicionar: Empregador (nome, CNPJ) e Valor
  - Cards para cada rendimento
  - Botão de exclusão

- **Meus Pedidos**
  - Lista de pedidos com status visual (badges coloridos):
    - PENDENTE (amarelo)
    - APROVADO (verde)
    - REPROVADO (vermelho)
    - CANCELADO (cinza)
  - Informações: Data, Veículo solicitado, Status
  - Ações: Editar (só PENDENTE), Cancelar (só PENDENTE), Ver Contrato (só APROVADO)

- **Novo Pedido**
  - **IMPORTANTE**: Seleção visual de automóvel com FOTOS/IMAGENS dos carros
  - Grid/carrossel de cards de veículos disponíveis
  - Cada card mostra: Foto do carro, Marca, Modelo, Ano, Placa
  - Campo de descrição da solicitação
  - Botão de envio

- **Visualizar Contrato**
  - Dados do contrato formatados
  - Número do contrato
  - Tipo de contrato (Locação Simples, Locação com Opção de Compra, Crédito Bancário)
  - Termos do contrato
  - Dados do cliente e do veículo
  - Botão para voltar

### 3️⃣ Área do Agente (Autenticada)
- **Login do Agente**
  - Formulário separado com username e senha
  - Visual diferenciado da área do cliente

- **Dashboard do Agente**
  - Topbar com nome do agente e logout
  - Estatísticas em cards:
    - Total de pedidos
    - Pendentes
    - Aprovados
    - Reprovados
    - Cancelados
  - Navegação: Pedidos, Frota, Contratos

- **Lista de Pedidos para Análise**
  - Tabela com todos os pedidos
  - Filtros por status
  - Colunas: ID, Cliente, CPF, Veículo, Data, Status
  - Ações rápidas: Aprovar (locação simples), Ver Detalhes
  - Badges de status coloridos

- **Detalhe do Pedido**
  - Informações completas do cliente
  - Lista de rendimentos do cliente (até 3)
  - Dados do veículo solicitado com FOTO
  - Descrição da solicitação
  - **Seletor de Tipo de Contrato**:
    - Locação Simples
    - Locação com Opção de Compra
    - Crédito Bancário
  - Botões: Aprovar (com tipo selecionado), Reprovar, Voltar

- **Gestão de Frota**
  - **IMPORTANTE**: Grid de cards com FOTOS dos automóveis
  - Cada card mostra: Foto, Marca, Modelo, Ano, Placa, Proprietário Atual
  - Botão para adicionar novo veículo
  - Ações: Editar, Ver Detalhes

- **Cadastro/Edição de Automóvel**
  - **Upload de foto do veículo** (campo de imagem)
  - Campos: Placa, Marca, Modelo, Ano
  - Preview da foto
  - Botões: Salvar, Cancelar

- **Visualizar Contrato (Agente)**
  - Mesma estrutura da visualização do cliente
  - Acesso a todos os contratos do sistema

---

## 🎨 Elementos Visuais Específicos para as Imagens

### 1. **Página Inicial (Home)**
- Hero section com gradiente Navy → Slate → Blue
- Ilustração/ícone de carro moderno
- Texto: "Sistema de Aluguel de Carros - Tecnologia e Mobilidade"
- 2 botões: "Fazer Login" (branco) e "Cadastrar-se" (transparente com borda)
- Cards com estatísticas (badges arredondados)

### 2. **Seleção de Veículos (Cliente)**
- **Grid responsivo de cards de carros**
- Cada card:
  - **FOTO REALISTA DO CARRO** (visão 3/4 frontal)
  - Marca e Modelo em destaque
  - Ano e Placa
  - Badge de disponibilidade
  - Botão "Selecionar" em azul
- Exemplos de carros:
  - Sedan executivo (ex: Honda Civic, Toyota Corolla)
  - SUV moderno (ex: Jeep Compass, Hyundai Creta)
  - Hatch compacto (ex: VW Polo, Fiat Argo)
  - Carro de luxo (ex: BMW Série 3, Mercedes Classe C)

### 3. **Dashboard do Agente**
- Cards de estatísticas com ícones
- Gráfico ou visualização de dados
- Lista de pedidos recentes
- Navegação lateral ou top tabs

### 4. **Análise de Pedido (Agente)**
- Layout em 2 colunas:
  - **Esquerda**: Dados do cliente, rendimentos
  - **Direita**: **FOTO DO VEÍCULO** solicitado, dados do carro
- Seletor de tipo de contrato (radio buttons ou dropdown estilizado)
- Botões de ação destacados

### 5. **Gestão de Frota**
- **Grid de cards de veículos com FOTOS**
- Cada card:
  - **FOTO DO CARRO**
  - Marca, Modelo, Ano
  - Placa
  - Badge de proprietário (Locadora/Cliente/Banco)
  - Ações: Editar, Detalhes

### 6. **Formulário de Cadastro de Veículo**
- **Área de upload de foto** (drag & drop ou botão)
- **Preview da foto** do carro
- Campos de texto para Placa, Marca, Modelo, Ano
- Validações visuais

---

## 🖼️ Especificações das Imagens de Carros

### Estilo das Fotos
- **Fundo limpo** (branco ou gradiente suave)
- **Iluminação profissional**
- **Ângulo 3/4 frontal** (mostra frente e lateral)
- **Alta qualidade**, realistas
- **Carros modernos** (2018-2024)
- **Cores variadas** (prata, preto, branco, azul, vermelho)

### Tipos de Veículos para Mostrar
1. **Sedan Executivo** - Ex: Honda Civic, Toyota Corolla
2. **SUV Médio** - Ex: Jeep Compass, Hyundai Creta
3. **Hatch Compacto** - Ex: VW Polo, Fiat Argo
4. **SUV Grande** - Ex: Toyota SW4, Chevrolet Trailblazer
5. **Carro de Luxo** - Ex: BMW Série 3, Mercedes Classe C
6. **Picape** - Ex: Fiat Toro, Chevrolet S10

---

## 📐 Componentes de UI para Incluir

### Badges de Status
- **PENDENTE**: Fundo amarelo claro (#FEF3C7), texto marrom (#92400E)
- **APROVADO**: Fundo verde claro (#DCFCE7), texto verde escuro (#166534)
- **REPROVADO**: Fundo vermelho claro (#FEE2E2), texto vermelho escuro (#991B1B)
- **CANCELADO**: Fundo cinza claro (#E2E8F0), texto cinza escuro (#334155)

### Botões
- **Primário**: Fundo azul (#1D4ED8), texto branco, bordas arredondadas
- **Secundário**: Fundo transparente, borda azul, texto azul
- **Perigo**: Fundo vermelho claro, texto vermelho escuro
- **Fantasma**: Fundo transparente, borda cinza

### Cards
- Fundo branco
- Borda sutil (#D9E4EF)
- Sombra suave
- Border-radius: 20px
- Padding generoso

### Tabelas
- Header com fundo cinza claro
- Linhas alternadas
- Hover effect
- Bordas arredondadas no container

### Modais
- Overlay escuro semi-transparente
- Card centralizado
- Sombra forte
- Bordas arredondadas

---

## 🎯 Requisitos para as Imagens Geradas

### Geral
1. **Resolução**: Alta qualidade, mínimo 1920x1080px
2. **Formato**: Widescreen (16:9 ou 16:10)
3. **Estilo**: Moderno, clean, profissional, tecnológico
4. **Cores**: Usar EXATAMENTE a paleta definida
5. **Tipografia**: Sans-serif moderna, hierarquia clara
6. **Espaçamento**: Generoso, respirável
7. **Consistência**: Manter o mesmo estilo em todas as telas

### Específico
1. **SEMPRE incluir fotos realistas de carros** nas telas de:
   - Seleção de veículo (cliente)
   - Detalhe do pedido (agente)
   - Gestão de frota (agente)
   - Cadastro de veículo (agente)

2. **Mostrar dados realistas**:
   - Nomes de clientes fictícios
   - CPFs formatados (XXX.XXX.XXX-XX)
   - Placas de carros (ABC-1D23)
   - Datas e horários
   - Valores monetários (R$ X.XXX,XX)

3. **Incluir elementos interativos**:
   - Botões com estados (hover, active)
   - Campos de formulário
   - Dropdowns/selects
   - Checkboxes/radio buttons
   - Modais de confirmação

---

## 📸 Telas Prioritárias para Gerar

### Prioridade ALTA (gerar primeiro)
1. **Página Inicial (Home)** - Hero + CTAs
2. **Seleção de Veículos (Cliente)** - Grid com fotos de carros
3. **Dashboard do Agente** - Estatísticas + lista de pedidos
4. **Análise de Pedido (Agente)** - Dados do cliente + foto do carro + seletor de contrato
5. **Gestão de Frota (Agente)** - Grid de cards com fotos dos carros

### Prioridade MÉDIA
6. **Login de Cliente**
7. **Cadastro de Cliente**
8. **Meus Pedidos (Cliente)** - Lista com badges de status
9. **Novo Pedido (Cliente)** - Formulário + seleção de veículo
10. **Cadastro de Veículo (Agente)** - Upload de foto + formulário

### Prioridade BAIXA
11. **Gestão de Rendimentos (Cliente)**
12. **Visualizar Contrato (Cliente/Agente)**
13. **Login do Agente**
14. **Meus Dados (Cliente)**

---

## 💡 Dicas para o ChatGPT

1. **Fotos de carros**: Use imagens realistas de carros modernos, com fundo limpo e iluminação profissional
2. **Consistência**: Mantenha o mesmo estilo visual em todas as telas
3. **Hierarquia**: Use tamanhos de fonte e pesos diferentes para criar hierarquia
4. **Espaçamento**: Deixe bastante espaço em branco, não sobrecarregue
5. **Cores**: Use a paleta definida de forma consistente
6. **Ícones**: Use ícones modernos e minimalistas quando necessário
7. **Responsividade**: Considere que o design deve funcionar em desktop
8. **Acessibilidade**: Contraste adequado entre texto e fundo
9. **Microinterações**: Sugira estados de hover, focus, active nos elementos
10. **Realismo**: Use dados fictícios mas realistas (nomes, CPFs, placas, etc.)

---

## 🚀 Prompt Final para Cada Tela

**Exemplo de prompt para a tela de Seleção de Veículos:**

"Crie um mockup de interface web moderna e tecnológica para a tela de 'Seleção de Veículos' de um sistema de aluguel de carros. Use a paleta de cores: Navy #07111F, Slate #102E4A, Blue #1D4ED8, Cyan #4FD1C5, Amber #F59E0B. A tela deve ter:
- Topbar sticky com logo, nome do usuário 'João Silva' e botão de logout
- Título 'Escolha seu veículo'
- Grid responsivo (3 colunas) com 6 cards de carros
- Cada card deve ter: foto realista do carro (sedan, SUV, hatch), marca e modelo em destaque, ano, placa, badge de disponibilidade, botão 'Selecionar' em azul
- Exemplos de carros: Honda Civic 2023, Jeep Compass 2024, VW Polo 2022, Toyota Corolla 2023, Hyundai Creta 2024, Fiat Argo 2023
- Design clean, bordas arredondadas (20px), sombras suaves, espaçamento generoso
- Fundo com gradiente suave de cinza claro
- Tipografia sans-serif moderna (Inter)
- Resolução 1920x1080px, estilo profissional e inovador"

---

## ✅ Checklist Final

Antes de gerar cada imagem, confirme:
- [ ] Paleta de cores correta
- [ ] Fotos de carros incluídas (quando aplicável)
- [ ] Tipografia moderna e legível
- [ ] Espaçamento adequado
- [ ] Bordas arredondadas
- [ ] Sombras suaves
- [ ] Badges de status coloridos
- [ ] Botões com estilo definido
- [ ] Dados realistas
- [ ] Layout responsivo
- [ ] Hierarquia visual clara
- [ ] Consistência com outras telas

---

**Boa sorte com a geração das imagens! 🚗✨**
