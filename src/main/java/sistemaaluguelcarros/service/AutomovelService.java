package sistemaaluguelcarros.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.TipoProprietarioVeiculo;
import sistemaaluguelcarros.repository.AutomovelRepository;
import sistemaaluguelcarros.validation.ValidationRules;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Stream;

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

    /**
     * Catálogo público: busca simples por texto e filtros de ano, com ordenação.
     * Implementado em memória para manter o repositório simples no contexto do laboratório.
     */
    public List<Automovel> buscarCatalogo(String q, Integer anoMin, Integer anoMax, String sort) {
        String termo = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        Stream<Automovel> stream = listarTodos().stream();

        if (!termo.isBlank()) {
            stream = stream.filter(a -> {
                String marca = a.getMarca() == null ? "" : a.getMarca().toLowerCase(Locale.ROOT);
                String modelo = a.getModelo() == null ? "" : a.getModelo().toLowerCase(Locale.ROOT);
                String placa = a.getPlacaNormalizada() == null ? "" : a.getPlacaNormalizada().toLowerCase(Locale.ROOT);
                return marca.contains(termo) || modelo.contains(termo) || placa.contains(termo);
            });
        }
        if (anoMin != null) {
            stream = stream.filter(a -> a.getAno() != null && a.getAno() >= anoMin);
        }
        if (anoMax != null) {
            stream = stream.filter(a -> a.getAno() != null && a.getAno() <= anoMax);
        }

        Comparator<Automovel> porMarcaModelo = Comparator
                .comparing((Automovel a) -> a.getMarca() == null ? "" : a.getMarca(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(a -> a.getModelo() == null ? "" : a.getModelo(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(a -> a.getAno() == null ? 0 : a.getAno(), Comparator.reverseOrder())
                .thenComparing(a -> a.getPlacaNormalizada() == null ? "" : a.getPlacaNormalizada(), String.CASE_INSENSITIVE_ORDER);

        String s = sort == null ? "relevancia" : sort.trim().toLowerCase(Locale.ROOT);
        if ("ano_desc".equals(s)) {
            stream = stream.sorted(Comparator.comparing((Automovel a) -> a.getAno() == null ? 0 : a.getAno()).reversed().thenComparing(porMarcaModelo));
        } else if ("ano_asc".equals(s)) {
            stream = stream.sorted(Comparator.comparing((Automovel a) -> a.getAno() == null ? 0 : a.getAno()).thenComparing(porMarcaModelo));
        } else if ("marca".equals(s)) {
            stream = stream.sorted(porMarcaModelo);
        } else {
            // "relevância" simples: mantém ordenação padrão do repo (marca/modelo/placa)
            stream = stream.sorted(porMarcaModelo);
        }

        return stream.toList();
    }

    public Optional<Automovel> buscarPorId(Long id) {
        return automovelRepository.findById(id);
    }

    public Automovel cadastrar(String placaBruta, String marca, String modelo, Integer ano, String fotoUrl) {
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
        String fotoNorm = normalizarTexto(fotoUrl);
        ValidationRules.validarFotoUrlOpcional(fotoNorm).ifPresent(msg -> {
            throw new IllegalStateException(msg);
        });
        if (automovelRepository.findByPlacaNormalizada(placa).isPresent()) {
            throw new IllegalStateException("Já existe automóvel cadastrado com esta placa.");
        }

        Automovel a = new Automovel(placa, marcaNorm, modeloNorm, ano, fotoNorm.isBlank() ? null : fotoNorm);
        a.setTipoProprietario(TipoProprietarioVeiculo.LOCADORA);
        a.setProprietarioCliente(null);
        return automovelRepository.save(a);
    }

    @Transactional
    public Automovel atualizar(Long id, String placaBruta, String marca, String modelo, Integer ano, String fotoUrl) {
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
        String fotoNorm = normalizarTexto(fotoUrl);
        ValidationRules.validarFotoUrlOpcional(fotoNorm).ifPresent(msg -> {
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
        existente.setFotoUrl(fotoNorm.isBlank() ? null : fotoNorm);
        return automovelRepository.update(existente);
    }

    private static String normalizarTexto(String s) {
        return s == null ? "" : s.trim();
    }
}
