package org.example;


public class Main {
    public static void main(String[] args) {
        IVehicleRepository iv = new IVehicleRepositoryImpl();

        UIForUser ui = new UIForUser(iv);
        ui.start();

    }
}