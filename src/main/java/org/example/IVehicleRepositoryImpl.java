package org.example;

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class IVehicleRepositoryImpl implements IVehicleRepository{
    private List<Vehicle> vehicles =  new ArrayList<>();
    private final String path = "vehicles.csv";
    private final Map<String, Function<String[], Vehicle>> creators = new HashMap<>();


    public IVehicleRepositoryImpl() {
        creators.put("CAR", p -> new Car(p[1], p[2], p[3], Integer.parseInt(p[4]), (int)Double.parseDouble(p[5]), Boolean.parseBoolean(p[6])));

        creators.put("MOTORCYCLE", p -> new Motorcycle(p[1], p[2], p[3], Integer.parseInt(p[4]), (int)Double.parseDouble(p[5]), Boolean.parseBoolean(p[6]), p[7]));
        load();

    }
    @Override
    public boolean rentVehicle(String id) {
        for (Vehicle v : vehicles){
            if(v.getId().equals(id) && !v.isRented()){
                v.setRented(true);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean returnVehicle(String id) {
            for (Vehicle v : vehicles) {
                if (v.getId().equals(id) && v.isRented()) {
                    v.setRented(false);
                    save();
                    return true;
                }

            }
        return false;
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> copy =  new ArrayList<>();
        for(Vehicle v : this.vehicles){
            copy.add(v.copy());
        }
        return copy;
    }

    @Override
    public Vehicle getVehicle(String id) {
        return vehicles.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public boolean add(Vehicle vehicle) {
        Vehicle v = vehicle.copy();
        vehicles.add(v);
        save();
        return false;
    }

    @Override
    public boolean remove(String id) {
        vehicles.removeIf(v -> v.getId().equals(id));
        save();
        return false;
    }


    @Override
    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(path))) {
            for (Vehicle v : vehicles) {
                pw.println(v.toCSV());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        File file = new File(path);
        this.vehicles.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                Vehicle v = creators.getOrDefault(parts[0], p -> null).apply(parts);

                Optional.ofNullable(v).ifPresent(this.vehicles::add);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

}
