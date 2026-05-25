package org.example.repositories.hibernate;

import org.example.db.HibernateConfig;
import org.example.models.Vehicle;
import org.example.repositories.IVehicleRepository;
import org.example.repositories.hibernate.jpa.VehicleJpaRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class VehicleHibernateRepository implements IVehicleRepository {
    private final VehicleJpaRepository vehicleJpaRepository;

    public VehicleHibernateRepository(VehicleJpaRepository vehicleJpaRepository) {
        this.vehicleJpaRepository = vehicleJpaRepository;
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleJpaRepository.findAll();
    }

    @Override
    public Optional<Vehicle> findById(String id) {

        return vehicleJpaRepository.findById(id);
    }

    @Override
    public Vehicle save(Vehicle vehicle) {

        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        return vehicleJpaRepository.save(vehicle);
    }

    @Override
    public void deleteById(String id) {
        vehicleJpaRepository.deleteById(id);
    }


}
