# Contrato.java

## Visão Geral da Classe

A classe `Contrato` representa o documento gerado automaticamente após a aprovação de um pedido de aluguel.

Ela consolida informações jurídicas e operacionais, mantendo vínculo exclusivo com um pedido e armazenando o texto integral dos termos contratuais.

---

## Boas Práticas Observadas

- Relacionamento `@OneToOne` com unicidade garantida.
- Uso de `@Lob` para armazenar texto extenso.
- Enum `TipoContrato`.
- `@PrePersist` para geração automática da data.

---

## Code Smells Identificados

- Primitive Obsession no número do contrato.
- Entidade anêmica.
- Texto contratual tratado como `String`.

---

## Sugestões de Refatoração

- Criar Value Object `NumeroContrato`.
- Extrair geração do texto para um componente específico.
- Implementar comportamento como `desativar()`.

---

## Padrões de Projeto Aplicáveis

- Entity Pattern
- Value Object
- Builder Pattern

---

## Avaliação Final

A entidade `Contrato` apresenta excelente modelagem, forte consistência estrutural e ótima aderência ao processo de formalização do aluguel.