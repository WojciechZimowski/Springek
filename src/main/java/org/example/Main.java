package org.example;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.VehicleCategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.example.repositories.hibernate.RentalHibernateRepository;
import org.example.repositories.hibernate.UserHibernateRepository;
import org.example.repositories.hibernate.VehicleHibernateRepository;
import org.example.repositories.impl.VehicleCategoryConfigJsonRepository;
import org.example.services.VehicleCategoryConfigService;
import org.example.services.VehicleValidator;
import org.example.services.hibernateService.AuthServiceInterface;
import org.example.services.hibernateService.RentalServiceInterface;
import org.example.services.hibernateService.UserServiceInterface;
import org.example.services.hibernateService.VehicleServiceInterface;
import org.example.services.hibernateService.impl.AuthHibernateService;
import org.example.services.hibernateService.impl.RentalHibernateService;
import org.example.services.hibernateService.impl.UserHibernateService;
import org.example.services.hibernateService.impl.VehicleHibernateService;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        Type categoryListType = new TypeToken<ArrayList<VehicleCategoryConfig>>(){}.getType();
        JsonFileStorage<VehicleCategoryConfig> configStorage = new JsonFileStorage<>("categories.json", categoryListType);
        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository(configStorage);
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);

        System.out.println(">>> Uruchamianie systemu w trybie HIBERNATE");

        RentalHibernateRepository rentalRepo = new RentalHibernateRepository();
        VehicleHibernateRepository vehicleRepo = new VehicleHibernateRepository();
        UserHibernateRepository userRepo = new UserHibernateRepository();

         AuthServiceInterface authService = new AuthHibernateService(userRepo);
        VehicleServiceInterface vehicleService = new VehicleHibernateService(vehicleRepo, rentalRepo);
        UserServiceInterface userService = new UserHibernateService(userRepo, rentalRepo);
        RentalServiceInterface rentalService = new RentalHibernateService(rentalRepo, vehicleRepo, userRepo);

        UIForUser ui = new UIForUser(configService, vehicleService, userService, rentalService, authService);
        ui.start();
    }
}