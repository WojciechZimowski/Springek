package org.example.repositories.hibernate;

import org.example.db.HibernateConfig;
import org.example.models.Vehicle;
import org.example.repositories.IVehicleRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateRepository implements IVehicleRepository {
    @Override
    public List<Vehicle> findAll() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("from Vehicle",Vehicle.class).list();
        }
    }

    @Override
    public Optional<Vehicle> findById(String id) {

        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Vehicle.class,id));
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        Transaction tx = null;try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Vehicle savedVehicle = (Vehicle) session.save(vehicle);
            tx.commit();
            return savedVehicle;
        }catch(Exception ex){
            if(tx!=null){tx.rollback();
            throw new RuntimeException("Błąd zapisu pojazdu",ex)
            ;}
        }

    }

    @Override
    public void deleteById(String id) {
        Transaction tx = null;try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Vehicle vehicle = (Vehicle) session.get(Vehicle.class,id);
            if(vehicle!=null){
                session.remove(vehicle);
            }
            tx.commit();
        }catch(Exception ex){
            if(tx!=null){tx.rollback();
            throw new RuntimeException("Błąd usuwania pojazdu",ex);}
        }
    }

    public void setSession(Session session) {
    }
}
