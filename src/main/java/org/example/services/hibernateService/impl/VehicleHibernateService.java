package org.example.services.hibernateService.impl;

import org.example.db.HibernateConfig;
import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.hibernate.RentalHibernateRepository;
import org.example.repositories.hibernate.VehicleHibernateRepository;
import org.example.services.hibernateService.VehicleServiceInterface;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateService implements VehicleServiceInterface {
    private final VehicleHibernateRepository vehicleRepository;
    private final RentalHibernateRepository rentalRepository;

    public VehicleHibernateService(VehicleHibernateRepository vehicleRepository, RentalHibernateRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM Vehicle", Vehicle.class).getResultList();}

    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Vehicle v WHERE v.id NOT IN " +
                                    "(SELECT r.vehicle.id FROM Rental r WHERE r.returnDateTime IS NULL OR r.returnDateTime = '')",
                            Vehicle.class)
                    .getResultList();
        }

    }

    @Override
    public Vehicle findById(String id) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepository.setSession(session);
            return vehicleRepository.findById(id).orElseThrow(()-> new RuntimeException("Nie można znaleźć pojazdu"));
        }
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            vehicleRepository.setSession(session);
            Vehicle saved = vehicleRepository.save(vehicle);
            tx.commit();
            return saved;
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }

    }

    @Override
    public void removeVehicle(String vehicleId) {
        Transaction tx = null;
        boolean isRented = false;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Optional<Rental> activeRental = session.createQuery(
                            "FROM Rental r WHERE r.vehicle.id = :vId AND (r.returnDateTime IS NULL OR r.returnDateTime = '')", Rental.class)
                    .setParameter("vId", vehicleId)
                    .uniqueResultOptional();

            if (isVehicleRented(vehicleId)) {
                isRented = true;
            } else {
                Vehicle vehicle = session.get(Vehicle.class, vehicleId);
                if (vehicle != null) {
                    session.remove(vehicle);
                } else {
                    throw new IllegalArgumentException("Nie znaleziono pojazdu o podanym ID: " + vehicleId);
                }
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }

        if (isRented) {
            throw new IllegalStateException("Pojazd jest aktualnie wypożyczony i nie można ga usunąć!");
        }
    }



    @Override
    public boolean isVehicleRented(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            rentalRepository.setSession(session);
            return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }
}
