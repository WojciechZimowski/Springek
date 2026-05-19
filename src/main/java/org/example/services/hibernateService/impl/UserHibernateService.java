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
        if (id.equals(loggedUserId)) {
            throw new IllegalArgumentException("Nie możesz usunąć samego siebie");
        }

        Transaction tx = null;
        boolean hasActiveRental = false;


        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Long activeRentalsCount = session.createQuery(
                            "SELECT COUNT(r) FROM Rental r WHERE r.user.id = :uId AND (r.returnDateTime IS NULL OR r.returnDateTime = '')", Long.class)
                    .setParameter("uId", id)
                    .uniqueResult();

            if (activeRentalsCount > 0) {
                hasActiveRental = true;
            } else {
                User user = session.get(User.class, id);
                if (user != null) {
                    session.remove(user);
                } else {
                    throw new IllegalArgumentException("Nie znaleziono użytkownika o podanym ID: " + id);
                }
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }

        // 2. Transakcja i sesja są już całkowicie ZAMKNIĘTE. Baza danych jest bezpieczna.
        // DOPIERO TERAZ sprawdzamy flagę i rzucamy Twój komunikat do UI:
        if (hasActiveRental) {
            throw new IllegalStateException("Użytkownik posiada aktywne wypożyczenie!");
        }
    }
}
