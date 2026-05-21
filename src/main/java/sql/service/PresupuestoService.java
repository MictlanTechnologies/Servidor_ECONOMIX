package sql.service;

import sql.dto.PresupuestoDto;

import java.util.List;

public interface PresupuestoService {

    List<PresupuestoDto> getAll();

    PresupuestoDto getById(Integer id);

    List<PresupuestoDto> getByUsuario(Integer idUsuario);

    List<PresupuestoDto> getByUsuarioAndPeriodo(Integer idUsuario, Integer mes, Integer anio);

    PresupuestoDto getByUsuarioCategoriaPeriodo(Integer idUsuario, String categoria, Integer mes, Integer anio);

    PresupuestoDto save(PresupuestoDto dto);

    PresupuestoDto update(Integer id, PresupuestoDto dto);

    void delete(Integer id);
}
