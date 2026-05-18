package org.example.services.jdbcService;

import org.example.models.Vehicle;
import org.example.repositories.IVehicleRepository;
import org.example.repositories.RentalRepository;
import org.example.services.VehicleValidator;

import java.util.List;

public class VehicleService {
    private final IVehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final VehicleValidator vehicleValidator;
    public VehicleService(IVehicleRepository vehicleRepository, RentalRepository rentalRepository, VehicleValidator vehicleValidator) {
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