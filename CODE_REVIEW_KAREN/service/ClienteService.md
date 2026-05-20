# ClienteService.java

O `ClienteService` concentra de forma coesa todas as regras de negócio relacionadas ao gerenciamento de clientes, incluindo cadastro, atualização, exclusão e autenticação lógica. A classe demonstra excelente separação de responsabilidades ao delegar à camada de validação a verificação dos dados e ao repository a persistência das informações.

Destaca-se a normalização do CPF, que garante consistência independentemente do formato informado pelo usuário. A utilização de hash de senha evidencia preocupação com segurança, evitando armazenamento de credenciais em texto puro. O uso de `Optional` e exceções específicas contribui para um fluxo de execução seguro, explícito e de fácil manutenção.

Sob a perspectiva arquitetural, o serviço apresenta alta coesão e baixo acoplamento, sendo um componente fundamental para assegurar a integridade e a confiabilidade do cadastro de clientes.