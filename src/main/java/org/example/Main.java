package org.example;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.*;
import org.example.repositories.*;
import org.example.repositories.impl.*;
import org.example.repositories.jdbc.*;
import org.example.services.*;


import java.lang.reflect.Type;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        Type categoryListType = new TypeToken<ArrayList<VehicleCategoryConfig>>(){}.getType();
        JsonFileStorage<VehicleCategoryConfig> configStorage = new JsonFileStorage<>("categories.json", categoryListType);
        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository(configStorage);
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator vehicleValidator = new VehicleValidator(configService);


        IUserRepository userRepo;
        IVehicleRepository vehicleRepo;
        RentalRepository rentalRepo;


        String mode = (args.length > 0) ? args[0].toLowerCase() : "json";

        if (mode.equals("jdbc")) {
            System.out.println(">>> Uruchamianie w trybie JDBC (Neon.tech)");
            userRepo = new UserJDBCRepository();
            vehicleRepo = new VehicleJDBCRepository();
            rentalRepo = new RentalJDBCRepository();
        } else {
            System.out.println(">>> Uruchamianie w trybie JSON (Pliki lokalne)");

            userRepo = new org.example.repositories.impl.UserJsonRepository(new JsonFileStorage<>("users.json", new TypeToken<ArrayList<User>>(){}.getType()));
            vehicleRepo = new org.example.repositories.impl.VehicleJsonRepository(new JsonFileStorage<>("vehicles.json", new TypeToken<ArrayList<Vehicle>>(){}.getType()));
            rentalRepo = new org.example.repositories.impl.RentalJsonRepository(new JsonFileStorage<>("rentals.json", new TypeToken<ArrayList<Rental>>(){}.getType()));
        }


        AuthService authService = new AuthService(userRepo);
        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);
        UserService userService = new UserService(userRepo, rentalRepo);
        RentalService rentalService = new RentalService(rentalRepo);

        UIForUser ui = new UIForUser(vehicleService, userService, rentalService, configService, authService);
        ui.start();
    }
}