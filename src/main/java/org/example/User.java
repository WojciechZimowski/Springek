package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class User {
    private Role role;
    private final Map<String, Function<String[], Vehicle>> creators = new HashMap<>();
    private String login;
    private String password;
    private String rentedVehicle;



    public User(String login, String password, String role, String rentedVehicle) {
        this.login = login;
        this.password = password;
        this.role = Role.valueOf(role.toUpperCase());
        this.rentedVehicle = rentedVehicle;
    }
    public User(User copy){
        this.login = copy.login;
        this.password = copy.password;
        this.role = copy.role;
        this.rentedVehicle = copy.rentedVehicle;
    }

    public Map<String, Function<String[], Vehicle>> getCreators() {
        return creators;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role.toString();
    }

    public String getRentedVehicle() {
        return rentedVehicle;
    }

    public void setRentedVehicle(String rentedVehicle) {
        this.rentedVehicle = rentedVehicle;
    }
    public String toCSV(){
        return String.format("%s;%s;%s;%s",login,password,role,(rentedVehicle==null||rentedVehicle.isEmpty() ?"":rentedVehicle));
    }
    public String toString(){
        return "Login: " + login +
                " | Rola: " + role +
                " | Wypożyczone ID: " + (rentedVehicle == null || rentedVehicle.isEmpty() ? "Brak" : rentedVehicle);
    }


    public String getLogin() {
        return login;
    }
}

