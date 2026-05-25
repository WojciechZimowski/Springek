package org.example.services.jdbcService;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RentalService {
    private final RentalRepository rentalJsonRepository;

    public RentalService(RentalRepository rentalJsonRepository) {
        this.rentalJsonRepository = rentalJsonRepository;
    }

    public void rentVehicle(String uId, String vId) {
        if (rentalJsonRepository.findByVehicleIdAndReturnDateIsNull(vId).isPresent()) {
            throw new RuntimeException("Pojazd jest już wypożyczony.");
        }



        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .user(User.builder().id(uId).build())
                .vehicle(Vehicle.builder().id(vId).build())
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
    public boolean isVehicleRented(String vId) {
        return rentalJsonRepository.findByVehicleIdAndReturnDateIsNull(vId).isPresent();
    }
    public List<Rental> findActiveRentalsByUser(String userId) {
        return rentalJsonRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .toList();
    }
    public List<Rental> findAllRentalsByUserId(String userId) {
        return rentalJsonRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .toList();
    }
}
