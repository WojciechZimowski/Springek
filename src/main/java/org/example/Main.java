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

        IUserRepository userRepo = new UserJDBCRepository();
        IVehicleRepository vehicleRepo = new VehicleJDBCRepository();
        RentalRepository rentalRepo = new RentalJDBCRepository();

        AuthService authService = new AuthService(userRepo);
        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);
        UserService userService = new UserService(userRepo, rentalRepo);
        RentalService rentalService = new RentalService(rentalRepo);


        UIForUser ui = new UIForUser(vehicleService, userService, rentalService, configService, authService);
        ui.start();
    }
}