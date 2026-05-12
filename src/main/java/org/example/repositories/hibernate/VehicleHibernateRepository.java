package org.example.repositories.hibernate;

import org.example.models.Vehicle;
import org.example.repositories.IVehicleRepository;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateRepository implements IVehicleRepository {
    @Override
    public List<Vehicle> findAll() {
        return List.of();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return null;
    }

    @Override
    public void deleteById(String id) {

    }
}
