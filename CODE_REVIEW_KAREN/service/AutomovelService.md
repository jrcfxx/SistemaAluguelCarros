# AutomovelService.java

## Visão Geral da Classe

A classe `AutomovelService` concentra as regras de negócio relacionadas à gestão da frota de veículos do sistema. Entre suas responsabilidades estão o cadastro, atualização, busca por identificador, listagem da frota e pesquisa no catálogo com filtros por texto, faixa de ano e critérios de ordenação.

Do ponto de vista arquitetural, esta classe representa um serviço de domínio altamente relevante, pois encapsula todas as regras associadas à entidade `Automovel`, incluindo validações de placa, ano, marca, modelo e URL de fotografia. A implementação demonstra clara separação entre regras de negócio e persistência, delegando ao `AutomovelRepository` a responsabilidade pelo acesso aos dados.

---

## Boas Práticas Observadas

### Centralização das Regras de Negócio

Todas as operações relacionadas à frota encontram-se concentradas em um único serviço, o que garante consistência e facilita a manutenção.

### Validações Centralizadas

A classe utiliza intensivamente `ValidationRules`, evitando duplicação de lógica e assegurando padronização das verificações.

### Normalização de Dados

Campos como placa, marca, modelo e URL são normalizados antes da persistência, aumentando a qualidade e consistência dos dados.

### Verificação de Unicidade

A validação de placa duplicada previne inconsistências no cadastro e reforça a integridade do banco de dados.

### Uso de Optional

Métodos como `buscarPorId()` utilizam `Optional`, tornando o tratamento da ausência de registros mais seguro e explícito.

### Uso de @Transactional

O método `atualizar()` é anotado com `@Transactional`, garantindo atomicidade da operação.

### Uso de Streams e Comparator

O método `buscarCatalogo()` emprega `Stream`, filtros e `Comparator`, demonstrando bom domínio da API funcional do Java.

### Responsabilidade Bem Delimitada

A classe mantém foco exclusivo na gestão dos automóveis, apresentando alta coesão.

---

## Code Smells Identificados

### Long Method

O método `buscarCatalogo()` possui diversas responsabilidades: filtragem, ordenação e normalização de parâmetros. Embora bem organizado, tende a crescer com a inclusão de novos critérios.

### Duplicated Code

Os métodos `cadastrar()` e `atualizar()` repetem várias etapas de validação e normalização, como:

- normalização da placa;
- validação de ano;
- validação de marca e modelo;
- validação de URL da foto.

### Primitive Obsession

A placa é tratada como `String`, embora represente um conceito importante do domínio e possa ser modelada como um objeto de valor.

### Conditional Complexity

A lógica de ordenação utiliza múltiplos `if/else`, o que pode dificultar expansão futura.

---

## Sugestões de Refatoração

### Extração de Métodos Privados

Criar métodos auxiliares como:

- `validarDadosAutomovel()`
- `normalizarCampos()`
- `verificarPlacaDuplicada()`

reduziria duplicação e aumentaria legibilidade.

### Criação de Value Object `Placa`

Encapsular regras de normalização e validação da placa em um objeto dedicado.

### Strategy Pattern para Ordenação

Cada critério de ordenação (`ano_desc`, `ano_asc`, `marca`) pode ser implementado como uma estratégia independente.

### DTO para Formulários

A criação de um `AutomovelFormDTO` pode desacoplar os parâmetros HTTP da entidade de domínio.

### Extração de `CatalogoAutomovelService`

Caso a busca do catálogo cresça, a funcionalidade pode ser isolada em um serviço específico.

---

## Padrões de Projeto Aplicáveis

### Service Layer

A classe é um excelente exemplo do padrão Service Layer, centralizando regras de negócio e oferecendo uma interface clara para os controllers.

### Repository Pattern

A persistência é abstraída por meio do `AutomovelRepository`.

### Value Object

A placa do veículo é um forte candidato a objeto de valor.

### Strategy Pattern

Os diferentes algoritmos de ordenação podem ser encapsulados em estratégias independentes.

### Factory Method

A criação do objeto `Automovel` pode ser centralizada em um método de fábrica.

### Specification Pattern

Filtros do catálogo podem ser modelados como especificações reutilizáveis.

---

## Princípios SOLID Relacionados

### Single Responsibility Principle (SRP)

A classe possui foco claro na gestão da frota, embora o catálogo possa futuramente ser extraído para um componente próprio.

### Open/Closed Principle (OCP)

Com Strategy e Specification, novos critérios de busca e ordenação podem ser adicionados sem modificar a lógica existente.

### Dependency Inversion Principle (DIP)

O serviço depende do repositório e da camada de validação, preservando baixo acoplamento.

### DRY (Don't Repeat Yourself)

A extração de métodos auxiliares eliminaria duplicação entre cadastro e atualização.

---

## Impacto Arquitetural das Melhorias

As melhorias sugeridas proporcionariam:

- redução de duplicação;
- maior reutilização;
- melhor encapsulamento;
- maior extensibilidade;
- simplificação da manutenção;
- melhor aderência ao SOLID.

---

## Avaliação Final

O `AutomovelService` apresenta excelente organização, forte encapsulamento das regras de negócio e uso consistente de validações e normalização de dados. O método `buscarCatalogo()` demonstra domínio avançado da API funcional do Java, enquanto os métodos de cadastro e atualização evidenciam preocupação com integridade e consistência.

Trata-se de uma implementação robusta, bem estruturada e tecnicamente madura, com oportunidades claras de evolução por meio de padrões como Value Object, Strategy e Specification.