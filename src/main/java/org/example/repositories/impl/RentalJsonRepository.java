package org.example.repositories.impl;

import org.example.db.JsonFileStorage;
import org.example.models.Rental;
import org.example.models.User;
import org.example.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("json")
public class RentalJsonRepository implements RentalRepository {
    private JsonFileStorage<Rental> storage;
    private List<Rental> rentals;
    public RentalJsonRepository(@Value("${carrent.json.vehicles-file}") String filename
    ) {
        this.storage = new JsonFileStorage<Rental> (filename, Rental.class);
        this.rentals = new ArrayList<>(storage.load());
    }
    @Override
    public List<Rental> findAll() {
        this.rentals = storage.load();
        return this.rentals.stream().map(Rental::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<Rental> findById(String id) {
        return storage.load().stream().filter(r -> r.getId().equals(id)).findFirst().map(Rental::copy);
    }

    @Override
    public Rental save(Rental rental) {
        this.rentals = storage.load();

        this.rentals.removeIf(r -> r.getId().equals(rental.getId()));

        Rental copy = rental.copy();
        this.rentals.add(copy);

        storage.save(this.rentals);
        return copy;
    }

    @Override
    public void deleteById(String id) {
        this.rentals = storage.load();
        this.rentals.removeIf(r -> r.getId().equals(id));
        storage.save(this.rentals);
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return storage.load().stream().filter(r->r.getVehicleId().equals(vehicleId)).
                filter(r->r.getReturnDate()==null).findFirst().map(Rental::copy);
    }
}
