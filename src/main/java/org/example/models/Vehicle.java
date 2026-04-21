package org.example.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@Builder
public  class Vehicle implements Serializable {

    private String id;
    private String category;
    private String brand;
    private String model;
    private Integer year;
    private String plate;
    private Double price;//warto zrobic klase do waluty


    private Map<String,Object> attributes;

    public Vehicle(  String id,String category,String brand, String model, Integer year,String plate, Double price, Map<String,Object> attributes) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.attributes = attributes!=null?new HashMap<>(attributes):new HashMap<>();

    }
    public Vehicle copy(){
        return Vehicle.builder().id(this.id)
                .category(this.category)
                .brand(this.brand)
                .model(this.model)
                .year(this.year)
                .plate(this.plate)
                .price(this.price)

                .attributes(this.attributes != null ? new HashMap<>(this.attributes) : new HashMap<>())
                .build();


    }




    public Map<String,Object> getAttributes() {
        return attributes != null? Collections.unmodifiableMap(attributes):Collections.emptyMap();
    }
    public Object getAttribute(String attributeName) {
            if(attributes == null) return null;
            return attributes.get(attributeName);
    }
    public void addAttribute(String key, Object value) {
        if(this.attributes == null){
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }
    public void removeAtribute(String key) {
        if(attributes != null){
            this.attributes.remove(key);
        }
    }

    @Override
    public String toString() {
       StringBuilder builder = new StringBuilder();
       builder.append(String.format("ID: %s | [%s] %s %s (%d) | Rejestracja: %s| Cena: %.2f zł",
               id, category, brand, model, year, plate, price));
       if(attributes!=null && !attributes.isEmpty()) {
           builder.append("|Dodatki:").append(attributes);
       }
       return builder.toString();
    }


}

