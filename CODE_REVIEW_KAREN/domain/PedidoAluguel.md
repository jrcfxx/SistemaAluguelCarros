# PedidoAluguel.java

## Visão Geral da Classe

A classe `PedidoAluguel` representa o principal processo de negócio do sistema, registrando a solicitação de aluguel de um veículo realizada por um cliente.

Ela conecta cliente, automóvel, status e contrato, funcionando como núcleo do fluxo operacional da aplicação.

---

## Boas Práticas Observadas

- Uso de `@PrePersist` e `@PreUpdate`.
- Relacionamentos bem definidos com `Cliente`, `Automovel` e `Contrato`.
- Uso de enum `StatusPedido`.
- Controle automático de timestamps.

---

## Code Smells Identificados

- Anemic Domain Model.
- Primitive Obsession em campos textuais.
- Regras de transição de estado concentradas no service.

---

## Sugestões de Refatoração

- Criar métodos de comportamento como `aprovar()`, `reprovar()` e `cancelar()`.
- Aplicar State Pattern para o ciclo de vida do pedido.
- Implementar `equals()` e `hashCode()`.

---

## Padrões de Projeto Aplicáveis

- Entity Pattern
- State Pattern
- Aggregate Root

---

## Avaliação Final

O `PedidoAluguel` é uma entidade central extremamente bem modelada, com excelente representação do fluxo de negócio e forte aderência ao domínio.