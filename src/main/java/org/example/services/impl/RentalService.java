package org.example.services.impl;

import org.example.models.Rental;
import org.example.repositories.IUserRepository;
import org.example.repositories.IVehicleRepository;
import org.example.repositories.RentalRepository;
import org.example.services.RentalServiceInterface;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalService implements RentalServiceInterface {
    private final RentalRepository rentalJsonRepository;
    private final IUserRepository userRepository;
    private final IVehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalJsonRepository, IUserRepository userRepository, IVehicleRepository vehicleRepository) {
        this.rentalJsonRepository = rentalJsonRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Rental rentVehicle(String uId, String vId) {
        if(vehicleHasActiveRental(vId)){
            throw new RuntimeException("Pojazd jest już wypożyczony.");
        }

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .userId(uId)
                .vehicleId(vId)
                .rentDateTime(LocalDateTime.now().toString())
                .build();
        rentalJsonRepository.save(rental);
    }

    public void returnVehicle(String vId) {
        rentalJsonRepository.findByVehicleIdAndReturnDateIsNull(vId).ifPresentOrElse(r -> {
            r.setReturnDateTime(LocalDateTime.now().toString());
            rentalJsonRepository.save(r);
        }, () -> { throw new RuntimeException("Nie znaleziono aktywnego wypożyczenia dla tego auta."); });
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

    public boolean isVehicleRented(String vId) {
        return rentalJsonRepository.findByVehicleIdAndReturnDateIsNull(vId).isPresent();
    }
    public List<Rental> findActiveRentalsByUser(String userId) {
        return rentalJsonRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .toList();
    }
    public List<Rental> findAllRentalsByUserId(String userId) {
        return rentalJsonRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .toList();
    }
}
