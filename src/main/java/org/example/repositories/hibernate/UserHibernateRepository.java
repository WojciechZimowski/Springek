package org.example.repositories.hibernate;

import org.example.db.HibernateConfig;
import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserHibernateRepository implements IUserRepository {
    private Session session;

    @Override
    public List<User> findAll() {
        return session.createQuery("from User",User.class).list();
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(session.get(User.class,id));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return session.createQuery("from User Where login= :l",User.class).setParameter("l",login).uniqueResultOptional();
    }

    @Override
    public User save(User user) {
        return session.merge(user);
    }

    @Override
    public void deleteById(String id) {
        User user = session.get(User.class,id);
        if(user!=null){
            session.delete(user);
        }
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
