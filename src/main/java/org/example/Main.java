package org.example;


import org.example.db.JsonFileStorage;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import org.example.repositories.IUserRepository;
import org.example.repositories.IVehicleRepository;
import org.example.repositories.RentalRepository;
import org.example.repositories.impl.VehicleJsonRepository;
import org.example.repositories.impl.RentalJsonRepository;
import org.example.repositories.impl.UserJsonRepository;
import org.example.services.AuthService;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {


        Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
        Type vehicleListType = new TypeToken<ArrayList<Vehicle>>(){}.getType();
        Type rentalListType = new TypeToken<ArrayList<Rental>>(){}.getType();


        JsonFileStorage<User> userStorage = new JsonFileStorage<>("users.json", userListType);
        JsonFileStorage<Vehicle> vehicleStorage = new JsonFileStorage<>("vehicles.json", vehicleListType);
        JsonFileStorage<Rental> rentalStorage = new JsonFileStorage<>("rentals.json", rentalListType);


        IUserRepository userRepo = new UserJsonRepository(userStorage);
        IVehicleRepository vehicleRepo = new VehicleJsonRepository(vehicleStorage);
        RentalRepository rentalRepo = new RentalJsonRepository(rentalStorage);


        AuthService authService = new AuthService(userRepo);


        UIForUser ui = new UIForUser(vehicleRepo, userRepo,rentalRepo,authService);
        ui.start();


    }
}