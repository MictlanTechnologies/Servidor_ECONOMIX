package sql.service;

import sql.model.Etiqueta;

import java.util.List;

public interface EtiquetaService {
    List<Etiqueta> getAll(Integer idUsuario);
    Etiqueta save(Etiqueta etiqueta);
    boolean delete(Integer idEtiqueta, Integer idUsuario);
    Etiqueta findByIdAndUsuario(Integer idEtiqueta, Integer idUsuario);
    Etiqueta findOrCreate(Integer idUsuario, String rawNombre);
}
