package sql.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import sql.model.Gasto;
import sql.model.GastoEtiqueta;
import sql.repository.GastoEtiquetaRepository;
import sql.repository.GastoRepository;
import sql.service.EtiquetaService;
import sql.service.GastoService;
import sql.service.PresupuestoService;

import java.util.List;

@Service
@AllArgsConstructor
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final PresupuestoService presupuestoService;
    private final EtiquetaService etiquetaService;
    private final GastoEtiquetaRepository gastoEtiquetaRepository;

    @Override
    public List<Gasto> getAll() {
        return gastoRepository.findAll();
    }

    @Override
    public Gasto getById(Integer id) {
        return gastoRepository.findById(id).orElse(null);
    }

    @Override
    public Gasto save(Gasto gasto) {
        // Validar presupuesto solo si se proporciona categoría
        if (gasto.getIdCategoriaPresupuesto() != null) {
            presupuestoService.validarGastoEnCategoria(
                    gasto.getIdUsuario(),
                    gasto.getIdCategoriaPresupuesto(),
                    gasto.getMontoGasto(),
                    gasto.getFechaGastos(),
                    null
            );
        }
        return gastoRepository.save(gasto);
    }

    @Override
    public void delete(Integer id) {
        gastoRepository.deleteById(id);
    }

    @Override
    public Gasto update(Integer id, Gasto gasto) {
        return gastoRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(gasto, existing, "idGastos");
                    // Validar presupuesto solo si se proporciona categoría
                    if (existing.getIdCategoriaPresupuesto() != null) {
                        presupuestoService.validarGastoEnCategoria(
                                existing.getIdUsuario(),
                                existing.getIdCategoriaPresupuesto(),
                                existing.getMontoGasto(),
                                existing.getFechaGastos(),
                                existing.getIdGastos()
                        );
                    }
                    return gastoRepository.save(existing);
                })
                .orElse(null);
    }

    @Override
    public Gasto saveWithTags(Gasto gasto, List<String> etiquetas) {
        // Guardar el gasto primero
        Gasto gastoGuardado = save(gasto);

        // Asociar etiquetas si se proporcionan
        if (etiquetas != null && !etiquetas.isEmpty()) {
            // Limpiar etiquetas anteriores si existen
            gastoEtiquetaRepository.deleteByIdGastos(gastoGuardado.getIdGastos());

            // Crear o reutilizar etiquetas y asociarlas
            for (String nombreEtiqueta : etiquetas) {
                var etiqueta = etiquetaService.obtenerOCrearEtiqueta(gasto.getIdUsuario(), nombreEtiqueta);

                GastoEtiqueta gastoEtiqueta = GastoEtiqueta.builder()
                        .idGastos(gastoGuardado.getIdGastos())
                        .idEtiqueta(etiqueta.getIdEtiqueta())
                        .build();

                gastoEtiquetaRepository.save(gastoEtiqueta);
            }
        }

        return gastoGuardado;
    }
}
