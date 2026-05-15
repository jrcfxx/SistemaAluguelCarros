# Code Review - Sistema Aluguel de Carros

## Objetivo

Este Pull Request tem como objetivo realizar uma revisão técnica do projeto, conforme solicitado na atividade de Code Review.

A revisão técnica foi organizada no arquivo `docs/CODE_REVIEW_ERICK.md`, conforme orientação recebida.

Os comentários foram separados por arquivo analisado e incluem pontos de arquitetura, padrões de projeto, refatorações, code smells, boas práticas, validações, segurança e testabilidade.

Entendo que, em um fluxo profissional de mercado, o ideal seria realizar comentários inline diretamente no Pull Request pela ferramenta de revisão do GitHub. Para esta atividade, mantive a revisão em Markdown para facilitar a organização e leitura.

## Pontos analisados

- Arquitetura geral do sistema
- Separação de responsabilidades
- Organização das classes
- Code smells
- Boas práticas de programação
- Possíveis padrões de projeto aplicáveis
- Refatorações sugeridas
- Manutenibilidade e testabilidade

## Observação

Este Pull Request deve permanecer aberto para avaliação do professor.

Professor: @joaopauloaramuni

## Comentários de Code Review - PedidoAluguelService.java

### Comentário 01 - Separação de responsabilidades

O `PedidoAluguelService` concentra várias responsabilidades: criação de pedido, atualização, cancelamento, aprovação, reprovação, listagem para análise e integração com contrato. Embora funcione, isso pode fazer a classe crescer bastante conforme novas regras sejam adicionadas.

Sugestão: separar parte do fluxo de aprovação/reprovação em um serviço específico, como `AnalisePedidoService` ou `PedidoWorkflowService`.

Benefício: melhora a coesão da classe, facilita testes unitários e deixa cada serviço com uma responsabilidade mais clara.

---

### Comentário 02 - Dependência direta do AutomovelRepository

No método `criarPedido`, o serviço acessa diretamente o `AutomovelRepository` para buscar o automóvel. Como já existe `AutomovelService` no projeto, seria interessante avaliar se essa busca deveria passar por ele.

Sugestão: substituir a dependência direta de `AutomovelRepository` por `AutomovelService`.

Benefício: centraliza regras relacionadas ao automóvel em uma única camada de serviço, evitando que validações futuras sobre disponibilidade, status ou propriedade do veículo fiquem espalhadas.

---

### Comentário 03 - Regra de pedido PENDENTE repetida

A validação de que apenas pedidos `PENDENTE` podem ser editados, cancelados, aprovados ou reprovados aparece em vários métodos do `PedidoAluguelService`.

Uma melhoria seria extrair essa regra para um método privado, como `validarPedidoPendente(...)`. Isso reduziria repetição e deixaria a regra mais fácil de alterar no futuro.

### Comentário 04 - Atualização de data em alterações

No método `criarPedido`, a data de solicitação e a última atualização são preenchidas. Porém, nos métodos `atualizarPedido`, `cancelarPedido`, `aprovarPedido` e `reprovarPedido`, a propriedade `ultimaAtualizacao` não parece ser atualizada.

Sugestão: atualizar `ultimaAtualizacao` sempre que houver mudança relevante no pedido.

Benefício: melhora a rastreabilidade do fluxo do pedido e permite que a interface ou relatórios exibam informações mais confiáveis sobre a última alteração.

---

### Comentário 05 - Exceções genéricas de regra de negócio

O serviço utiliza `IllegalStateException` para representar várias situações de regra de negócio, como cliente não encontrado, automóvel não encontrado, pedido não encontrado ou status inválido.

Sugestão: criar exceções específicas de domínio, como `PedidoNaoEncontradoException`, `RegraNegocioException` ou `PedidoStatusInvalidoException`.

Benefício: facilita o tratamento de erro nos controllers, melhora a clareza do código e permite mensagens/respostas mais adequadas ao usuário.

---

### Comentário 06 - Aprovação gera contrato dentro do mesmo serviço

O método `aprovarPedido` altera o status do pedido, atualiza o repositório, chama `ContratoService` e associa o contrato ao pedido. Essa lógica é importante e envolve uma transição de estado do domínio.

Sugestão: considerar um serviço específico para orquestrar esse fluxo, como `AprovacaoPedidoService`, mantendo o `PedidoAluguelService` mais focado nas operações principais de pedido.

Benefício: deixa o fluxo de aprovação mais explícito, facilita testes da regra de aprovação e reduz o acoplamento entre pedido e contrato.

---

### Comentário 07 - Tipo de contrato padrão definido de forma silenciosa

No método `aprovarPedido`, quando `tipoContrato` vem nulo, o sistema assume `TipoContrato.LOCACAO_SIMPLES`. A regra faz sentido, mas está embutida diretamente no método.

