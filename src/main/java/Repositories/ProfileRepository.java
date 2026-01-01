package Repositories;

import Entities.Profile;
import Repositories.Interface.ISecureRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.util.List;

public class ProfileRepository extends JPARepository implements ISecureRepository<Profile, Integer> {

    public ProfileRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Profile findById(Integer id, String uuid) {
        try {
            return this.getEntityManager()
                    .createQuery("SELECT p FROM Profile p WHERE p.owner.uuid = :uuid AND p.id = :id", Profile.class)
                    .setParameter("uuid", uuid)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Profile> readAll(String uuid) {
        return this.getEntityManager()
                .createQuery("SELECT p FROM Profile p WHERE p.owner.uuid = :uuid", Profile.class)
                .setParameter("uuid", uuid)
                .getResultList();
    }

    @Override
    public Profile save(Profile model) {

        EntityManager em = this.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();
            model = em.merge(model);
            tx.commit();

            return model;

        } catch (Exception e) {

            if (tx.isActive()) tx.rollback();
            throw e;

        }

    }

    @Override
    public void delete(Profile model) {

        EntityManager em = this.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        int id = model.getId();

        try {

            tx.begin();
            Profile profile = em.find(Profile.class, id);

            if (profile != null) {
                em.remove(profile);
            }

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) tx.rollback();
            throw e;

        }

    }

}
