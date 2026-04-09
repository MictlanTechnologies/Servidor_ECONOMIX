package sql.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sql.model.Etiqueta;
import sql.repository.EtiquetaRepository;
import sql.service.EtiquetaService;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class EtiquetaServiceImpl implements EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;

    @Override
    public List<Etiqueta> getAll(Integer idUsuario) {
        if (idUsuario == null) {
            return List.of();
        }
        return etiquetaRepository.findByIdUsuario(idUsuario);
    }

    @Override
    public Etiqueta save(Etiqueta etiqueta) {
        NombreSlug nombreSlug = normalize(etiqueta.getNombre());
        etiquetaRepository.findByIdUsuarioAndSlug(etiqueta.getIdUsuario(), nombreSlug.slug)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe una etiqueta con ese nombre");
                });
        LocalDateTime now = LocalDateTime.now();
        etiqueta.setNombre(nombreSlug.nombre);
        etiqueta.setSlug(nombreSlug.slug);
        etiqueta.setCreatedAt(now);
        etiqueta.setUpdatedAt(now);
        return etiquetaRepository.save(etiqueta);
    }

    @Override
    public boolean delete(Integer idEtiqueta, Integer idUsuario) {
        return etiquetaRepository.findByIdEtiquetaAndIdUsuario(idEtiqueta, idUsuario)
                .map(existing -> {
                    etiquetaRepository.deleteById(existing.getIdEtiqueta());
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Etiqueta findByIdAndUsuario(Integer idEtiqueta, Integer idUsuario) {
        return etiquetaRepository.findByIdEtiquetaAndIdUsuario(idEtiqueta, idUsuario).orElse(null);
    }

    @Override
    public Etiqueta findOrCreate(Integer idUsuario, String rawNombre) {
        NombreSlug nombreSlug = normalize(rawNombre);
        return etiquetaRepository.findByIdUsuarioAndSlug(idUsuario, nombreSlug.slug)
                .orElseGet(() -> save(Etiqueta.builder()
                        .idUsuario(idUsuario)
                        .nombre(nombreSlug.nombre)
                        .build()));
    }

    private NombreSlug normalize(String raw) {
        String base = raw == null ? "" : raw.trim();
        if (base.isBlank()) {
            throw new IllegalArgumentException("Etiqueta inválida");
        }
        if (!base.startsWith("#")) {
            base = "#" + base;
        }
        String slugBase = base.substring(1).trim().toLowerCase(Locale.ROOT);
        slugBase = Normalizer.normalize(slugBase, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (slugBase.isBlank()) {
            throw new IllegalArgumentException("Etiqueta inválida");
        }
        return new NombreSlug("#" + slugBase, slugBase);
    }

    private record NombreSlug(String nombre, String slug) {
    }
}
