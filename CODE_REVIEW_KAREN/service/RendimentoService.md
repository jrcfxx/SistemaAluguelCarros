# RendimentoService.java

## Visão Geral da Classe

A classe `RendimentoService` é responsável por gerenciar os rendimentos declarados pelos clientes, permitindo cadastro, listagem, contagem e exclusão dessas informações. Sua principal função é garantir que os dados financeiros utilizados na análise de crédito sejam consistentes, válidos e respeitem as restrições definidas pelo sistema.

Do ponto de vista arquitetural, esta classe representa um importante serviço de domínio, pois encapsula regras de negócio relacionadas à capacidade financeira do cliente. Essas informações são fundamentais para a aprovação ou reprovação de pedidos de aluguel, tornando esta classe estratégica para o funcionamento do sistema.

---

## Boas Práticas Observadas

### Centralização das Regras de Negócio

Todas as operações relacionadas aos rendimentos foram agrupadas em um único serviço, promovendo alta coesão e facilitando manutenção.

### Validação Completa dos Dados

Antes de persistir um rendimento, a classe valida:

- nome do empregador;
- CNPJ opcional;
- valor mensal;
- limite máximo de rendimentos por cliente.

Essa abordagem garante integridade e consistência dos dados.

### Reutilização de ValidationRules

A utilização da classe `ValidationRules` evita duplicação de código e padroniza as validações em toda a aplicação.

### Verificação de Existência do Cliente

O método `adicionar()` utiliza `ClienteService.buscarPorId()`, garantindo que apenas clientes válidos possam registrar rendimentos.

### Composição entre Entidades

A criação explícita do objeto `Empregador` demonstra modelagem orientada a objetos adequada, preservando o relacionamento entre entidades.

### Encapsulamento de Regras Financeiras

A restrição `MAX_RENDIMENTOS_POR_CLIENTE` é aplicada diretamente na camada de serviço, reforçando o controle do domínio.

### Métodos Pequenos e Coesos

Cada método possui responsabilidade clara e bem delimitada.

---

## Code Smells Identificados

### Long Method

O método `adicionar()` concentra validação, busca de cliente, criação de objetos e persistência. Embora organizado, pode ser dividido para aumentar legibilidade.

### Duplicated Exception Handling

O padrão:

```java
ValidationRules.validarX(...).ifPresent(msg -> {
    throw new IllegalStateException(msg);
});