package org.example.services.hibernateService.impl;

import org.example.models.Vehicle;
import org.example.services.hibernateService.VehicleServiceInterface;

import java.util.List;

public class VehicleHibernateService implements VehicleServiceInterface {
    @Override
    public List<Vehicle> findAllVehicles() {
        return List.of();
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        return List.of();
    }

    @Override
    public Vehicle findById(String id) {
        return null;
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        return null;
    }

    @Override
    public void removeVehicle(String vehicleId) {

    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        return false;
    }
}
