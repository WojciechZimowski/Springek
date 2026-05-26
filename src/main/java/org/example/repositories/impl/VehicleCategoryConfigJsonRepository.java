package org.example.repositories.impl;

import org.example.db.JsonFileStorage;
import org.example.models.VehicleCategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public class VehicleCategoryConfigJsonRepository implements VehicleCategoryConfigRepository {
    private final JsonFileStorage<VehicleCategoryConfig> jsonFileStorage;

    public VehicleCategoryConfigJsonRepository(JsonFileStorage<VehicleCategoryConfig> jsonFileStorage) {
        this.jsonFileStorage = jsonFileStorage;
    }

    @Override
    public List<VehicleCategoryConfig> findAll() {
        return jsonFileStorage.load();
    }

    @Override
    public Optional<VehicleCategoryConfig> findByCategory(String category) {
        return jsonFileStorage.load().stream()
                .filter(c -> c.getCategory().equalsIgnoreCase(category))
                .findFirst(); }
}
