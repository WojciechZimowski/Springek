package org.example.services;

import org.example.models.Vehicle;
import org.example.models.VehicleCategoryConfig;

import java.util.Map;

public class VehicleValidator {
    private final VehicleCategoryConfigService configService;

    public VehicleValidator(VehicleCategoryConfigService configService) {
        this.configService = configService;
    }

    public void validate(Vehicle vehicle) {

        if (vehicle.getBrand() == null || vehicle.getBrand().isBlank())
            throw new RuntimeException("Marka nie może być pusta!");
        if(!configService.categoryExists(vehicle.getCategory())){
            throw new RuntimeException("BŁĄD: Kategoria "+vehicle.getCategory()+" Nie istnieje");
        }


        VehicleCategoryConfig config = configService.getByCategory(vehicle.getCategory());
        Map<String, String> requiredAttrs = config.getAttributes();
        Map<String, Object> vehicleAttrs = vehicle.getAttributes();


        for (Map.Entry<String, String> entry : requiredAttrs.entrySet()) {
            String attrName = entry.getKey();
            String expectedType = entry.getValue();

            if (!vehicleAttrs.containsKey(attrName)) {
                throw new RuntimeException("Brak atrybutu [" + attrName + "] dla kategorii " + vehicle.getCategory());
            }

            Object value = vehicleAttrs.get(attrName);
            validateType(attrName, value, expectedType);
        }
    }

    private void validateType(String name, Object value, String type) {
        if (type.equals("integer")) {
            try {

                if (value instanceof String) {
                    Integer.parseInt((String) value);
                } else if (!(value instanceof Integer)) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new RuntimeException("Atrybut [" + name + "] musi być liczbą (integer)!");
            }
        }
        if (type.equals("string") && (value == null || value.toString().isBlank())) {
            throw new RuntimeException("Atrybut [" + name + "] nie może być pusty!");
        }
    }
}
