package org.example;

public class Motorcycle extends Vehicle {
    private MotorcycleCategory kategoria;

    public Motorcycle(String id, String brand, String model, Integer year, Integer price, boolean rented, String kategoria) {
        super(id, brand, model, year, price, rented);
        this.kategoria = MotorcycleCategory.valueOf(kategoria.toUpperCase());
    }
    public Motorcycle(Motorcycle copy){
        super(copy); this.kategoria = copy.kategoria;
    }

    public String toCSV( ) {
        return "MOTORCYCLE;" + super.toCSV() + ";" + kategoria;
    }

    @Override
    public String toString() {
        return "Motorcycle "+ super.toString() +
                " category='" + kategoria + '\'';

    }
    @Override
    public Vehicle copy() {
        return new  Motorcycle(this);
    }


}
