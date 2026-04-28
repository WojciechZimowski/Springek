package org.example.services;

import org.example.models.Vehicle;
import org.example.repositories.impl.RentalJsonRepository;
import org.example.repositories.impl.VehicleJsonRepository;

import java.util.List;

public class VehicleService {
    private final VehicleJsonRepository vehicleRepository;
    private final RentalJsonRepository rentalRepository;
    private final VehicleValidator vehicleValidator;
    public VehicleService(VehicleJsonRepository vehicleRepository, RentalJsonRepository rentalRepository, VehicleValidator vehicleValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.vehicleValidator = vehicleValidator;
    }
    public void addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicleRepository.save(vehicle);

    }
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll().stream().toList();
    }
    public void removeVehicle(String id) {
        if(rentalRepository.findByVehicleIdAndReturnDateIsNull(id).isPresent()) {
            throw new RuntimeException("Pojazd " + id + " jest wypożyczony!");
        }
        vehicleRepository.deleteById(id);
    }
    public Vehicle findVehicleById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pojazdu o ID: " + id));
    }
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> rentalRepository.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .toList();
    }



}
