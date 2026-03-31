package org.example;

import java.io.Serializable;

public abstract  class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;

    private String brand;
    private String model;
    private Integer year;

    private Integer price;//warto zrobic klase do waluty
    private boolean rented;

    public Vehicle(  String id,String brand, String model, Integer year, Integer price, boolean rented) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;

    }
    public Vehicle(Vehicle copy){
        this.id=copy.id;
        this.brand=copy.brand;
        this.model=copy.model;
        this.year= copy.year;
        this.price=copy.price;
        this.rented=copy.rented;

    }

    public String getId() {
        return id;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public String toCSV(){


        return String.format("%s;%s;%s;%d;%d;%b",id,brand,model,year,price,rented);
    }

    @Override
    public String toString() {
        return
                "ID:'" + id + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", rented=" + rented
                ;
    }

     public abstract Vehicle copy();
}
