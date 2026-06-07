package org.example.services.hibernateService.impl;

import org.example.db.HibernateConfig;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.IUserRepository;
import org.example.repositories.IVehicleRepository;
import org.example.repositories.RentalRepository;
import org.example.repositories.hibernate.RentalJpaRepository;
import org.example.repositories.hibernate.UserJpaRepository;
import org.example.repositories.hibernate.VehicleJpaRepository;
import org.example.services.hibernateService.RentalServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RentalService implements RentalServiceInterface {
    private final RentalRepository rentalRepository;
    private final IVehicleRepository vehicleRepository;
    private final IUserRepository userRepository;

    public RentalService(RentalRepository rentalRepository,
                         IVehicleRepository vehicleHibernateService,
                         IUserRepository userHibernateService) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleHibernateService;
        this.userRepository = userHibernateService;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        if (userHasActiveRental(userId)) {
            throw new IllegalStateException("Masz już aktywne wypożyczenie!");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym ID"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o podanym ID"));

        if (vehicleHasActiveRental(vehicleId)) {
            throw new IllegalStateException("Ten pojazd jest już wypożyczony przez kogoś innego!");
        }


        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .vehicle(vehicle)
                .rentDateTime(LocalDateTime.now().toString())
                .build();

        return rentalRepository.save(rental);
    }


    @Override
    public Rental returnVehicle(String userId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {


            Rental rental = findActiveRentalByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie masz aktualnie żadnego wypożyczonego pojazdu"));

            rental.setReturnDate(LocalDateTime.now().toString());
            return rentalRepository.save(rental);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(userId) && r.isActive())
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(userId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }


}