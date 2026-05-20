# ValidationRules.java

## Visão Geral da Classe

A classe `ValidationRules` desempenha um papel central na arquitetura do sistema, concentrando todas as regras de validação utilizadas pelas demais camadas da aplicação. Sua principal responsabilidade é garantir que os dados recebidos estejam em conformidade com os critérios de integridade antes de serem persistidos ou processados pelas regras de negócio.

Do ponto de vista arquitetural, essa abordagem é extremamente positiva, pois evita duplicação de código, promove reutilização e assegura consistência entre os diversos módulos do sistema. A centralização das validações também facilita manutenção, testes e evolução do software.

---

## Boas Práticas Observadas

### Centralização das Regras de Validação

Todas as verificações de integridade encontram-se em um único componente reutilizável. Essa decisão reduz redundância e garante que diferentes partes do sistema utilizem exatamente os mesmos critérios de validação.

### Uso de Constantes

A definição de constantes como `NOME_MIN_LENGTH`, `CPF_PATTERN` e `PLACA_NORMALIZADA_LENGTH` elimina números mágicos e expressões repetidas, tornando o código mais legível e fácil de manter.

### Encapsulamento de Algoritmos Complexos

A implementação do algoritmo de validação de CPF demonstra atenção à robustez e à confiabilidade dos dados, encapsulando uma lógica não trivial em métodos especializados.

### Uso de Expressões Regulares

A utilização de objetos `Pattern` pré-compilados é uma decisão técnica adequada, pois melhora desempenho e padroniza as validações de campos textuais.

### Classe Utilitária Final

A declaração `public final class ValidationRules` indica que a classe não deve ser estendida, preservando seu propósito exclusivamente utilitário e evitando heranças indevidas.

---

## Code Smells Identificados

### Large Class (Classe Muito Grande)

A classe concentra validações para múltiplos domínios distintos, incluindo:

- CPF
- CNPJ
- Nome
- Senha
- URL
- Placa
- Ano do veículo
- Valores monetários
- Descrição de pedidos

Embora a centralização seja benéfica, o crescimento excessivo pode dificultar leitura, manutenção e testes.

### Divergent Change

Alterações em diferentes regras de negócio podem impactar simultaneamente esta classe. Por exemplo, mudanças na validação de placas ou URLs exigem modificações no mesmo arquivo, aumentando o risco de regressões.

### Low Cohesion (Coesão Reduzida)

Apesar de todas as funções serem validações, elas pertencem a contextos distintos (cliente, automóvel, pedido e autenticação). Isso indica que a responsabilidade está conceitualmente ampla demais.

### God Utility Class

A classe tende a se tornar um "repositório universal" de validações. Esse tipo de utilitário pode crescer indefinidamente e se tornar difícil de manter, configurando um smell típico de classes utilitárias excessivamente abrangentes.

---

## Sugestões de Refatoração

### Separação por Contexto de Negócio

Uma melhoria importante seria dividir a classe em componentes especializados:

- `DocumentoValidator`
- `ClienteValidator`
- `VeiculoValidator`
- `PedidoValidator`
- `AuthValidator`

Essa decomposição aumentaria coesão e reduziria complexidade.

### Extração de Componentes Independentes

O algoritmo de CPF e CNPJ pode ser isolado em classes próprias, facilitando testes unitários e reutilização em outros projetos.

### Uso de Bean Validation

A adoção de anotações como `@NotBlank`, `@Size`, `@Pattern` e `@Email` reduziria código manual e integraria as validações ao ecossistema do Micronaut.

### Criação de Exceções Específicas

Métodos podem lançar exceções semânticas como:

- `CpfInvalidoException`
- `PlacaInvalidaException`
- `SenhaInvalidaException`

Isso melhora rastreabilidade e clareza no tratamento de erros.

### Aplicação de Testes Parametrizados

Validações baseadas em regex e algoritmos matemáticos podem ser testadas com maior abrangência por meio de testes parametrizados, cobrindo cenários positivos e negativos.

---

## Padrões de Projeto Aplicáveis

### Strategy Pattern

Cada algoritmo de validação pode ser encapsulado em uma estratégia independente, implementando uma interface comum:

```java
public class CpfValidator implements Validator<String> {
    @Override
    public void validate(String cpf) {
        // lógica de validação
    }
}