package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.TipoProprietarioVeiculo;
import sistemaaluguelcarros.repository.AutomovelRepository;
import sistemaaluguelcarros.validation.ValidationRules;
import sistemaaluguelcarros.viewmodel.VeiculoDestaqueHome;

import java.util.List;
import java.util.Optional;

@Singleton
public class AutomovelService {

    private final AutomovelRepository automovelRepository;

    public AutomovelService(AutomovelRepository automovelRepository) {
        this.automovelRepository = automovelRepository;
    }

    public List<Automovel> listarTodos() {
        return automovelRepository.listarOrdenados();
    }

    public long contarTodos() {
        return automovelRepository.count();
    }

    /**
     * Categorias em destaque na home (imagens e preços ilustrativos para vitrine).
     */
    public List<VeiculoDestaqueHome> listarDestaques() {
        return List.of(
                new VeiculoDestaqueHome(
                        1,
                        "Sedans Executivos",
                        "Conforto e sofisticação para o dia a dia",
                        "/images/cars/sedan-executivo.jpg",
                        5,
                        4,
                        "Automático",
                        "549",
                        "/dia"
                ),
                new VeiculoDestaqueHome(
                        2,
                        "SUVs Premium",
                        "Espaço, presença e segurança em qualquer rota",
                        "/images/cars/suv-premium.jpg",
                        5,
                        5,
                        "Automático",
                        "659",
                        "/dia"
                ),
                new VeiculoDestaqueHome(
                        3,
                        "Esportivos",
                        "Performance e estilo para quem busca emoção",
                        "/images/cars/esportivo.jpg",
                        2,
                        2,
                        "Automático",
                        "1.299",
                        "/dia"
                ),
                new VeiculoDestaqueHome(
                        4,
                        "Elétricos",
                        "Mobilidade silenciosa e eficiente",
                        "/images/cars/eletrico.jpg",
                        5,
                        4,
                        "Automático",
                        "599",
                        "/dia"
                )
        );
    }

    /** Lista para seleção no formulário de pedido (cliente). */
    public List<Automovel> listarParaSelecaoPedido() {
        return listarTodos();
    }

    public Optional<Automovel> buscarPorId(Long id) {
        return automovelRepository.findById(id);
    }

    public Automovel cadastrar(String placaBruta, String marca, String modelo, Integer ano) {
        String placa = ValidationRules.normalizarPlaca(placaBruta);
        ValidationRules.validarPlaca(placa).ifPresent(msg -> {
            throw new IllegalStateException(msg);
        });
        ValidationRules.validarAnoVeiculo(ano).ifPresent(msg -> {
            throw new IllegalStateException(msg);
        });
        String marcaNorm = normalizarTexto(marca);
        String modeloNorm = normalizarTexto(modelo);
        ValidationRules.validarMarcaModeloAutomovel(marcaNorm, modeloNorm).ifPresent(msg -> {
            throw new IllegalStateException(msg);
        });
        if (automovelRepository.findByPlacaNormalizada(placa).isPresent()) {
            throw new IllegalStateException("Já existe automóvel cadastrado com esta placa.");
        }

        Automovel a = new Automovel(placa, marcaNorm, modeloNorm, ano);
        a.setTipoProprietario(TipoProprietarioVeiculo.LOCADORA);
        a.setProprietarioCliente(null);
        return automovelRepository.save(a);
    }

    @Transactional
    public Automovel atualizar(Long id, String placaBruta, String marca, String modelo, Integer ano) {
        Automovel existente = automovelRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Automóvel não encontrado."));
        String placa = ValidationRules.normalizarPlaca(placaBruta);
        ValidationRules.validarPlaca(placa).ifPresent(msg -> {
            throw new IllegalStateException(msg);
        });
        ValidationRules.validarAnoVeiculo(ano).ifPresent(msg -> {
            throw new IllegalStateException(msg);
        });
        String marcaNorm = normalizarTexto(marca);
        String modeloNorm = normalizarTexto(modelo);
        ValidationRules.validarMarcaModeloAutomovel(marcaNorm, modeloNorm).ifPresent(msg -> {
            throw new IllegalStateException(msg);
        });
        automovelRepository.findByPlacaNormalizada(placa).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new IllegalStateException("Já existe automóvel cadastrado com esta placa.");
            }
        });

        existente.setPlacaNormalizada(placa);
        existente.setMarca(marcaNorm);
        existente.setModelo(modeloNorm);
        existente.setAno(ano);
        return automovelRepository.update(existente);
    }

    private static String normalizarTexto(String s) {
        return s == null ? "" : s.trim();
    }
}