Sugestão: centralizar essa decisão no `TipoContratoResolver`, que já existe no projeto, ou documentar claramente essa regra como padrão oficial do domínio.

Benefício: evita regra escondida dentro do fluxo de aprovação e facilita alteração futura do comportamento padrão.

---

### Comentário 08 - Normalização da descrição

O método `normalizarDescricao` apenas trata `null` e aplica `trim`. Como a descrição também passa por validação em `ValidationRules`, pode ser interessante concentrar normalização e validação em uma camada própria ou em um objeto de formulário/DTO.

Sugestão: criar um DTO/Form para criação e edição de pedido, deixando o service receber dados já normalizados ou centralizando essa transformação em uma classe específica.

Benefício: melhora a separação entre entrada de dados e regra de negócio, além de facilitar testes das validações.

## Comentários de Code Review - ContratoService.java

### Comentário 09 - Serviço com mais de uma responsabilidade

O `ContratoService` atualmente cria o contrato, gera o número do contrato, monta os termos textuais e ainda aciona a regra de propriedade do veículo.

Sugestão: separar a montagem textual do contrato em uma classe própria, como `ContratoTemplateService` ou `ContratoFormatter`.

Benefício: o `ContratoService` ficaria mais focado na regra de negócio principal, enquanto a geração do conteúdo textual do contrato ficaria isolada e mais fácil de manter.

---

### Comentário 10 - Geração de número de contrato

O método `gerarNumeroContrato` usa o ID do pedido junto com data e hora até segundos para formar o número do contrato.

Sugestão: avaliar se esse formato garante unicidade em todos os cenários ou se seria melhor centralizar essa regra em um componente específico, como `NumeroContratoGenerator`.

Benefício: facilita alterar futuramente o padrão de numeração sem mexer diretamente no serviço de contrato.

---

### Comentário 11 - Texto do contrato hardcoded no service

O método `montarTermosAcademicos` possui um texto grande fixo dentro da classe de serviço.

Sugestão: mover esse texto para um template externo, arquivo de recurso ou classe especializada em geração de documentos.

Benefício: melhora a legibilidade do serviço, facilita manutenção do texto e permite futuras variações de contrato sem alterar regra de negócio.

---

### Comentário 12 - Possível uso de Strategy para tipo de contrato

O contrato possui diferentes tipos, como locação simples, locação com opção de compra e crédito bancário. Caso cada tipo passe a ter regras próprias de texto, propriedade ou validação, o uso de condicionais pode crescer bastante.

Sugestão: considerar o padrão Strategy para representar o comportamento de cada `TipoContrato`.

Benefício: cada tipo de contrato teria sua própria regra isolada, facilitando manutenção e extensão sem modificar diretamente o `ContratoService`.

---

### Comentário 13 - Acoplamento entre contrato e propriedade do veículo

Após salvar o contrato, o serviço chama `propriedadeVeiculoService.aplicarPropriedadeAposContrato(salvo)`. A regra funciona, mas cria um acoplamento direto entre geração de contrato e alteração de titularidade do veículo.

Sugestão: avaliar se essa ação poderia ser tratada por um serviço de workflow, como `FormalizacaoContratoService`, responsável por orquestrar contrato + propriedade.

Benefício: deixa mais claro que a geração do contrato e a atualização da titularidade fazem parte de um fluxo maior de formalização.

---

### Comentário 14 - Validação do status do pedido aprovado

O método `criarContratoParaPedidoAprovado` recebe um pedido que, pelo nome, já deveria estar aprovado. Porém, dentro do método não há uma validação explícita verificando se o status realmente é `APROVADO`.

Sugestão: validar o status do pedido antes de gerar o contrato.

Benefício: evita geração indevida de contrato caso o método seja chamado por engano com um pedido em outro status.

---

### Comentário 15 - Tratamento de dados nulos no contrato

O método `montarTermosAcademicos` trata nome, CPF e veículo com valores padrão quando estão nulos. Isso evita erro de execução, mas pode mascarar inconsistências importantes no domínio.

Sugestão: avaliar quais dados são obrigatórios para gerar contrato e validar esses campos antes da geração.

Benefício: garante que contratos sejam criados apenas com informações mínimas confiáveis e evita documentos incompletos.

## Comentários de Code Review - PropriedadeVeiculoService.java

### Comentário 16 - Boa separação da regra de titularidade

A criação do `PropriedadeVeiculoService` foi uma boa decisão, porque a regra de titularidade do automóvel ficou separada do `ContratoService`.

Esse desenho melhora a organização do domínio. Como evolução futura, se surgirem novos tipos de contrato, essa lógica poderia ser migrada para estratégias específicas por tipo de contrato.

### Comentário 17 - Possível aplicação do padrão Strategy

