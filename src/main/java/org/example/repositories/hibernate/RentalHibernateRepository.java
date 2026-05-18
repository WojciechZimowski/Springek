package org.example.repositories.hibernate;

import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class RentalHibernateRepository implements RentalRepository {
    private Session session;
    @Override
    public List<Rental> findAll() {
        return session.createQuery("from Rental",Rental.class).list();
    }

    @Override
    public Optional<Rental> findById(String id) {
        return Optional.ofNullable(session.get(Rental.class,id));
    }

    @Override
    public Rental save(Rental rental) {
        return session.merge(rental);
    }

    @Override
    public void deleteById(String id) {
        Rental rental = session.get(Rental.class,id);
        if (rental != null) {
            session.remove(rental);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return session.createQuery("FROM Rental r WHERE r.vehicle.id = :vId AND r.returnDateTime IS NULL", Rental.class)
                .setParameter("vId", vehicleId)
                .uniqueResultOptional();
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
