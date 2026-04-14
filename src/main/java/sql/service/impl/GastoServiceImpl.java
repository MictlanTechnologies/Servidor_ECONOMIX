package sql.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import sql.model.Gasto;
import sql.repository.GastoRepository;
import sql.service.GastoService;
import sql.service.PresupuestoService;

import java.util.List;

@Service
@AllArgsConstructor
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final PresupuestoService presupuestoService;

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
        presupuestoService.validarGastoEnCategoria(
                gasto.getIdUsuario(),
                gasto.getIdCategoriaPresupuesto(),
                gasto.getMontoGasto(),
                gasto.getFechaGastos(),
                null
        );
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
                    presupuestoService.validarGastoEnCategoria(
                            existing.getIdUsuario(),
                            existing.getIdCategoriaPresupuesto(),
                            existing.getMontoGasto(),
                            existing.getFechaGastos(),
                            existing.getIdGastos()
                    );
                    return gastoRepository.save(existing);
                })
                .orElse(null);
    }
}
