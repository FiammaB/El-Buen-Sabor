package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.BaseEntity;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface BaseRepository <Entity extends BaseEntity, ID extends Serializable> extends JpaRepository<Entity, ID> {
    Logger logger = LoggerFactory.getLogger(BaseRepository.class);

    @Override
    @Transactional
    default void delete(Entity entity) {
        logger.info("EJECUTANDO DELETE SOBREESCRITO");
        entity.setEstaDadoDeBaja(true);
        save(entity);
    }

    @Override
    default Entity getById(ID id){
        logger.info("EJECUTANDO GEY BY ID SOBREESCRITO");
        var optionalEntity = findById(id);

        if (optionalEntity.isEmpty()){
            String errMsg = "La entidad con el id " + id + " se encuentra borrada logicamente";
            logger.error(errMsg);
            throw new RuntimeException(errMsg);
        }

        var entity = optionalEntity.get();
        if(entity.isEstaDadoDeBaja()){
            String errMsg = "La entidad del tipo " + entity.getClass().getSimpleName() + " con el id " + id + " se encuentra borrada logicamente";
            logger.error(errMsg);
            throw new RuntimeException(errMsg);
        }
        return entity;
    }

    default List<Entity> getAll(){
        logger.info("EJECUTANDO GET ALL PERSONALIZADO");
        var entities = findAll().stream().filter(e -> !e.isEstaDadoDeBaja()).toList();
        return entities;
    }

}