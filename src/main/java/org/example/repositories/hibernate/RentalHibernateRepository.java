package org.example.repositories.hibernate;

import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.example.repositories.hibernate.jpa.RentalJpaRepository;
import org.hibernate.Session;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class RentalHibernateRepository implements RentalRepository {
    private final RentalJpaRepository rentalJpaRepository;

    public RentalHibernateRepository(RentalJpaRepository rentalJpaRepository) {
        this.rentalJpaRepository = rentalJpaRepository;
    }

    @Override
    public List<Rental> findAll() {
        return rentalJpaRepository.findAll();
    }

    @Override
    public Optional<Rental> findById(String id) {
        return rentalJpaRepository.findById(id);
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        }
        return rentalJpaRepository.save(rental);
    }

    @Override
    public void deleteById(String id) {
        rentalJpaRepository.deleteById(id);
        }


    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return rentalJpaRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
    }


}
