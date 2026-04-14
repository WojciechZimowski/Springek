package org.example.repositories.impl;

import org.example.db.JsonFileStorage;
import org.example.models.Vehicle;
import org.example.repositories.IVehicleRepository;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IVehicleRepositoryImpl implements IVehicleRepository {
    private JsonFileStorage<Vehicle> storage;
    private List<Vehicle> vehicles =  new ArrayList<>();
    public IVehicleRepositoryImpl(JsonFileStorage<Vehicle> storage) {
        this.storage = storage;
        this.vehicles = storage.load();
    }





    @Override
    public List<Vehicle> findAll() {
        this.vehicles = storage.load();
        return this.vehicles.stream().map(Vehicle::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return storage.load().stream().filter(v->v.getId().equals(id)).findFirst().map(Vehicle::copy);
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        this.vehicles=storage.load();
        this.vehicles.removeIf(v->v.getId().equals(vehicle.getId()));
        Vehicle copy = vehicle.copy();
        this.vehicles.add(copy);
        storage.save(this.vehicles);
        return copy;
    }

    @Override
    public void deleteById(String id) {
        this.vehicles=storage.load();
        this.vehicles.removeIf(v->v.getId().equals(id));
        storage.save(this.vehicles);

    }
}