O método `aplicarPropriedadeAposContrato` usa um `switch` baseado no `TipoContrato`.

Atualmente está simples e legível, mas se novos tipos de contrato forem adicionados, esse `switch` pode crescer e dificultar a manutenção.

Sugestão: considerar o padrão Strategy, criando uma estratégia para cada tipo de contrato, por exemplo:

`LocacaoSimplesStrategy`, `OpcaoCompraStrategy` e `CreditoBancarioStrategy`.

Benefício: cada regra de titularidade ficaria isolada em uma classe própria, respeitando melhor o princípio Aberto/Fechado.

---

### Comentário 18 - Validação de tipo de contrato nulo

O método usa `contrato.getTipoContrato()` e aplica o `switch` diretamente. Caso o tipo de contrato esteja nulo, pode ocorrer erro em tempo de execução.

Sugestão: validar explicitamente se `tipo` é nulo antes do `switch`.

Benefício: gera uma mensagem de erro mais clara e facilita a identificação do problema durante manutenção.

---

### Comentário 19 - Regra de negócio acoplada ao enum

A lógica de titularidade está diretamente ligada aos valores do enum `TipoContrato`.

Sugestão: avaliar se o próprio enum poderia conhecer parte do seu comportamento ou se cada tipo deveria ser tratado por uma classe especializada.

Benefício: reduz a dependência de condicionais e facilita a evolução quando novos tipos de contrato forem criados.

---

### Comentário 20 - Ausência de histórico de propriedade

O comentário da classe informa que a atualização ocorre sem histórico, mantendo apenas o estado atual da titularidade.

Sugestão: caso o sistema evolua, considerar uma entidade como `HistoricoPropriedadeVeiculo` ou `TransferenciaPropriedade`.

Benefício: permitiria auditoria das mudanças de titularidade, principalmente em contratos com opção de compra ou crédito bancário.

---

### Comentário 21 - Métodos privados bem separados

Os métodos `aplicarLocacaoSimples`, `aplicarOpcaoCompra` e `aplicarCreditoBancario` deixam a lógica mais legível do que colocar tudo diretamente dentro do `switch`.

Ponto positivo: essa separação facilita leitura, teste e manutenção.

Sugestão: manter esse padrão e, se as regras crescerem, transformar esses métodos em estratégias/classes próprias.

---

### Comentário 22 - Transação em atualização de propriedade

O uso de `@Transactional` no método `aplicarPropriedadeAposContrato` é adequado, pois a operação altera o estado do automóvel após a geração do contrato.

Sugestão: garantir que esse método continue sendo chamado dentro de um fluxo transacional maior quando contrato e propriedade forem alterados em conjunto.

Benefício: evita inconsistência, como contrato criado sem atualização correta da titularidade do veículo.

## Comentários de Code Review - PedidoController.java

### Comentário 23 - Controller com muitas responsabilidades

O `PedidoController` está responsável por autenticar sessão, buscar dados, tratar erros, montar models para as views, redirecionar e controlar o fluxo de criação, edição, cancelamento e visualização de contrato.

Sugestão: manter o controller mais focado apenas no recebimento da requisição e delegar a montagem de models ou regras auxiliares para classes específicas, como um `PedidoViewModelFactory`.

Benefício: o controller fica menor, mais legível e mais fácil de testar.

---

### Comentário 24 - Verificação de autenticação repetida

A verificação `sessionAuthService.clienteAutenticado(session)` aparece repetida em vários métodos do controller.

Sugestão: avaliar a criação de um mecanismo centralizado de autenticação/autorização, como filtro, interceptor ou helper específico para controllers autenticados.

Benefício: reduz repetição e evita inconsistências caso a regra de autenticação mude no futuro.

---

### Comentário 25 - Montagem manual de Map para views

O controller monta manualmente vários `Map<String, Object>` para enviar dados ao Thymeleaf.

Sugestão: criar objetos específicos de view model, como `PedidoListaViewModel`, `PedidoFormularioViewModel` ou uma factory para montagem desses modelos.

Benefício: melhora a organização, reduz risco de erro em nomes de atributos e facilita reutilização entre telas.

---

### Comentário 26 - Tratamento de erro baseado em texto da exceção

No método `salvarEdicao`, há uma comparação direta com a mensagem `"Pedido não encontrado."`.

Sugestão: evitar tomar decisões baseadas no texto da exceção. O ideal seria usar exceções específicas, como `PedidoNaoEncontradoException` ou `RegraNegocioException`.

Benefício: deixa o código mais seguro, pois mudanças no texto da mensagem não quebrariam a lógica do controller.

---

### Comentário 27 - Redirecionamentos duplicados

Existem métodos separados para redirecionar com mensagem e erro, tanto com `redirect` quanto com `seeOther`.

