package Repositories.Interface;

import java.util.List;

public interface IRepository<ModelType, ID> {

    public ModelType findById(ID id);
    public List<ModelType> readAll();

    public ModelType save(ModelType model);
    public void delete(ModelType model);


}
