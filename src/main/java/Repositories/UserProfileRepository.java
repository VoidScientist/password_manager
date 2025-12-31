package Repositories;


import Entities.UserProfile;
import Repositories.Interface.IRepository;
import jakarta.persistence.*;

import java.util.List;

public class UserProfileRepository implements IRepository<UserProfile, String> {

    private final EntityManagerFactory emf;

    public UserProfileRepository(String pUnit) {

        this.emf = Persistence.createEntityManagerFactory(pUnit);

    }

    public UserProfile findByUsername(String username) {

        try (EntityManager em = emf.createEntityManager()) {

            return em.createQuery("select u from UserProfile u where u.username = :username", UserProfile.class)
                    .setParameter("username", username)
                    .getSingleResult();

        } catch (NoResultException e) {

            return null;

        }

    }

    @Override
    public UserProfile findById(String uuid) {

        try (EntityManager em = emf.createEntityManager()) {

            return em.find(UserProfile.class, uuid);

        }

    }

    @Override
    public List<UserProfile> readAll() {

        try (EntityManager em = emf.createEntityManager()) {

            return em.createQuery("select u from UserProfile u", UserProfile.class).getResultList();

        }

    }

    @Override
    public UserProfile save(UserProfile model) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try(em) {

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
    public void delete(UserProfile model) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        String uuid = model.getUuid();

        try(em) {

            tx.begin();
            UserProfile user = em.find(UserProfile.class, uuid);

            if (user != null) {
                em.remove(user);
            }

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) tx.rollback();
            throw e;

        }

    }

}
