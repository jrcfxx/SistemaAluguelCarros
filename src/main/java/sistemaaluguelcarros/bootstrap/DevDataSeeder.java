package sistemaaluguelcarros.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.Empregador;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.Rendimento;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.domain.TipoContrato;
import sistemaaluguelcarros.domain.TipoProprietarioVeiculo;
import sistemaaluguelcarros.repository.AutomovelRepository;
import sistemaaluguelcarros.repository.ClienteRepository;
import sistemaaluguelcarros.repository.ContratoRepository;
import sistemaaluguelcarros.repository.PedidoAluguelRepository;
import sistemaaluguelcarros.repository.RendimentoRepository;
import sistemaaluguelcarros.service.PasswordHashService;
import sistemaaluguelcarros.validation.ValidationRules;

@Singleton
public class DevDataSeeder {

    private static final String SEED_SENHA = "Teste123";

    private final boolean enabled;
    private final ClienteRepository clienteRepository;
    private final AutomovelRepository automovelRepository;
    private final RendimentoRepository rendimentoRepository;
    private final PedidoAluguelRepository pedidoAluguelRepository;
    private final ContratoRepository contratoRepository;
    private final PasswordHashService passwordHashService;

    public DevDataSeeder(
            @Value("${APP_SEED:false}") boolean enabled,
            ClienteRepository clienteRepository,
            AutomovelRepository automovelRepository,
            RendimentoRepository rendimentoRepository,
            PedidoAluguelRepository pedidoAluguelRepository,
            ContratoRepository contratoRepository,
            PasswordHashService passwordHashService
    ) {
        this.enabled = enabled;
        this.clienteRepository = clienteRepository;
        this.automovelRepository = automovelRepository;
        this.rendimentoRepository = rendimentoRepository;
        this.pedidoAluguelRepository = pedidoAluguelRepository;
        this.contratoRepository = contratoRepository;
        this.passwordHashService = passwordHashService;
    }

    @EventListener
    @Transactional
    public void onStartup(ServerStartupEvent event) {
        if (!enabled) {
            return;
        }

        if (clienteRepository.count() > 0) {
            return;
        }

        Random random = new Random(20260413L);

        Cliente ana = criarCliente(
                random,
                "Ana Souza",
                "12.345.678-9",
                "Rua das Flores, 123 - Centro",
                "Engenheira de Software"
        );
        Cliente bruno = criarCliente(
                random,
                "Bruno Lima",
                "98.765.432-1",
                "Avenida Paulista, 1000 - Bela Vista",
                "Professor"
        );
        Cliente carla = criarCliente(
                random,
                "Carla Ribeiro",
                "45.678.901-2",
                "Rua dos Ipês, 45 - Jardim",
                "Analista de Sistemas"
        );
        Cliente diego = criarCliente(
                random,
                "Diego Martins",
                "MG-12.345.678",
                "Av. Brasil, 2500 - Centro",
                "Técnico em Informática"
        );
        Cliente elisa = criarCliente(
                random,
                "Elisa Ferreira",
                "11.223.344-5",
                "Rua Sete de Setembro, 90 - Centro",
                "Advogada"
        );
        Cliente fabio = criarCliente(
                random,
                "Fabio Oliveira",
                "SP-55.666.777",
                "Rua Joaquim Nabuco, 310 - Vila Nova",
                "Administrador"
        );
        Cliente gabriela = criarCliente(
                random,
                "Gabriela Santos",
                "33.444.555-6",
                "Alameda Santos, 500 - Jardins",
                "Médica"
        );
        Cliente heitor = criarCliente(
                random,
                "Heitor Costa",
                "RJ-12.345.67-8",
                "Rua XV de Novembro, 15 - Centro",
                "Engenheiro Civil"
        );

        criarRendimento(ana, "Tech LTDA", SeedDataFactory.gerarCnpjValidoSomenteDigitos(random), "8500.00");
        criarRendimento(ana, "Consultoria Alfa", SeedDataFactory.gerarCnpjValidoSomenteDigitos(random), "3200.00");

        criarRendimento(bruno, "Escola Estadual", SeedDataFactory.gerarCnpjValidoSomenteDigitos(random), "4200.00");

        criarRendimento(carla, "Fintech S/A", SeedDataFactory.gerarCnpjValidoSomenteDigitos(random), "9800.00");
        criarRendimento(carla, "Freelas", null, "1500.00");

        criarRendimento(diego, "Oficina Mecânica", SeedDataFactory.gerarCnpjValidoSomenteDigitos(random), "3900.00");

        criarRendimento(elisa, "Escritório Jurídico", SeedDataFactory.gerarCnpjValidoSomenteDigitos(random), "12000.00");

        criarRendimento(fabio, "Comércio Ltda", SeedDataFactory.gerarCnpjValidoSomenteDigitos(random), "5600.00");
        criarRendimento(fabio, "Investimentos", null, "2100.00");

        criarRendimento(gabriela, "Hospital Central", SeedDataFactory.gerarCnpjValidoSomenteDigitos(random), "15000.00");

        criarRendimento(heitor, "Construtora Delta", SeedDataFactory.gerarCnpjValidoSomenteDigitos(random), "10200.00");

        // Frota de luxo — fotos armazenadas localmente em /img/cars/
        Automovel porsche = criarAutomovelLocadora("POR0A11", "Porsche", "911 GT3", 2024,
                "/img/cars/porsche-911.png");
        Automovel ferrari = criarAutomovelLocadora("FER0B24", "Ferrari", "Roma", 2024,
                "/img/cars/ferrari-roma.png");
        Automovel lamborghini = criarAutomovelLocadora("LAM0C23", "Lamborghini", "Huracán EVO", 2023,
                "/img/cars/lamborghini-huracan.png");
        Automovel mclaren = criarAutomovelLocadora("MCL0D22", "McLaren", "720S", 2022,
                "/img/cars/mclaren-720s.png");
        Automovel mercedesAMG = criarAutomovelLocadora("AMG0E23", "Mercedes-AMG", "GT S", 2023,
                "/img/cars/mercedes-amg.png");
        Automovel bmwM8 = criarAutomovelLocadora("BMW0F22", "BMW", "M8 Gran Coupé", 2022,
                "/img/cars/bmw-m8.png");
        Automovel astonMartin = criarAutomovelCliente("AST0G21", "Aston Martin", "DB11", 2021, bruno,
                "/img/cars/aston-martin-db11.png");
        Automovel bentley = criarAutomovelLocadora("BNT0H23", "Bentley", "Continental GT", 2023,
                "/img/cars/bentley-continental.png");
        Automovel rangeRover = criarAutomovelLocadora("RRV0J24", "Range Rover", "Autobiography", 2024,
                "/img/cars/range-rover.png");
        Automovel rollsRoyceBanco = criarAutomovelBanco("RRC0K23", "Rolls-Royce", "Ghost", 2023,
                "/img/cars/rolls-royce-ghost.png");

        PedidoAluguel p1 = criarPedido(ana, porsche, "Solicito o Porsche 911 GT3 para viagens executivas por 30 dias.", StatusPedido.PENDENTE);
        criarPedido(carla, mclaren, "Preciso do McLaren 720S para eventos corporativos por 15 dias.", StatusPedido.PENDENTE);
        criarPedido(diego, mercedesAMG, "Veículo para deslocamentos de alto padrão por duas semanas.", StatusPedido.REPROVADO);
        criarPedido(elisa, rangeRover, "Solicito Range Rover para audiências e deslocamentos por 20 dias.", StatusPedido.CANCELADO);

        PedidoAluguel pAprovado1 = criarPedido(bruno, astonMartin, "Locação do Aston Martin DB11 com opção de compra ao final.", StatusPedido.APROVADO);
        criarContrato(pAprovado1, 1, "Contrato de locação com opção de compra gerado para ambiente local.", TipoContrato.LOCACAO_COM_OPCAO_COMPRA);

        PedidoAluguel pAprovado2 = criarPedido(gabriela, ferrari, "Ferrari Roma para deslocamentos premium por 45 dias.", StatusPedido.APROVADO);
        criarContrato(pAprovado2, 2, "Contrato de locação simples gerado para ambiente local.", TipoContrato.LOCACAO_SIMPLES);

        PedidoAluguel pAprovado3 = criarPedido(heitor, rollsRoyceBanco, "Rolls-Royce Ghost para uso executivo via crédito bancário.", StatusPedido.APROVADO);
        criarContrato(pAprovado3, 3, "Contrato de crédito bancário (leasing) gerado para ambiente local.", TipoContrato.CREDITO_BANCARIO);

        pedidoAluguelRepository.save(p1);
    }

