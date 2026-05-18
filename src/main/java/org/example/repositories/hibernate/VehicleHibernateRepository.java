package org.example.repositories.hibernate;

import org.example.db.HibernateConfig;
import org.example.models.Vehicle;
import org.example.repositories.IVehicleRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateRepository implements IVehicleRepository {
    private Session session;
    @Override
    public List<Vehicle> findAll() {
        return session.createQuery("from Vehicle",Vehicle.class).list();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(session.get(Vehicle.class,id));
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return session.merge(vehicle);
    }

    @Override
    public void deleteById(String id) {
        Vehicle vehicle = session.get(Vehicle.class,id);
        if(vehicle!=null){
            session.remove(vehicle);
        }
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
