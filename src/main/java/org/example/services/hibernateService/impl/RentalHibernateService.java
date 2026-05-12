package org.example.services.hibernateService.impl;

import org.example.db.HibernateConfig;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.services.hibernateService.RentalServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class RentalHibernateService implements RentalServiceInterface {
    private final RentalRepository rentalRepository;
    private final VehicleHibernateService vehicleRepository;
    private final UserHibernateService userRepository;

    public RentalHibernateService(RentalRepository rentalRepository, VehicleHibernateService vehicleHibernateService, UserHibernateService userHibernateService) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleHibernateService;
        this.userRepository = userHibernateService;
    }


    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        Transaction tx = null;
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            setSession(session);

            boolean userHasActivRental = rentalRepository.findAll().stream()
                    .anyMatch(r->userId.equals(r.getUserId())&& r.isActive());
            if(userHasActivRental){
                throw new IllegalStateException("Masz już aktywne wypożyczenie");
            }
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(()-> new IllegalArgumentException("Nie znaleziono pojazdu"));
            User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException(ytkownika))
        }
    }

    private void setSession(Session session) {
    }

    @Override
    public Rental returnVehicle(String userId) {
        return null;
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return Optional.empty();
    }

    @Override
    public List<Rental> findAllRentals() {
        return List.of();
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        return List.of();
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return false;
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        return false;
    }
}