    private Cliente criarCliente(Random random, String nome, String rg, String endereco, String profissao) {
        String cpfFormatado = SeedDataFactory.gerarCpfValidoFormatado(random);
        Cliente c = new Cliente(
                nome,
                ValidationRules.normalizarCpf(cpfFormatado),
                rg,
                endereco,
                profissao
        );
        c.setSenhaHash(passwordHashService.hash(SEED_SENHA));
        return clienteRepository.save(c);
    }

    private void criarRendimento(Cliente cliente, String empregadorNome, String cnpjSomenteDigitosOuNull, String valorMensal) {
        Rendimento r = new Rendimento();
        r.setCliente(cliente);
        r.setEmpregador(new Empregador(empregadorNome, cnpjSomenteDigitosOuNull));
        r.setValorMensal(new BigDecimal(valorMensal));
        rendimentoRepository.save(r);
    }

    private Automovel criarAutomovelLocadora(String placa, String marca, String modelo, int ano, String fotoUrl) {
        Automovel a = new Automovel(placa, marca, modelo, ano, fotoUrl);
        a.setTipoProprietario(TipoProprietarioVeiculo.LOCADORA);
        return automovelRepository.save(a);
    }

    private Automovel criarAutomovelCliente(String placa, String marca, String modelo, int ano, Cliente proprietario, String fotoUrl) {
        Automovel a = new Automovel(placa, marca, modelo, ano, fotoUrl);
        a.setTipoProprietario(TipoProprietarioVeiculo.CLIENTE);
        a.setProprietarioCliente(proprietario);
        return automovelRepository.save(a);
    }

    private Automovel criarAutomovelBanco(String placa, String marca, String modelo, int ano, String fotoUrl) {
        Automovel a = new Automovel(placa, marca, modelo, ano, fotoUrl);
        a.setTipoProprietario(TipoProprietarioVeiculo.BANCO);
        return automovelRepository.save(a);
    }

    private PedidoAluguel criarPedido(Cliente cliente, Automovel automovel, String descricao, StatusPedido status) {
        PedidoAluguel p = new PedidoAluguel(cliente, descricao);
        p.setAutomovel(automovel);
        p.setStatus(status);
        return pedidoAluguelRepository.save(p);
    }

    private void criarContrato(PedidoAluguel pedido, int sequencia, String termos, TipoContrato tipo) {
        int ano = LocalDateTime.now().getYear();
        String numero = String.format("CTR-%d-%04d", ano, sequencia);
        Contrato c = new Contrato(pedido, numero, termos, LocalDateTime.now(), tipo);
        contratoRepository.save(c);
    }
}

