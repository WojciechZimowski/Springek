package org.example.services.hibernateService.impl;

import org.example.db.HibernateConfig;
import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.hibernate.RentalHibernateRepository;
import org.example.repositories.hibernate.VehicleHibernateRepository;
import org.example.services.hibernateService.VehicleServiceInterface;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class VehicleHibernateService implements VehicleServiceInterface {
    private final VehicleHibernateRepository vehicleRepository;
    private final RentalHibernateRepository rentalRepository;

    public VehicleHibernateService(VehicleHibernateRepository vehicleRepository, RentalHibernateRepository rentalRepository) {
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
