package org.example.services.hibernateService.impl;

import org.example.db.HibernateConfig;
import org.example.models.User;
import org.example.repositories.hibernate.RentalHibernateRepository;
import org.example.repositories.hibernate.UserHibernateRepository;
import org.example.repositories.hibernate.VehicleHibernateRepository;
import org.example.services.hibernateService.UserServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserHibernateService implements UserServiceInterface {
    private final UserHibernateRepository userRepository;
    private final RentalHibernateRepository rentalRepository;

    public UserHibernateService(UserHibernateRepository userRepository, RentalHibernateRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }


    @Override
    public List<User> findAllUsers() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepository.setSession(session);
            return (List<User>) userRepository.findAll();
        }
    }

    @Override
    public User findById(String id) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepository.setSession(session);
            return (User) userRepository.findById(id).orElseThrow(()-> new RuntimeException("Nie znaleziono użytkownika"));
        }
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        if(id.equals(loggedUserId)){
            throw new IllegalArgumentException("Nie możesz usunąć samego siebie");
        }
        Transaction tx= null;
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userRepository.setSession(session);
            rentalRepository.setSession(session);
            boolean hasActive = rentalRepository.findAll().stream()
                    .anyMatch(r -> id.equals(r.getUser().getId()) && r.isActive());
            if (hasActive) {
                throw new IllegalStateException("Użytkownik posiada aktywne wypożyczenie!");
            }
            userRepository.deleteById(id);
            tx.commit();
        }catch(Exception ex){
            if(tx!=null){
                tx.rollback();
                throw ex;
            }
        }
    }
}