Sugestão: criar um método mais genérico para redirecionamento, recebendo o tipo de parâmetro e o status HTTP desejado.

Benefício: reduz duplicação e deixa o padrão Post/Redirect/Get mais consistente.

---

### Comentário 28 - Regra de status no controller

No método de edição, o controller verifica diretamente se o pedido está com status `PENDENTE`.

Sugestão: essa regra poderia ficar exclusivamente no `PedidoAluguelService`, deixando o controller apenas reagir ao resultado da operação.

Benefício: concentra a regra de negócio na camada de serviço e evita duplicação entre controller e service.

---

### Comentário 29 - Uso de strings fixas para nomes de views e URLs

O controller usa várias strings fixas, como `"pedidos/lista"`, `"pedidos/formulario"`, `"/pedidos"` e `"/clientes"`.

Sugestão: centralizar nomes de views e rotas constantes em uma classe de constantes ou em métodos auxiliares.

Benefício: reduz erro de digitação e facilita manutenção caso uma rota ou view seja renomeada.

---

### Comentário 30 - Método de visualização de contrato dentro do PedidoController

O método `visualizarContrato` está dentro do `PedidoController`, mas trata uma tela de contrato e usa diretamente o `ContratoService`.

Sugestão: avaliar se a visualização de contrato deveria estar em um `ContratoController`, mantendo o `PedidoController` focado apenas no ciclo de vida dos pedidos.

Benefício: melhora a separação por responsabilidade e deixa os controllers mais alinhados aos módulos do domínio.

## Comentários de Code Review - AgenteController.java

### Comentário 31 - Controller concentrando muitos fluxos do agente

O `AgenteController` concentra login, logout, listagem de pedidos, detalhamento, aprovação, reprovação e visualização de contrato.

Sugestão: separar em controllers menores, como `AgenteAuthController`, `AgentePedidoController` e `AgenteContratoController`.

Benefício: melhora a separação de responsabilidades e facilita manutenção conforme a área do agente crescer.

---

### Comentário 32 - Verificação de sessão repetida

A verificação `agenteSessionService.agenteAutenticado(session)` aparece em vários métodos.

Sugestão: centralizar essa verificação em um filtro/interceptor de autenticação para rotas `/agente/**`.

Benefício: reduz repetição no controller e garante proteção uniforme para todas as rotas do agente.

---

### Comentário 33 - Montagem manual de dados do painel

No método `listarPedidos`, o controller monta manualmente totais de pedidos pendentes, aprovados, reprovados e cancelados.

Sugestão: criar um objeto como `PainelAgenteResumoDTO` ou `AgenteDashboardViewModel`.

Benefício: deixa o controller mais limpo e centraliza a estrutura de dados usada pela tela do agente.

---

### Comentário 34 - Aprovação e reprovação com fluxo muito parecido

Os métodos `aprovarPedido` e `reprovarPedido` têm estrutura semelhante: validam sessão, chamam service, montam URI com mensagem ou erro e retornam `seeOther`.

Sugestão: extrair a montagem de redirecionamento com mensagem/erro para métodos auxiliares.

Benefício: reduz duplicação e deixa os métodos principais mais objetivos.

---

### Comentário 35 - Contrato dentro do AgenteController

O método `visualizarContrato` trata uma funcionalidade de contrato dentro do controller de agente.

Sugestão: avaliar a criação de um `AgenteContratoController` ou `ContratoController` com controle de acesso específico por tipo de usuário.

Benefício: melhora a organização por módulo e evita que o `AgenteController` vire um controller genérico demais.

---

### Comentário 36 - Uso de string fixa para rotas e views

O controller possui várias strings fixas, como `"/agente/login"`, `"/agente/pedidos"`, `"agente/pedidos/lista"` e `"contrato/visualizar"`.

Sugestão: centralizar rotas e nomes de views em constantes.

Benefício: reduz risco de erro de digitação e facilita renomeações futuras.

## Comentários de Code Review - ValidationRules.java

### Comentário 37 - Classe de validação muito centralizada

A classe `ValidationRules` concentra validações de vários domínios diferentes: cliente, senha, CPF, CNPJ, rendimento, placa, automóvel, foto e descrição de pedido.

Sugestão: separar em classes menores, como `ClienteValidator`, `DocumentoValidator`, `AutomovelValidator`, `PedidoValidator` e `RendimentoValidator`.

Benefício: melhora a coesão, facilita testes unitários e evita que uma única classe cresça demais conforme o sistema evoluir.

---

### Comentário 38 - Uso de métodos estáticos

A classe usa apenas métodos estáticos. Isso funciona para validações simples, mas pode dificultar testes com mocks, injeção de dependência e evolução para regras configuráveis.

Sugestão: avaliar transformar parte das validações em serviços injetáveis ou validators específicos.

