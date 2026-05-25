package org.example.repositories;

import org.example.models.VehicleCategoryConfig;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface VehicleCategoryConfigRepository {
    List<VehicleCategoryConfig> findAll();
    Optional<VehicleCategoryConfig> findByCategory(String category);
}


