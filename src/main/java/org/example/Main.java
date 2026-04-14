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
import org.example.repositories.impl.IVehicleRepositoryImpl;
import org.example.repositories.impl.RentalJsonRepository;
import org.example.repositories.impl.UserRepository;
import org.example.services.AuthService;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        // 1. Definiujemy TYPY dla GSONa (żeby wiedział, jak czytać listy obiektów)
        Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
        Type vehicleListType = new TypeToken<ArrayList<Vehicle>>(){}.getType();
        Type rentalListType = new TypeToken<ArrayList<Rental>>(){}.getType();


        JsonFileStorage<User> userStorage = new JsonFileStorage<>("users.json", userListType);
        JsonFileStorage<Vehicle> vehicleStorage = new JsonFileStorage<>("vehicles.json", vehicleListType);
        JsonFileStorage<Rental> rentalStorage = new JsonFileStorage<>("rentals.json", rentalListType);


        IUserRepository userRepo = new UserRepository(userStorage);
        IVehicleRepository vehicleRepo = new IVehicleRepositoryImpl(vehicleStorage);
        RentalRepository rentalRepo = new RentalJsonRepository(rentalStorage);


        AuthService authService = new AuthService(userRepo);


        UIForUser ui = new UIForUser(vehicleRepo, userRepo,rentalRepo,authService);
        ui.start();


    }
}