Benefício: permite maior flexibilidade caso algumas regras passem a depender de configuração, banco de dados ou contexto do usuário.

---

### Comentário 39 - Ponto positivo: constantes bem definidas

As constantes de tamanho mínimo e máximo, como `NOME_MIN_LENGTH`, `SENHA_MIN_LENGTH`, `DESCRICAO_PEDIDO_MAX_LENGTH` e outras, deixam as regras mais explícitas.

Ponto positivo: isso evita números mágicos espalhados pelo código e facilita manutenção.

Sugestão: manter esse padrão e, se necessário, documentar quais regras vieram do enunciado e quais foram decisões da equipe.

---

### Comentário 40 - Validação de Cliente misturando muitos campos

O método `validarCliente` valida nome, CPF, endereço, RG e profissão no mesmo bloco.

Sugestão: dividir a validação em métodos menores, como `validarNome`, `validarCpfCliente`, `validarEndereco`, `validarRg` e `validarProfissao`.

Benefício: melhora a leitura do método principal e facilita testes específicos para cada campo.

---

### Comentário 41 - Retorno com apenas uma mensagem de erro

Os métodos retornam `Optional<String>`, ou seja, apenas o primeiro erro encontrado.

Sugestão: avaliar retornar uma lista de erros, como `List<String>`, principalmente em formulários de cadastro.

Benefício: o usuário poderia ver todos os problemas do formulário de uma vez, em vez de corrigir um erro por tentativa.

---

### Comentário 42 - Validação de CPF bem implementada

A validação de CPF remove caracteres não numéricos, verifica tamanho, rejeita dígitos repetidos e calcula os dígitos verificadores.

Ponto positivo: essa regra melhora a qualidade dos dados cadastrados e evita aceitar CPFs claramente inválidos.

Sugestão: manter testes unitários específicos para CPFs válidos, inválidos, nulos, formatados e com dígitos repetidos.

---

### Comentário 43 - Normalização de placa pode cortar entrada inválida

O método `normalizarPlaca` remove caracteres inválidos, transforma em maiúsculas e corta a string para 7 caracteres caso seja maior.

Sugestão: avaliar se cortar automaticamente é o melhor comportamento. Em alguns casos, pode ser mais seguro retornar a placa completa normalizada e deixar a validação acusar excesso de caracteres.

Benefício: evita que uma entrada errada seja alterada silenciosamente antes da validação.

---

### Comentário 44 - Uso de Year.now diretamente na validação

O método `validarAnoVeiculo` usa `Year.now().getValue()` diretamente.

Sugestão: para facilitar testes, considerar receber o ano atual como parâmetro ou encapsular isso em um serviço/clock.

Benefício: torna os testes mais previsíveis e evita dependência direta do ano do sistema.

---

### Comentário 45 - Validação de URL de foto simplificada

A validação de URL aceita caminhos iniciados com `/` e URLs `http/https`, o que atende ao projeto acadêmico.

Sugestão: caso o sistema evolua, considerar validações mais específicas para evitar URLs malformadas ou entradas perigosas.

Benefício: melhora segurança e consistência dos dados armazenados.

---

### Comentário 46 - Falta de integração com Bean Validation

O projeto poderia aproveitar anotações como `@NotBlank`, `@Size`, `@Pattern` e `@Positive` em DTOs ou entidades.

Sugestão: usar Bean Validation para regras simples e manter `ValidationRules` apenas para regras mais complexas, como CPF, CNPJ e placa.

Benefício: reduz código manual, padroniza validações e melhora integração com frameworks Java.

## Comentários de Code Review - ClienteService.java

### Comentário 47 - Boa separação do hash de senha

O `ClienteService` não gera o hash diretamente, ele delega essa responsabilidade para `PasswordHashService`.

Ponto positivo: isso melhora a separação de responsabilidades e facilita trocar a estratégia de hash futuramente sem alterar a regra de cadastro do cliente.

---

### Comentário 48 - Dependência direta do AutomovelRepository

O `ClienteService` depende diretamente de `AutomovelRepository` para atualizar veículos antes de excluir um cliente.

Sugestão: avaliar se essa regra deveria ser delegada ao `PropriedadeVeiculoService`, já que ela envolve titularidade de automóvel.

Benefício: centraliza regras de propriedade do veículo em um único serviço e reduz acoplamento entre cliente e automóvel.

---

### Comentário 49 - Exclusão de cliente com regra de domínio complexa

O método `excluir` não apenas remove o cliente, mas também altera a titularidade dos automóveis associados a ele.

Sugestão: considerar um serviço específico para esse fluxo, como `ExclusaoClienteService` ou `ClienteWorkflowService`.

Benefício: deixa claro que a exclusão do cliente possui efeitos colaterais no domínio e facilita testes dessa regra.

