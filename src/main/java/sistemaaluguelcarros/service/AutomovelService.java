package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.TipoProprietarioVeiculo;
import sistemaaluguelcarros.repository.AutomovelRepository;
import sistemaaluguelcarros.validation.ValidationRules;

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
