package Repositories.Interface;

import java.util.List;

/**
 * Interface utilisée pour les dépôts de Profiles et Category.
 * Ceux-ci nécessitant un UUID pour récupérer les données.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 *
 * @param <ModelType> type du modèle du repository
 * @param <ID> type de l'id du modèle
 */
public interface ISecureRepository<ModelType, ID>  {

    ModelType findById(ID id, String uuid);
    List<ModelType> readAll(String uuid);

    ModelType save(ModelType model);
    void delete(ModelType model);

}