---

### Comentário 50 - Uso de IllegalStateException para regras de negócio

Assim como em outros services, o `ClienteService` usa `IllegalStateException` para situações como CPF duplicado, cliente inexistente ou uso incorreto do método.

Sugestão: criar exceções específicas, como `ClienteNaoEncontradoException`, `CpfDuplicadoException` ou `RegraNegocioException`.

Benefício: melhora o tratamento de erro nos controllers e evita depender de mensagens textuais para identificar o problema.

---

### Comentário 51 - Verificação de CPF duplicado deve ser reforçada no banco

O service verifica se já existe cliente com o mesmo CPF antes de salvar ou atualizar.

Sugestão: além da verificação no service, garantir uma restrição `UNIQUE` no banco de dados para o CPF normalizado.

Benefício: evita duplicidade mesmo em cenários de concorrência, quando duas requisições tentam cadastrar o mesmo CPF ao mesmo tempo.

---

### Comentário 52 - Cadastro com senha poderia ser transacional

O método `cadastrarComSenha` valida cliente, normaliza CPF, valida senha, verifica duplicidade, gera hash e salva.

Sugestão: considerar o uso de `@Transactional` nesse método, principalmente por envolver uma sequência importante de validações e persistência.

Benefício: garante maior consistência caso o fluxo de cadastro cresça no futuro.

---

### Comentário 53 - Mensagem expondo CPF

No método `salvar`, a mensagem `"CPF já cadastrado: " + cliente.getCpf()` exibe o CPF diretamente.

Sugestão: evitar expor dados sensíveis em mensagens de erro, usando algo como `"CPF já cadastrado."`.

Benefício: reduz exposição desnecessária de dados pessoais.

---

### Comentário 54 - Uso misto de @Valid e ValidationRules

O método `salvar` recebe `@Valid Cliente cliente`, mas as validações principais são feitas manualmente por `ValidationRules`.

Sugestão: padronizar a estratégia de validação: usar Bean Validation para regras simples e `ValidationRules` apenas para regras específicas, como CPF.

Benefício: deixa a validação mais consistente e reduz duplicação de regras.

---

### Comentário 55 - Exclusão física de cliente

O método `excluir` remove o cliente diretamente do banco.

Sugestão: avaliar se seria melhor usar exclusão lógica, por exemplo com um campo `ativo` ou `excluidoEm`.

Benefício: preserva histórico de pedidos, contratos e auditoria, principalmente em um sistema que envolve documentos e registros financeiros.

---

### Comentário 56 - Listagem geral de clientes

O método `listarTodos` retorna todos os clientes cadastrados.

Sugestão: garantir que esse método seja usado apenas em áreas autorizadas, como administração ou agente, e nunca exposto para cliente comum.

Benefício: evita vazamento de dados de outros clientes.

## Comentários de Code Review - AutomovelService.java

### Comentário 57 - Filtro de catálogo em memória

O método `buscarCatalogo` busca todos os automóveis e depois aplica filtros em memória usando `Stream`.

Sugestão: caso o volume de veículos cresça, mover esses filtros para o repositório/banco de dados, usando query com parâmetros opcionais.

Benefício: melhora performance e evita carregar todos os registros antes de filtrar.

---

### Comentário 58 - Comentário justificando decisão técnica

O código informa que o filtro em memória foi escolhido para manter o repositório simples no contexto do laboratório.

Ponto positivo: essa justificativa ajuda quem lê o código a entender que foi uma decisão consciente, não necessariamente um erro.

Sugestão: manter esse tipo de comentário quando houver decisões técnicas provisórias ou acadêmicas.

---

### Comentário 59 - Duplicação entre cadastrar e atualizar

Os métodos `cadastrar` e `atualizar` repetem várias etapas: normalizar placa, validar placa, validar ano, normalizar marca/modelo, validar marca/modelo e validar URL da foto.

Sugestão: extrair essa lógica repetida para métodos privados, como `validarDadosAutomovel` ou `normalizarDadosAutomovel`.

Benefício: reduz duplicação e facilita manutenção caso alguma regra de validação mude.

---

### Comentário 60 - Possível uso de DTO/Form Object para automóvel

Os métodos `cadastrar` e `atualizar` recebem vários parâmetros soltos: placa, marca, modelo, ano e fotoUrl.

Sugestão: criar um objeto de entrada, como `AutomovelForm` ou `AutomovelRequest`.

Benefício: melhora a organização dos dados, facilita validação e evita métodos com muitos parâmetros.

---

### Comentário 61 - Regra de placa única deve ser reforçada no banco

O service verifica se já existe automóvel com a mesma placa antes de salvar ou atualizar.

Sugestão: além da verificação no service, garantir uma restrição `UNIQUE` no banco para `placaNormalizada`.

