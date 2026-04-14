package sql.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sql.dto.EtiquetaDto;
import sql.model.Etiqueta;
import sql.repository.EtiquetaRepository;
import sql.service.EtiquetaService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class EtiquetaServiceImpl implements EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;

    @Override
    public List<EtiquetaDto> obtenerEtiquetasPorUsuario(Integer idUsuario) {
        return etiquetaRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public EtiquetaDto crearEtiqueta(Integer idUsuario, String nombre) {
        String slug = normalizarSlug(nombre);
        
        // Verificar si ya existe
        if (etiquetaRepository.findByIdUsuarioAndSlug(idUsuario, slug).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La etiqueta ya existe para este usuario");
        }

        Etiqueta etiqueta = Etiqueta.builder()
                .idUsuario(idUsuario)
                .nombre(nombre)
                .slug(slug)
                .createdAt(LocalDateTime.now())
                .build();

        etiqueta = etiquetaRepository.save(etiqueta);
        return toDto(etiqueta);
    }

    @Override
    public void eliminarEtiqueta(Integer idUsuario, Integer idEtiqueta) {
        Etiqueta etiqueta = etiquetaRepository.findById(idEtiqueta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Etiqueta no encontrada"));

        if (!etiqueta.getIdUsuario().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar esta etiqueta");
        }

        etiquetaRepository.delete(etiqueta);
    }

    @Override
    public Etiqueta obtenerOCrearEtiqueta(Integer idUsuario, String nombre) {
        String slug = normalizarSlug(nombre);

        return etiquetaRepository.findByIdUsuarioAndSlug(idUsuario, slug)
                .orElseGet(() -> {
                    Etiqueta etiqueta = Etiqueta.builder()
                            .idUsuario(idUsuario)
                            .nombre(nombre)
                            .slug(slug)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return etiquetaRepository.save(etiqueta);
                });
    }

    private String normalizarSlug(String nombre) {
        return nombre.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceFirst("^-+", "")
                .replaceFirst("-+$", "");
    }

    private EtiquetaDto toDto(Etiqueta etiqueta) {
        return EtiquetaDto.builder()
                .idEtiqueta(etiqueta.getIdEtiqueta())
                .idUsuario(etiqueta.getIdUsuario())
                .nombre(etiqueta.getNombre())
                .slug(etiqueta.getSlug())
                .createdAt(etiqueta.getCreatedAt())
                .updatedAt(etiqueta.getUpdatedAt())
                .build();
    }
}
