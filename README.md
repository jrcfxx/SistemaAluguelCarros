<div align="center">

# Sistema de Aluguel de Carros

<p>
  Aplicação acadêmica desenvolvida para a disciplina de <strong>Laboratório de Desenvolvimento de Software</strong>,
  com foco em <strong>cadastro de clientes</strong>, <strong>gestão de pedidos de aluguel</strong>,
  <strong>análise financeira</strong>, <strong>geração de contratos</strong> e
  <strong>transferência de propriedade de automóveis</strong>.
</p>

<p>
  <img src="https://img.shields.io/badge/Java-17-red?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle" />
  <img src="https://img.shields.io/badge/Micronaut-4.x-1B1F23?style=for-the-badge&logo=micronaut&logoColor=white" alt="Micronaut" />
  <img src="https://img.shields.io/badge/Thymeleaf-Views-005F0F?style=for-the-badge" alt="Thymeleaf" />
  <img src="https://img.shields.io/badge/H2-Database-0B6E99?style=for-the-badge" alt="H2 Database" />
  <img src="https://img.shields.io/badge/Status-Em%20Modelagem%20e%20Implementação-F59E0B?style=for-the-badge" alt="Status do projeto" />
</p>

</div>

---

## Sumário

- [Sobre o projeto](#sobre-o-projeto)
- [Objetivos do sistema](#objetivos-do-sistema)
- [Estado atual do repositório](#estado-atual-do-repositório)
- [Stack tecnológica](#stack-tecnológica)
- [Arquitetura proposta](#arquitetura-proposta)
- [Diagramas do projeto](#diagramas-do-projeto)
- [Modelo de domínio](#modelo-de-domínio)
- [Regras de negócio](#regras-de-negócio)
- [Histórias de usuário](#histórias-de-usuário)
- [Estrutura atual do projeto](#estrutura-atual-do-projeto)
- [Como executar](#como-executar)
- [Roadmap sugerido](#roadmap-sugerido)
- [Referências](#referências)

## Sobre o projeto

O sistema foi concebido para permitir que clientes realizem pedidos de aluguel de automóveis pela internet, enquanto agentes, como empresas e bancos, possam analisar a viabilidade financeira dessas solicitações.

Após a aprovação do pedido, o sistema deve possibilitar a geração de contrato e refletir a propriedade do automóvel conforme as regras definidas pelo tipo de contrato.

## Objetivos do sistema

De acordo com a especificação do laboratório, o sistema deve:

- permitir cadastro e autenticação de clientes;
- permitir criar, consultar, modificar e cancelar pedidos de aluguel;
- permitir que agentes analisem, aprovem ou reprovem pedidos;
- gerar contrato após a aprovação;
- armazenar rendimentos do cliente, limitados a `3` registros;
- registrar automóveis e refletir sua propriedade conforme o contrato;
- atender a uma arquitetura `MVC` em `Java`.

## Estado atual do repositório

Atualmente, o repositório já possui a base de configuração do projeto e os artefatos de modelagem, incluindo:

- configuração inicial com `Gradle`;
- definição de aplicação `Java` com `Micronaut`;
- dependências para camada web, persistência e views server-side;
- diagramas UML em `Diagrams/`;
- wrappers do Gradle para execução e build.

> **Importante**
>
> Neste momento, o projeto ainda não possui a pasta `src/` com a implementação da aplicação. Portanto, este README documenta o estado atual da modelagem, da arquitetura planejada e dos requisitos levantados até agora.

## Stack tecnológica

| Tecnologia | Papel no projeto |
|---|---|
| `Java 17` | Linguagem principal da aplicação |
| `Gradle` | Build, execução e gerenciamento de dependências |
| `Micronaut` | Base da aplicação web |
| `Micronaut Data + Hibernate/JPA` | Persistência e acesso a dados |
| `Thymeleaf` | Renderização de páginas no servidor |
| `H2 Database` | Banco de dados para ambiente local |
| `JUnit 5` | Testes automatizados |

## Arquitetura proposta

Pelo enunciado e pela configuração presente no projeto, a solução foi pensada como uma aplicação web em `Java`, seguindo o padrão `MVC`, com separação entre apresentação, regras de negócio e persistência.

### Visão arquitetural

O fluxo arquitetural esperado para o projeto é:

`Cliente/Agente -> Interface Web -> Controllers -> Services -> Repositories -> Banco de Dados`

Além disso, a camada de serviços concentra as regras de negócio ligadas aos módulos de:

- `Pedidos`
- `Contratos`
- `Clientes`
- `Rendimentos`

### Subsystems identificados no enunciado

O documento da atividade divide o sistema em dois grandes subsistemas:

- `Gestão de pedidos e contratos`
- `Construção dinâmica das páginas web`

### Arquitetura de implementação esperada

| Camada | Responsabilidade |
|---|---|
| `Views` | Renderização das páginas com `Thymeleaf` |
| `Controllers` | Recebimento das requisições e controle do fluxo MVC |
| `Services` | Aplicação das regras de negócio |
| `Repositories` | Persistência com `JPA/Hibernate` |
| `Database` | Armazenamento local com `H2` |

> **Observação**
>
> Caso o preview local do editor não renderize diagramas `Mermaid`, a visualização costuma funcionar normalmente no GitHub. Os diagramas UML oficiais do projeto também estão disponíveis em `Diagrams/`.

## Diagramas do projeto

Os modelos UML já presentes no repositório estão na pasta `Diagrams/`:

- `Diagrams/CasoDeUso.drawio`
- `Diagrams/DiagramaDeClasses.drawio`

Esses arquivos podem ser abertos no [diagrams.net](https://www.diagrams.net/) para edição e visualização completa.

### Diagrama de Casos de Uso

<div align="center">
  <img src="./Diagrams/CasoDeUso.drawio.png" alt="Diagrama de Casos de Uso do Sistema de Aluguel de Carros" width="100%" />
</div>

O diagrama de casos de uso modela os principais atores e suas interações:

| Ator | Ações principais |
|---|---|
| `Cliente` | cadastrar-se, fazer login, criar pedido, consultar pedido, modificar pedido, cancelar pedido |
| `Agente` | analisar pedido, aprovar pedido, reprovar pedido, modificar pedido |
| `Empresa` | especialização de `Agente` |
| `Banco` | especialização de `Agente` |

### Diagrama de Classes

O diagrama de classes apresenta as entidades centrais do domínio e seus relacionamentos:

- `Cliente`
- `Rendimento`
- `Empregador`
- `Automóvel`
- `PedidoAluguel`
- `Contrato`
- `Agente`
- `Banco`
- `Empresa`

## Modelo de domínio

### Diagrama de Classes

<div align="center">
  <img src="./Diagrams/DiagramaDeClasses.drawio.png" alt="Diagrama de Classes do Sistema de Aluguel de Carros" width="100%" />
</div>

### Entidades principais

| Entidade | Responsabilidade |
|---|---|
| `Cliente` | Representa o usuário que solicita o aluguel |
| `Rendimento` | Registra a capacidade financeira do cliente |
| `Empregador` | Identifica a origem do rendimento |
| `Automóvel` | Representa o veículo alugado no sistema |
| `PedidoAluguel` | Controla a solicitação de aluguel e seu status |
| `Contrato` | Formaliza a relação de aluguel após aprovação |
| `Agente` | Analisa pedidos e decide pela aprovação ou reprovação |
| `Banco` | Pode conceder crédito vinculado ao pedido |
| `Empresa` | Participa da gestão do aluguel |

## Regras de negócio

As principais regras de negócio levantadas até o momento são:

- o sistema só pode ser utilizado após cadastro prévio;
- o `CPF` do cliente deve ser único;
- o cliente pode possuir no máximo `3` rendimentos;
- apenas clientes autenticados podem criar pedidos;
- pedidos iniciam com status `PENDENTE`;
- pedidos com status `PENDENTE` podem ser modificados e cancelados;
- pedidos aprovados ou reprovados não podem ser modificados;
- contratos só podem ser gerados para pedidos `APROVADO`;
- a propriedade do automóvel pode variar conforme o tipo de contrato;
- o aluguel pode estar associado a contrato de crédito concedido por banco agente.


## Estrutura atual do projeto

```text
.
|-- Diagrams/
|   |-- CasoDeUso.drawio
|   `-- DiagramaDeClasses.drawio
|-- gradle/
|-- build.gradle
|-- gradle.properties
|-- gradlew
|-- gradlew.bat
|-- settings.gradle
`-- README.md
```

## Como executar

### Pré-requisitos

- `Java 17`

### Comandos

No Windows:

```powershell
.\gradlew.bat run
```

Para compilar e testar:

```powershell
.\gradlew.bat build
.\gradlew.bat test
```

> **Observação**
>
> A configuração de build já está preparada, mas a implementação da aplicação ainda não foi adicionada em `src/`. Portanto, a execução completa do sistema depende da criação do código-fonte.

## Roadmap sugerido

Com base no laboratório e no estado atual do repositório, os próximos passos mais naturais são:

1. criar a estrutura `src/main/java` e `src/main/resources`;
2. implementar o CRUD de cliente;
3. adicionar autenticação e login;
4. implementar o fluxo de pedido de aluguel;
5. persistir entidades com `JPA` e `H2`;
6. criar views com `Thymeleaf`;
7. revisar os diagramas conforme a implementação evoluir.

## Referências

- Enunciado da atividade: `LABORATÓRIO 02 - Sistema de Aluguel de Carros.pdf`
- Diagramas UML: `Diagrams/CasoDeUso.drawio` e `Diagrams/DiagramaDeClasses.drawio`