Benefício: evita duplicidade em situações de concorrência, quando duas requisições tentam cadastrar a mesma placa ao mesmo tempo.

---

### Comentário 62 - Cadastro poderia ser transacional

O método `cadastrar` executa validações, verifica duplicidade, cria o automóvel e salva no repositório, mas não está anotado com `@Transactional`.

Sugestão: considerar o uso de `@Transactional` também no cadastro, assim como já ocorre no método `atualizar`.

Benefício: mantém consistência no padrão das operações de escrita.

---

### Comentário 63 - Ordenação poderia ser centralizada

A ordenação por marca, modelo, ano e placa é definida diretamente dentro do método `buscarCatalogo`.

Sugestão: extrair o comparator para um método privado, por exemplo `comparadorPorMarcaModeloAnoPlaca`.

Benefício: melhora a leitura do método principal e facilita reutilização da ordenação em outros pontos.

---

### Comentário 64 - Uso de strings fixas para tipo de ordenação

O método `buscarCatalogo` compara strings como `"ano_desc"`, `"ano_asc"` e `"marca"`.

Sugestão: criar um enum para representar os tipos de ordenação do catálogo.

Benefício: evita erro de digitação, melhora legibilidade e facilita manutenção.

---

### Comentário 65 - Boa definição de titularidade inicial

No cadastro do automóvel, o sistema define o proprietário inicial como `LOCADORA` e limpa o `proprietarioCliente`.

Ponto positivo: essa regra deixa explícito o estado inicial do veículo antes de qualquer contrato.

Sugestão: manter essa regra centralizada no service ou em uma factory de criação de automóvel.

# Análise Geral da Arquitetura

O projeto utiliza uma arquitetura MVC com separação entre Controllers, Services, Repositories, entidades de domínio, validações e views Thymeleaf. Essa estrutura é adequada para uma aplicação web acadêmica, pois separa a camada de apresentação, a regra de negócio e a persistência.

Como ponto positivo, o projeto já possui uma organização clara por pacotes, como `controller`, `service`, `repository`, `domain` e `validation`. Isso facilita a leitura do código e demonstra uma preocupação inicial com separação de responsabilidades.

Como melhoria futura, o projeto poderia evoluir para uma arquitetura em camadas mais rígida ou até uma arquitetura inspirada em Clean Architecture/Hexagonal Architecture, principalmente porque o domínio já possui regras relevantes envolvendo pedidos, contratos, aprovação, reprovação, automóveis, titularidade e rendimentos.

## Sugestão de evolução arquitetural

Uma possível evolução seria organizar o sistema em camadas como:

- Camada de apresentação: Controllers e Views Thymeleaf
- Camada de aplicação: casos de uso, como `CriarPedidoUseCase`, `AprovarPedidoUseCase` e `GerarContratoUseCase`
- Camada de domínio: entidades, enums e regras puras do negócio
- Camada de infraestrutura: repositories, banco de dados, sessão e configurações externas

Essa separação ajudaria a reduzir o acoplamento entre regras de negócio e framework, além de facilitar testes e manutenção.

---

# Sugestões de Padrões de Projeto

## Repository

O projeto já utiliza repositories para acesso a dados, o que é um ponto positivo. Esse padrão ajuda a isolar a persistência e evita que controllers ou services manipulem diretamente detalhes do banco.

Sugestão: manter as consultas concentradas nos repositories e evitar que regras de negócio sejam colocadas nessa camada.

## Service Layer

O projeto também utiliza services para centralizar regras de negócio. Isso é adequado para o padrão MVC.

Sugestão: alguns services podem ser divididos conforme crescerem. Por exemplo, o fluxo de aprovação de pedido poderia futuramente ficar em um serviço específico, como `PedidoWorkflowService` ou `AnalisePedidoService`.

## Strategy

O padrão Strategy seria útil para regras que variam de acordo com o tipo de contrato.

Atualmente, regras relacionadas a `TipoContrato` aparecem no fluxo de contrato e titularidade do veículo. Caso novos tipos de contrato sejam adicionados, uma estratégia por tipo poderia evitar crescimento de `switch` ou `if/else`.

Exemplo de possível divisão:

- `LocacaoSimplesStrategy`
- `LocacaoComOpcaoCompraStrategy`
- `CreditoBancarioStrategy`

Benefício: cada tipo de contrato teria sua própria regra, facilitando manutenção e respeitando o princípio Aberto/Fechado.

## Factory

O padrão Factory poderia ser usado na criação de contratos ou objetos mais complexos.

Exemplo: uma `ContratoFactory` poderia centralizar a criação do objeto `Contrato`, número do contrato, data de geração e termos iniciais.

Benefício: evita que o service acumule lógica de criação e montagem de objetos.

## DTO / Form Object

