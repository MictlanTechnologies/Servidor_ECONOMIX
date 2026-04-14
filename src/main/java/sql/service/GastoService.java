package sql.service;

import sql.model.Gasto;

import java.util.List;

public interface GastoService {
    List<Gasto> getAll();
    Gasto getById(Integer id);
    Gasto save(Gasto gasto);
    void delete(Integer id);
    Gasto update(Integer id, Gasto gasto);
    Gasto saveWithTags(Gasto gasto, List<String> etiquetas);
}
