package sql.service;

import sql.dto.EtiquetaDto;
import sql.model.Etiqueta;

import java.util.List;

public interface EtiquetaService {
    List<EtiquetaDto> obtenerEtiquetasPorUsuario(Integer idUsuario);
    EtiquetaDto crearEtiqueta(Integer idUsuario, String nombre);
    void eliminarEtiqueta(Integer idUsuario, Integer idEtiqueta);
    Etiqueta obtenerOCrearEtiqueta(Integer idUsuario, String nombre);
}
