# Cliente.java

## Visão Geral da Classe

A classe `Cliente` representa a entidade central que modela os usuários do sistema de aluguel de veículos. Ela armazena informações cadastrais, credenciais de autenticação e o relacionamento com os rendimentos declarados pelo cliente.

Do ponto de vista da modelagem orientada a objetos, esta entidade é um importante agregado do domínio, pois concentra dados essenciais utilizados em autenticação, análise financeira, criação de pedidos e geração de contratos.

---

## Boas Práticas Observadas

### Uso de Entidade JPA

A anotação `@Entity` identifica corretamente a classe como entidade persistente.

### Restrição de Unicidade

O CPF é definido como único, garantindo integridade de dados e prevenindo duplicidades.

### Uso de Bean Validation

Anotações como `@NotBlank` reforçam restrições estruturais no próprio modelo.

### Senha Armazenada como Hash

O atributo `senhaHash` demonstra preocupação com segurança e proteção de credenciais.

### Relacionamento com Rendimento

O uso de `@OneToMany` com `cascade = CascadeType.ALL` e `orphanRemoval = true` mantém consistência entre o cliente e seus rendimentos.

### Inicialização da Coleção

A lista `rendimentos` é inicializada no momento da declaração, evitando `NullPointerException`.

---

## Code Smells Identificados

### Anemic Domain Model

A classe atua predominantemente como estrutura de dados com getters e setters, contendo pouca lógica comportamental.

### Primitive Obsession

CPF, RG e senha são tratados como `String`, embora representem conceitos de domínio relevantes.

### Mutable Entity

Todos os atributos podem ser alterados livremente, o que pode dificultar o controle de invariantes.

---

## Sugestões de Refatoração

### Criação de Value Objects

Encapsular:

- `Cpf`
- `Rg`
- `SenhaHash`

### Métodos de Comportamento

Adicionar métodos como:

- `adicionarRendimento()`
- `removerRendimento()`
- `alterarEndereco()`

### Implementação de equals() e hashCode()

Fundamental para comparação consistente de entidades.

---

## Padrões de Projeto Aplicáveis

### Entity Pattern

A classe já representa adequadamente uma entidade de domínio.

### Aggregate Root

O `Cliente` atua como raiz do agregado que controla os `Rendimento`.

### Value Object

Campos como CPF e senha são candidatos ideais.

---

## Princípios SOLID Relacionados

### Single Responsibility Principle (SRP)

A classe modela exclusivamente o conceito de cliente.

### Encapsulation

Os atributos privados preservam o estado interno.

---

## Avaliação Final

A entidade `Cliente` apresenta excelente modelagem e forte aderência às boas práticas de JPA e domínio orientado a objetos. A implementação é consistente, segura e adequada ao papel estratégico que desempenha no sistema.