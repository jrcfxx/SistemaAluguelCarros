# PropriedadeVeiculoService.java

## Visão Geral da Classe

A classe `PropriedadeVeiculoService` é responsável por aplicar as regras de negócio que determinam a titularidade do automóvel após a geração de um contrato. O comportamento varia de acordo com o `TipoContrato`, alterando o proprietário para locadora, cliente ou banco.

A classe representa um excelente exemplo de serviço de domínio, pois encapsula uma regra específica e altamente coesa do sistema.

---

## Boas Práticas Observadas

### Alta Coesão

A classe possui uma responsabilidade única e claramente definida.

### Uso de Enum para Decisão

O `switch` sobre `TipoContrato` torna o código explícito e fácil de compreender.

### Métodos Privados Especializados

Cada regra de titularidade foi extraída para métodos específicos.

### Uso de @Transactional

Garante persistência consistente da alteração do automóvel.

---

## Code Smells Identificados

### Conditional Complexity

O `switch` tende a crescer caso novos tipos de contrato sejam adicionados.

### Open/Closed Principle Violation

A inclusão de novos contratos exige modificação do método principal.

---

## Sugestões de Refatoração

### Strategy Pattern

Criar uma estratégia específica para cada tipo de contrato:

- `LocacaoSimplesStrategy`
- `OpcaoCompraStrategy`
- `CreditoBancarioStrategy`

### Factory Method

Uma fábrica pode selecionar automaticamente a estratégia adequada.

---

## Padrões de Projeto Aplicáveis

### Strategy Pattern

Encapsula cada regra de alteração de propriedade em classes independentes.

### Factory Method

Seleciona dinamicamente a estratégia apropriada.

### Domain Service

A classe já é um excelente exemplo desse padrão.

---

## Princípios SOLID Relacionados

### Single Responsibility Principle (SRP)

A classe possui foco extremamente bem definido.

### Open/Closed Principle (OCP)

O uso de Strategy permitiria expansão sem modificações no código existente.

---

## Impacto Arquitetural das Melhorias

As melhorias proporcionariam:

- maior extensibilidade;
- eliminação de condicionais;
- melhor aderência ao OCP;
- maior testabilidade.

---

## Avaliação Final

O `PropriedadeVeiculoService` é um dos melhores exemplos de modelagem de regras de negócio do projeto. A classe apresenta altíssima coesão, excelente clareza e forte aderência aos princípios de Engenharia de Software.