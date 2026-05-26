package org.example.services.hibernateService.impl;

import org.example.models.Vehicle;
import org.example.repositories.IVehicleRepository;
import org.example.repositories.RentalRepository;
import org.example.repositories.hibernate.RentalJpaRepository;
import org.example.repositories.hibernate.VehicleJpaRepository;
import org.example.services.hibernateService.VehicleServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleService implements VehicleServiceInterface {
    private final IVehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public VehicleService(IVehicleRepository vehicleRepository, RentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAllVehicles() {
    return vehicleRepository.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> rentalRepository.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle findById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie można znaleźć pojazdu"));
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);

    }

    @Override
    public void removeVehicle(String vehicleId) {
        if (isVehicleRented(vehicleId)) {
            throw new IllegalStateException("Pojazd jest aktualnie wypożyczony i nie można go usunąć!");
        }

        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym ID: " + vehicleId));

        vehicleRepository.deleteById(vehicleId);
    }



    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}
