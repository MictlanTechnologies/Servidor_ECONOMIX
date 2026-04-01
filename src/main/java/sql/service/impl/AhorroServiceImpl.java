package sql.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import sql.model.Ahorro;
import sql.repository.AhorroRepository;
import sql.service.AhorroService;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class AhorroServiceImpl implements AhorroService {

    private final AhorroRepository ahorroRepository;

    @Override
    public List<Ahorro> getAll() {
        return ahorroRepository.findAll();
    }

    @Override
    public Ahorro getById(Integer id) {
        return ahorroRepository.findById(id).orElse(null);
    }

    @Override
    public Ahorro save(Ahorro ahorro) {
        syncLegacyFields(ahorro);
        return ahorroRepository.save(ahorro);
    }

    @Override
    public void delete(Integer id) {
        ahorroRepository.deleteById(id);
    }

    @Override
    public Ahorro update(Integer id, Ahorro ahorro) {
        return ahorroRepository.findById(id)
                .map(existing -> {
                    BeanUtils.copyProperties(ahorro, existing, "idAhorro");
                    syncLegacyFields(existing);
                    return ahorroRepository.save(existing);
                })
                .orElse(null);
    }

    private void syncLegacyFields(Ahorro ahorro) {
        if (ahorro.getMontoAhorrado() == null && ahorro.getMontoAhorro() != null) {
            ahorro.setMontoAhorrado(ahorro.getMontoAhorro());
        }
        if (ahorro.getMontoAhorro() == null && ahorro.getMontoAhorrado() != null) {
            ahorro.setMontoAhorro(ahorro.getMontoAhorrado());
        }
        if (ahorro.getMeta() == null && ahorro.getMontoAhorro() != null) {
            ahorro.setMeta(ahorro.getMontoAhorro());
        }
        if (ahorro.getMeta() == null) {
            ahorro.setMeta(BigDecimal.ZERO);
        }
    }
}
