package Repositories.Interface;

import java.util.List;

public interface IRepository<ModelType, ID> {

    ModelType findById(ID id);
    List<ModelType> readAll();

    ModelType save(ModelType model);
    void delete(ModelType model);


}
