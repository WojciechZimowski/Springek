package org.example.repositories.hibernate.jpa;

import org.example.models.Vehicle;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("jpa")
public interface IVehicleJpaRepository extends JpaRepository<Vehicle,String> {

}
