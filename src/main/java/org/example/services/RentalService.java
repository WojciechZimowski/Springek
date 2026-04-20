package org.example.services;

import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.example.repositories.impl.RentalJsonRepository;

import java.util.List;

public class RentalService {
    private final RentalJsonRepository rentalJsonRepository;

    public RentalService(RentalJsonRepository rentalJsonRepository) {
        this.rentalJsonRepository = rentalJsonRepository;
    }

    public boolean vehicleHasActiveRental(String id){

    }
    public List<Rental> findUserRentals(String id){}
    public boolean rentVehicle(String uId, String vId){}
    public boolean returnVehicle(String uId){}
    public boolean findActiveRentalByUserId(String id){}
    public List<Rental> findAllRentals(){}
}
