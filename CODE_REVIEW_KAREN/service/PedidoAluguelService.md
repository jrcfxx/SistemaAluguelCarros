# PedidoAluguelService.java

O `PedidoAluguelService` representa o núcleo funcional do sistema, implementando o ciclo completo de vida dos pedidos de aluguel. A classe coordena a criação, alteração, cancelamento, aprovação e reprovação de pedidos, além de garantir a consistência entre as diversas entidades envolvidas no processo.

O uso de `@Transactional` assegura atomicidade nas operações, preservando a integridade dos dados mesmo em cenários de falha. As validações de estado impedem transições inválidas, reforçando a modelagem correta do domínio e evitando inconsistências de negócio.

Trata-se de uma classe altamente estratégica, que demonstra domínio de modelagem de processos, encapsulamento de regras de negócio e aplicação adequada de princípios de Engenharia de Software.