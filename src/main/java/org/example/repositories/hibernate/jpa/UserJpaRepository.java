package org.example.repositories.hibernate.jpa;

import org.example.models.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("jpa")
public interface UserJpaRepository extends JpaRepository<User,String> {

}
