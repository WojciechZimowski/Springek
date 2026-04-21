package org.example;


import org.example.db.JsonFileStorage;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import org.example.models.VehicleCategoryConfig;
import org.example.repositories.IUserRepository;
import org.example.repositories.IVehicleRepository;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.example.repositories.impl.VehicleCategoryConfigJsonRepository;
import org.example.repositories.impl.VehicleJsonRepository;
import org.example.repositories.impl.RentalJsonRepository;
import org.example.repositories.impl.UserJsonRepository;
import org.example.services.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {


        Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
        Type vehicleListType = new TypeToken<ArrayList<Vehicle>>(){}.getType();
        Type rentalListType = new TypeToken<ArrayList<Rental>>(){}.getType();
        Type categoryListType = new TypeToken<ArrayList<VehicleCategoryConfig>>(){}.getType();


        JsonFileStorage<User> userStorage = new JsonFileStorage<>("users.json", userListType);
        JsonFileStorage<Vehicle> vehicleStorage = new JsonFileStorage<>("vehicles.json", vehicleListType);
        JsonFileStorage<Rental> rentalStorage = new JsonFileStorage<>("rentals.json", rentalListType);
        JsonFileStorage<VehicleCategoryConfig> configStorage = new JsonFileStorage<>("categories.json", categoryListType);UserJsonRepository userRepo = new UserJsonRepository(userStorage);
        VehicleJsonRepository vehicleRepo = new VehicleJsonRepository(vehicleStorage);
        RentalJsonRepository rentalRepo = new RentalJsonRepository(rentalStorage);

        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository(configStorage);


        AuthService authService = new AuthService(userRepo);


        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator vehicleValidator = new VehicleValidator(configService);


        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);
        UserService userService = new UserService(userRepo, rentalRepo);
        RentalService rentalService = new RentalService(rentalRepo);


        UIForUser ui = new UIForUser(vehicleService,userService,rentalService,configService,authService);
        ui.start();


    }
}