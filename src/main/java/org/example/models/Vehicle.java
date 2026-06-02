package org.example.models;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@EqualsAndHashCode(of="id")
@ToString
@Table(name="vehicle")
public  class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable=false,unique=true)
    private String id;

    private String category;
    private String brand;
    private String model;
    private Integer year;
    private String plate;
    @Column(columnDefinition = "NUMERIC")
    private Double price;//warto zrobic klase do waluty

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String,Object> attributes= new HashMap<>();

    @Builder
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




}

