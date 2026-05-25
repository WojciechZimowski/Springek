package org.example;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.VehicleCategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.example.repositories.hibernate.RentalJpaRepository;
import org.example.repositories.hibernate.UserJpaRepository;
import org.example.repositories.hibernate.VehicleJpaRepository;
import org.example.repositories.impl.VehicleCategoryConfigJsonRepository;
import org.example.services.VehicleCategoryConfigService;
import org.example.services.hibernateService.AuthServiceInterface;
import org.example.services.hibernateService.RentalServiceInterface;
import org.example.services.hibernateService.UserServiceInterface;
import org.example.services.hibernateService.VehicleServiceInterface;
import org.example.services.hibernateService.impl.AuthService;
import org.example.services.hibernateService.impl.RentalService;
import org.example.services.hibernateService.impl.UserService;
import org.example.services.hibernateService.impl.VehicleService;


import java.lang.reflect.Type;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        
        Type categoryListType = new TypeToken<ArrayList<VehicleCategoryConfig>>(){}.getType();
        JsonFileStorage<VehicleCategoryConfig> configStorage = new JsonFileStorage<>("categories.json", categoryListType);
        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository(configStorage);
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        //integracja z jdbc
        System.out.println(">>> Uruchamianie systemu w trybie HIBERNATE");

        RentalJpaRepository rentalRepo = new RentalJpaRepository();
        VehicleJpaRepository vehicleRepo = new VehicleJpaRepository();
        UserJpaRepository userRepo = new UserJpaRepository();

         AuthServiceInterface authService = new AuthService(userRepo);
        VehicleServiceInterface vehicleService = new VehicleService(vehicleRepo, rentalRepo);
        UserServiceInterface userService = new UserService(userRepo, rentalRepo);
        RentalServiceInterface rentalService = new RentalService(rentalRepo, vehicleRepo, userRepo);

        UIForUser ui = new UIForUser(configService, vehicleService, userService, rentalService, authService);
        ui.start();
    }
}
