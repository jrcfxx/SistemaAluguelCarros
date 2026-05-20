# Rendimento.java

## Visão Geral da Classe

A classe `Rendimento` representa uma fonte de renda declarada pelo cliente, contendo vínculo com um empregador e valor mensal.

Essas informações são essenciais para a análise financeira dos pedidos de aluguel.

---

## Boas Práticas Observadas

- Modelagem explícita da composição com `Empregador`.
- Uso de `BigDecimal` para valores monetários.
- Relacionamento com `Cliente`.
- Separação adequada de conceitos financeiros.

---

## Code Smells Identificados

- Entidade anêmica.
- Primitive Obsession em campos monetários e documentais.
- Possível ausência de comportamento próprio.

---

## Sugestões de Refatoração

- Criar Value Objects para CNPJ e valor monetário.
- Adicionar métodos de atualização controlada.
- Implementar `equals()` e `hashCode()`.

---

## Padrões de Projeto Aplicáveis

- Entity Pattern
- Value Object
- Composite (conceitualmente com `Empregador`)

---

## Avaliação Final

A entidade `Rendimento` apresenta modelagem clara, boa representação financeira e forte alinhamento com os requisitos de análise de crédito do sistema.