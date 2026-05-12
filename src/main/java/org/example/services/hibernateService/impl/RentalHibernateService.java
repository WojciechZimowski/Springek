package org.example.services.hibernateService.impl;

import org.example.db.HibernateConfig;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.hibernate.RentalHibernateRepository;
import org.example.repositories.hibernate.UserHibernateRepository;
import org.example.repositories.hibernate.VehicleHibernateRepository;
import org.example.services.hibernateService.RentalServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RentalHibernateService implements RentalServiceInterface {
    private final RentalHibernateRepository rentalRepository;
    private final VehicleHibernateRepository vehicleRepository;
    private final UserHibernateRepository userRepository;

    public RentalHibernateService(RentalHibernateRepository rentalRepository, VehicleHibernateRepository vehicleHibernateService, UserHibernateRepository userHibernateService) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleHibernateService;
        this.userRepository = userHibernateService;
    }


    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            setSession(session);

            boolean userHasActivRental = rentalRepository.findAll().stream()
                    .anyMatch(r -> userId.equals(r.getUserId()) && r.isActive());
            if (userHasActivRental) {
                throw new IllegalStateException("Masz już aktywne wypożyczenie");
            }
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu"));
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(ytkownika));

            boolean vehicleIsRented = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isPresent();
            if (vehicleIsRented) {
                throw new IllegalStateException("Ten pojazd jest już wypożyczony");

            }
        }
    }


    @Override
    public Rental returnVehicle(String userId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            setSession(session);
            Rental rental = rentalRepository.findAll().stream()
                    .filter(r -> userId.equals(r.getUserId()))
                    .filter(Rental::isActive)
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Nie masz aktualnie żadnego pojazdu"));
            rental.setReturnDateTime(LocalDateTime.now().toString());
            Rental savedRental = rentalRepository.save(rental);
            tx.commit();
            return savedRental;
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepository.findAll().stream()
                    .filter(r -> userId.equals(r.getUserId())).filter(Rental::isActive).findFirst();
        }
    }

    @Override
    public List<Rental> findAllRentals() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepository.findAll();
        }
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepository.findAll().stream()
                    .filter(r -> userId.equals(r.getUserId())).toList();
        }
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }

    private void setSession(Session session) {
        rentalRepository.setSession(session);
        vehicleRepository.setSession(session);
        userRepository.setSession(session);

    }
    private void rollback(Transaction tx) {
        if(tx!=null && tx.isActive()){
            tx.rollback();
        }
    }
}