package org.example;


import org.example.repositories.IVehicleRepository;
import org.example.repositories.impl.IVehicleRepositoryImpl;

public class Main {
    public static void main(String[] args) {
        IVehicleRepository iv = new IVehicleRepositoryImpl();

        UIForUser ui = new UIForUser(iv);
        ui.start();

    }
}