O projeto poderia usar DTOs ou objetos de formulário para entrada de dados vindos das telas.

Exemplo:

- `CriarPedidoForm`
- `EditarClienteForm`
- `CadastrarAutomovelForm`
- `AprovarPedidoForm`

Benefício: evita que os controllers trabalhem diretamente com muitos parâmetros soltos e facilita validações.

## ViewModel

Como os controllers montam muitos `Map<String, Object>` para as views Thymeleaf, seria interessante criar ViewModels.

Exemplo:

- `PedidoListaViewModel`
- `PedidoFormularioViewModel`
- `PainelAgenteViewModel`

Benefício: reduz erro em nomes de atributos e melhora a organização da camada de apresentação.

---

# Sugestões de Refatoração

## Separar controllers grandes

Alguns controllers concentram muitos fluxos diferentes. Uma melhoria seria dividir controllers por responsabilidade.

Exemplo:

- `AgenteAuthController`
- `AgentePedidoController`
- `AgenteContratoController`
- `PedidoController`
- `ContratoController`

Benefício: melhora a legibilidade e evita controllers grandes demais.

## Separar validações por domínio

A classe `ValidationRules` concentra validações de cliente, senha, CPF, CNPJ, rendimento, automóvel, placa, foto e pedido.

Sugestão: dividir em classes menores:

- `ClienteValidator`
- `DocumentoValidator`
- `PedidoValidator`
- `AutomovelValidator`
- `RendimentoValidator`

Benefício: melhora coesão e facilita testes.

## Criar exceções de domínio

O projeto utiliza `IllegalStateException` para várias regras de negócio.

Sugestão: criar exceções específicas, como:

- `PedidoNaoEncontradoException`
- `ClienteNaoEncontradoException`
- `RegraNegocioException`
- `PedidoStatusInvalidoException`

Benefício: evita tratamento de erro baseado em texto e deixa o código mais expressivo.

## Centralizar redirecionamentos

Há vários redirecionamentos com mensagens e erros usando `UriBuilder`.

Sugestão: criar métodos utilitários ou uma classe auxiliar para redirecionamentos.

Benefício: reduz duplicação e padroniza o uso de mensagens na aplicação.

---

# Code Smells Identificados

## Classe com muitas responsabilidades

Algumas classes, principalmente controllers e services principais, acumulam várias responsabilidades.

Exemplo: controller lidando com autenticação, busca de dados, tratamento de erro, montagem de model e redirecionamento.

Impacto: dificulta manutenção e testes.

## Duplicação de lógica

A verificação de sessão aparece repetida em vários métodos de controller.

Impacto: caso a regra de autenticação mude, será necessário alterar vários pontos.

## Tratamento de erro por string

Em alguns pontos, o código compara mensagens de exceção para decidir o fluxo.

Impacto: se o texto da mensagem mudar, a lógica pode quebrar.

## Strings mágicas

Há várias strings fixas para nomes de views, rotas e mensagens.

Impacto: aumenta risco de erro de digitação e dificulta refatorações.

## Método com muitas etapas

Alguns métodos fazem muitas ações em sequência, como validar, buscar dados, alterar status, salvar, gerar contrato e redirecionar.

Impacto: reduz legibilidade e dificulta testes isolados.

---

# Boas Práticas Observadas

O projeto também possui vários pontos positivos:

- Boa separação inicial entre controller, service, repository e domain.
- Uso de BCrypt para senha.
- Uso de sessão separada para cliente e agente.
- Validações tanto no cliente quanto no servidor.
- Uso de repositories para persistência.
- Uso de services para regras de negócio.
- Testes automatizados para services e controllers.
- Uso de transação em operações críticas.
- Organização das views Thymeleaf por domínio.
- Documentação no README e presença de diagramas UML.

Esses pontos mostram que o projeto possui uma boa base e pode evoluir com refatorações graduais.

---

# Conclusão Geral do Code Review

De forma geral, o projeto apresenta uma base funcional bem estruturada para um sistema acadêmico de aluguel de carros. A escolha de uma arquitetura MVC com Java, Micronaut, Thymeleaf, JPA/Hibernate e banco SQL está coerente com o objetivo do sistema.

As principais oportunidades de melhoria estão relacionadas à redução de acoplamento, separação de responsabilidades, extração de validações, criação de DTOs/ViewModels, uso de padrões como Strategy e Factory, e melhoria no tratamento de exceções.

A recomendação principal é evoluir o projeto aos poucos, sem reescrever tudo de uma vez. O caminho mais seguro seria começar pela separação dos controllers grandes, criação de validators específicos e extração das regras de contrato/titularidade para estratégias ou serviços de workflow.

Com essas melhorias, o sistema tende a ficar mais fácil de manter, testar e expandir em futuras versões.