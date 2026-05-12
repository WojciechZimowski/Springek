package org.example.services.hibernateService.impl;

import org.example.models.Rental;
import org.example.services.hibernateService.RentalServiceInterface;

import java.util.List;
import java.util.Optional;

public class RentalHibernateService implements RentalServiceInterface {
    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        return null;
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
