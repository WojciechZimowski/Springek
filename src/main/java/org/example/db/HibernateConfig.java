package org.example.db;

import lombok.Getter;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfig {
    @Getter
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            configuration.setProperty("hibernate.connection.url", System.getenv("DB_URL"));
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Vehicle.class);
            configuration.addAnnotatedClass(Rental.class);

            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
}
