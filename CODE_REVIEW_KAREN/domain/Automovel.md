# Automovel.java

## Visão Geral da Classe

A classe `Automovel` representa os veículos disponíveis no sistema, armazenando dados cadastrais, informações de propriedade e vínculo opcional com um cliente.

Ela é uma entidade estratégica do domínio, pois participa diretamente do processo de solicitação, aprovação e transferência de titularidade.

---

## Boas Práticas Observadas

- Uso de `@Entity` e `@Table` com restrição de unicidade da placa.
- Validações declarativas com `@NotBlank`, `@NotNull`, `@Min` e `@Max`.
- Uso de enum `TipoProprietarioVeiculo`.
- Relacionamento `@ManyToOne` com carregamento `LAZY`.
- Construtores sobrecarregados para facilitar criação.

---

## Code Smells Identificados

- Primitive Obsession no atributo `placaNormalizada`.
- Entidade predominantemente anêmica.
- Possível ausência de `equals()` e `hashCode()`.

---

## Sugestões de Refatoração

- Criar Value Object `Placa`.
- Adicionar métodos de comportamento, como `transferirParaCliente()`.
- Implementar `equals()` e `hashCode()`.

---

## Padrões de Projeto Aplicáveis

- Entity Pattern
- Value Object
- State (evolução futura do proprietário)

---

## Avaliação Final

A entidade `Automovel` apresenta modelagem consistente, validações robustas e excelente aderência ao domínio do problema.