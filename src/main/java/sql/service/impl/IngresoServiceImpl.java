package sql.service.impl;

import sql.model.Ingreso;
import sql.model.IngresoEtiqueta;
import sql.repository.IngresoEtiquetaRepository;
import sql.repository.IngresoRepository;
import sql.service.EtiquetaService;
import sql.service.IngresoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class IngresoServiceImpl implements IngresoService {

    private final IngresoRepository ingresoRepository;
    private final EtiquetaService etiquetaService;
    private final IngresoEtiquetaRepository ingresoEtiquetaRepository;

    @Override
    public List<Ingreso> getAll() {
        return ingresoRepository.findAll();
    }

    @Override
    public Ingreso getById(Integer id) {
        return ingresoRepository.findById(id).orElse(null);
    }

    @Override
    public Ingreso save(Ingreso ingreso) {
        return ingresoRepository.save(ingreso);
    }

    @Override
    public void delete(Integer id) {
        ingresoRepository.deleteById(id);
    }

    @Override
    public Ingreso update(Integer id, Ingreso ingreso) {
        return ingresoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(ingreso, existing, "idIngresos");
                    return ingresoRepository.save(existing);
                })
                .orElse(null);
    }

    @Override
    public Ingreso saveWithTags(Ingreso ingreso, List<String> etiquetas) {
        // Guardar el ingreso primero
        Ingreso ingresoGuardado = save(ingreso);

        // Asociar etiquetas si se proporcionan
        if (etiquetas != null && !etiquetas.isEmpty()) {
            // Limpiar etiquetas anteriores si existen
            ingresoEtiquetaRepository.deleteByIdIngresos(ingresoGuardado.getIdIngresos());

            // Crear o reutilizar etiquetas y asociarlas
            for (String nombreEtiqueta : etiquetas) {
                var etiqueta = etiquetaService.obtenerOCrearEtiqueta(ingreso.getIdUsuario(), nombreEtiqueta);

                IngresoEtiqueta ingresoEtiqueta = IngresoEtiqueta.builder()
                        .idIngresos(ingresoGuardado.getIdIngresos())
                        .idEtiqueta(etiqueta.getIdEtiqueta())
                        .build();

                ingresoEtiquetaRepository.save(ingresoEtiqueta);
            }
        }

        return ingresoGuardado;
    }
}
