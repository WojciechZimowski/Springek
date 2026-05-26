package org.example.config;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.VehicleCategoryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Type;
import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public JsonFileStorage<VehicleCategoryConfig> vehicleCategoryStorage() {

        String fileName = "categories.json";


        Type type = new TypeToken<List<VehicleCategoryConfig>>(){}.getType();

        return new JsonFileStorage<>(fileName, type);
    }
}
