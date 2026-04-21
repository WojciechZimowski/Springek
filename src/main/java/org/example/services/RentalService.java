package org.example.services;

import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.example.repositories.impl.RentalJsonRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RentalService {
    private final RentalJsonRepository rentalJsonRepository;

    public RentalService(RentalJsonRepository rentalJsonRepository) {
        this.rentalJsonRepository = rentalJsonRepository;
    }

    public void rentVehicle(String uId, String vId) {
        if (rentalJsonRepository.findByVehicleIdAndReturnDateIsNull(vId).isPresent()) {
            throw new RuntimeException("Pojazd jest już wypożyczony.");
        }

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .userId(uId)
                .vehicleId(vId)
                .rentDateTime(LocalDateTime.now().toString())
                .build();
        rentalJsonRepository.save(rental);
    }

    public void returnVehicle(String vId) {
        rentalJsonRepository.findByVehicleIdAndReturnDateIsNull(vId).ifPresentOrElse(r -> {
            r.setReturnDateTime(LocalDateTime.now().toString());
            rentalJsonRepository.save(r);
        }, () -> { throw new RuntimeException("Nie znaleziono aktywnego wypożyczenia dla tego auta."); });
    }
}
