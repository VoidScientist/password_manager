package Repositories.Interface;

import java.util.List;

/**
 * Interface utilisée pour UserProfileRepository
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 * @param <ModelType> type du modèle du repository
 * @param <ID> type de l'id du modèle
 */
public interface IRepository<ModelType, ID> {

    ModelType findById(ID id);
    List<ModelType> readAll();

    ModelType save(ModelType model);
    void delete(ModelType model);


}
