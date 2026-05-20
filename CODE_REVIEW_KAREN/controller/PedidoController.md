# PedidoController.java

## Visão Geral da Classe

A classe `PedidoController` é responsável por gerenciar todas as requisições HTTP relacionadas ao ciclo de vida dos pedidos de aluguel. Entre suas atribuições estão a exibição da listagem de pedidos do cliente autenticado, a criação de novos pedidos, a edição de pedidos pendentes, o cancelamento e a visualização do contrato associado ao pedido aprovado.

No contexto da arquitetura MVC, esta classe desempenha adequadamente o papel de Controller, atuando como intermediária entre a interface web (Thymeleaf) e a camada de serviços. Sua responsabilidade está restrita ao tratamento de parâmetros, validações iniciais, controle de sessão e redirecionamento para as views apropriadas.

---

## Boas Práticas Observadas

### Separação Clara de Responsabilidades

O controller delega toda a lógica de negócio ao `PedidoAluguelService`, mantendo-se enxuto e alinhado ao princípio de separação de responsabilidades.

### Controle de Autenticação e Autorização

A utilização do `SessionAuthService` garante que o cliente só possa acessar e manipular seus próprios pedidos, reforçando a segurança da aplicação.

### Uso do Padrão Post/Redirect/Get

Após operações `POST`, o controller utiliza `HttpResponse.seeOther()`, evitando reenvio acidental de formulários e melhorando a experiência do usuário.

### Tratamento Consistente de Exceções

Erros de negócio são capturados e convertidos em mensagens amigáveis, facilitando a interação com o usuário.

### Integração com Thymeleaf

A construção de `ModelAndView` com mapas organizados demonstra boa prática na preparação dos dados para a camada de apresentação.

---

## Code Smells Identificados

### Repetição de Verificações de Sessão

Diversos métodos repetem a validação de autenticação e autorização, o que pode gerar duplicação de código.

### Long Methods

Métodos responsáveis por montar modelos ou tratar diferentes cenários podem crescer e comprometer a legibilidade.

### Primitive Obsession

Parâmetros como `Long`, `String` e `Integer` podem futuramente ser encapsulados em DTOs específicos.

---

## Sugestões de Refatoração

### Extração de Métodos Auxiliares

Criar métodos como:

- `obterClienteAutenticado()`
- `validarAcessoAoPedido()`
- `montarFormularioPedido()`

reduz duplicação e melhora a legibilidade.

### Uso de DTOs

Introduzir `PedidoFormDTO` para desacoplar os parâmetros HTTP da entidade de domínio.

### Aplicação de Interceptors ou Filters

A autenticação pode ser tratada automaticamente por componentes do Micronaut, reduzindo código repetitivo.

---

## Padrões de Projeto Aplicáveis

### Front Controller

O Micronaut já implementa esse padrão, centralizando o roteamento das requisições.

### Data Transfer Object (DTO)

Formulários podem ser representados por objetos específicos de transferência de dados.

### Template Method

Métodos auxiliares podem padronizar o fluxo de autenticação, validação e montagem da resposta.

### Facade

O `PedidoAluguelService` já atua como fachada para operações complexas do domínio.

---

## Princípios SOLID Relacionados

### Single Responsibility Principle (SRP)

A classe está corretamente focada na camada de apresentação.

### Dependency Inversion Principle (DIP)

O controller depende de services injetados, e não de implementações diretas de persistência.

---

## Impacto Arquitetural das Melhorias

As melhorias sugeridas resultariam em:

- menor duplicação de código;
- maior legibilidade;
- melhor testabilidade;
- maior desacoplamento;
- simplificação da manutenção.

---

## Avaliação Final

O `PedidoController` apresenta excelente aderência ao padrão MVC e demonstra clara separação entre apresentação e regras de negócio. A implementação é organizada, segura e alinhada às boas práticas de desenvolvimento web com Micronaut e Thymeleaf.

Trata-se de um controller robusto e bem estruturado, que evidencia domínio consistente de arquitetura em camadas, autenticação e fluxo HTTP.