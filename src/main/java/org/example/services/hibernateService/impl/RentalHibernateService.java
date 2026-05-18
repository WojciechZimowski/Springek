package org.example.services.hibernateService.impl;

import org.example.db.HibernateConfig;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.hibernate.RentalHibernateRepository;
import org.example.repositories.hibernate.UserHibernateRepository;
import org.example.repositories.hibernate.VehicleHibernateRepository;
import org.example.services.hibernateService.RentalServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalHibernateService implements RentalServiceInterface {
    private final RentalHibernateRepository rentalRepository;
    private final VehicleHibernateRepository vehicleRepository;
    private final UserHibernateRepository userRepository;

    public RentalHibernateService(RentalHibernateRepository rentalRepository,
                                  VehicleHibernateRepository vehicleHibernateService,
                                  UserHibernateRepository userHibernateService) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleHibernateService;
        this.userRepository = userHibernateService;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();


            Optional<Rental> activeUserRental = session.createQuery(
                            "FROM Rental r WHERE r.user.id = :uId AND r.returnDateTime IS NULL", Rental.class)
                    .setParameter("uId", userId)
                    .uniqueResultOptional();

            if (activeUserRental.isPresent()) {
                throw new IllegalStateException("Masz już aktywne wypożyczenie!");
            }

            Vehicle vehicle = session.get(Vehicle.class, vehicleId);
            if (vehicle == null) {
                throw new IllegalArgumentException("Nie znaleziono pojazdu o podanym ID");
            }

            User user = session.get(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("Nie znaleziono użytkownika o podanym ID");
            }

            Optional<Rental> activeVehicleRental = session.createQuery(
                            "FROM Rental r WHERE r.vehicle.id = :vId AND r.returnDateTime IS NULL", Rental.class)
                    .setParameter("vId", vehicleId)
                    .uniqueResultOptional();

            if (activeVehicleRental.isPresent()) {
                throw new IllegalStateException("Ten pojazd jest już wypożyczony przez kogoś innego!");
            }

            Rental rental = Rental.builder()
                    .id(UUID.randomUUID().toString())
                    .user(user)
                    .vehicle(vehicle)
                    .rentDateTime(LocalDateTime.now().toString())
                    .build();

            session.persist(rental);

            tx.commit();
            return rental;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Rental returnVehicle(String userId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Rental rental = session.createQuery(
                            "FROM Rental r WHERE r.user.id = :uId AND r.returnDateTime IS NULL", Rental.class)
                    .setParameter("uId", userId)
                    .uniqueResultOptional()
                    .orElseThrow(() -> new IllegalArgumentException("Nie masz aktualnie żadnego wypożyczonego pojazdu"));

            rental.setReturnDateTime(LocalDateTime.now().toString());

            session.merge(rental);
            tx.commit();
            return rental;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Rental r WHERE r.user.id = :uId AND r.returnDateTime IS NULL", Rental.class)
                    .setParameter("uId", userId)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Rental> findAllRentals() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM Rental", Rental.class).getResultList();
        }
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Rental r WHERE r.user.id = :uId", Rental.class)
                    .setParameter("uId", userId)
                    .getResultList();
        }
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Optional<Rental> active = session.createQuery(
                            "FROM Rental r WHERE r.vehicle.id = :vId AND r.returnDateTime IS NULL", Rental.class)
                    .setParameter("vId", vehicleId)
                    .uniqueResultOptional();
            return active.isPresent();
        }
    }

    private void setSession(Session session) {
        rentalRepository.setSession(session);
        vehicleRepository.setSession(session);
        userRepository.setSession(session);
    }
}