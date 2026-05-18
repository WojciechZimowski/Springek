package org.example.repositories.hibernate;

import org.example.db.HibernateConfig;
import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserHibernateRepository implements IUserRepository {
    private final UserHibernateRepository userRepository;
    private final RentalHibernateRepository rentalRepository;

    public UserHibernateRepository(UserHibernateRepository userRepository, RentalHibernateRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepository.setSession(session);
            return session.createQuery("from User",User.class).list();
        }

    }

    @Override
    public Optional<User> findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepository.setSession(session);
            return Optional.ofNullable(session.get(User.class,id));
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE login = :login",User.class).setParameter("login",login).uniqueResultOptional();
        }
    }

    @Override
    public User save(User user) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User savedUser = session.merge(user);
            tx.commit();
            return savedUser;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                throw new RuntimeException("Błąd zapisu użytkownika", e);
            }
        }
    }

    @Override
    public void deleteById(String id) {
        Transaction tx = null;
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class,id);
            if(user!=null){
                session.remove(user);
            }
            tx.commit();

        }catch(Exception e){
            if(tx!=null){
                tx.rollback();
                throw new RuntimeException("Błąd usuwania użytkownika",e);
            }
        }
    }

    public void setSession(Session session) {
    }
}
