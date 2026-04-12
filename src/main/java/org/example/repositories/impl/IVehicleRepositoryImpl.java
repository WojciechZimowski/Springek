package org.example.repositories.impl;

import org.example.db.JsonFileStorage;
import org.example.models.Vehicle;
import org.example.repositories.IVehicleRepository;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IVehicleRepositoryImpl implements IVehicleRepository {
    private JsonFileStorage<Vehicle> storage;
    private List<Vehicle> vehicles =  new ArrayList<>();
    public IVehicleRepositoryImpl(JsonFileStorage<Vehicle> storage) {
        this.storage = storage;
        this.vehicles = storage.load();
    }



//    public IVehicleRepositoryImpl() {
//        creators.put("CAR", p -> new Car(p[1], p[2], p[3], Integer.parseInt(p[4]), (int)Double.parseDouble(p[5]), Boolean.parseBoolean(p[6])));
//
//        creators.put("MOTORCYCLE", p -> new Motorcycle(p[1], p[2], p[3], Integer.parseInt(p[4]), (int)Double.parseDouble(p[5]), Boolean.parseBoolean(p[6]), p[7]));
//        File file = new File(path);
//        this.vehicles.clear();
//        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split(";");
//                Vehicle v = creators.getOrDefault(parts[0], p -> null).apply(parts);
//
//                Optional.ofNullable(v).ifPresent(this.vehicles::add);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//    @Override
//    public boolean rentVehicle(String id) {
//        for (Vehicle v : vehicles){
//            if(v.getId().equals(id) && !v.isRented()){
//                v.setRented(true);
//                try (PrintWriter pw = new PrintWriter(new FileOutputStream(path))) {
//                    for (Vehicle ve : vehicles) {
//                        pw.println(ve.toCSV());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean returnVehicle(String id) {
//            for (Vehicle v : vehicles) {
//                if (v.getId().equals(id) && v.isRented()) {
//                    v.setRented(false);
//                    try (PrintWriter pw = new PrintWriter(new FileOutputStream(path))) {
//                        for (Vehicle ve : vehicles) {
//                            pw.println(ve.toCSV());
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    return true;
//                }
//
//            }
//        return false;
//    }
//
//    @Override
//    public List<Vehicle> getVehicles() {
//        List<Vehicle> copy =  new ArrayList<>();
//        for(Vehicle v : this.vehicles){
//            copy.add(v.copy());
//        }
//        return copy;
//    }
//
//    @Override
//    public Vehicle getVehicle(String id) {
//        return vehicles.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
//    }
//
//    @Override
//    public boolean add(Vehicle vehicle) {
//
//        for (Vehicle vec : vehicles) {
//            if(vec.getId().equals(vehicle.getId())){
//                System.out.println("Błąd");
//                return false;
//            }
//        }
//        Vehicle v = vehicle.copy();
//        vehicles.add(v);
//        try (PrintWriter pw = new PrintWriter(new FileOutputStream(path))) {
//
//            for (Vehicle ve : vehicles) {
//
//                pw.println(ve.toCSV());
//            }
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    @Override
//    public boolean remove(String id) {
//        vehicles.removeIf(v -> v.getId().equals(id));
//        try (PrintWriter pw = new PrintWriter(new FileOutputStream(path))) {
//            for (Vehicle ve : vehicles) {
//                pw.println(ve.toCSV());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


    @Override
    public List<Vehicle> findAll() {
        this.vehicles = storage.load();
        return this.vehicles.stream().map(Vehicle::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return storage.load().stream().filter(v->v.getId().equals(id)).findFirst().map(Vehicle::copy);
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        this.vehicles=storage.load();
        this.vehicles.removeIf(v->v.getId().equals(vehicle.getId()));
        Vehicle copy = vehicle.copy();
        this.vehicles.add(copy);
        storage.save(this.vehicles);
        return copy;
    }

    @Override
    public void deleteById(String id) {
        this.vehicles=storage.load();
        this.vehicles.removeIf(v->v.getId().equals(id));
        storage.save(this.vehicles);

    }
}
