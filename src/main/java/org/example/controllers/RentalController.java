package org.example.controllers;

import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.services.hibernateService.RentalServiceInterface;
import org.example.services.hibernateService.VehicleServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private  final RentalServiceInterface rentalService;

    public RentalController(RentalServiceInterface rentalService) {
        this.rentalService = rentalService;
    }

    //ktos chce zwrocic pojazd-nie ma zworconego
    //usuwanie pojazdu wyborzycoznego
    //osoba chce wyporzyczyc 2 pojazdy(w nowej logice nie powinna)
    //błedy wyswietlac czy cos takiego
    //zwrocenie pojazdu
    //delete pojazd i rental ma sie usuwać, kaskadowo
    //      .
    //    ___\_______
    //   / |____|____\
    //  |       |    """"""|,
    // ==--(o)--------(o)---'


    //   __________________________________
    //  | |
    //  | |____
    //  |
    // ==--(o)----------'
    @GetMapping
    public List<Rental> listAll(

    ) {
        return rentalService.findAllRentals();
    }

    @PostMapping("/users/{userId}/rent/{vehicleId}")
    public Rental rentVehicle(@PathVariable String userId, @PathVariable String vehicleId) {
        return rentalService.rentVehicle(userId, vehicleId);
    }
        @PostMapping("/users/{userId}/return")
        public Rental returnVehicle(@PathVariable String userId) {
            return rentalService.returnVehicle(userId);
        }
}
