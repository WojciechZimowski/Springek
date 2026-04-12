package org.example;

import org.example.models.*;
import org.example.repositories.IUserRepository;
import org.example.repositories.IVehicleRepository;
import org.example.repositories.impl.UserRepository;
import org.example.services.Authentication;
import org.example.services.Hasher;

import java.util.List;
import java.util.Scanner;

public class UIForUser {
    private final IVehicleRepository repo;
    private final Scanner scanner;
    private IUserRepository userRepo;
    private User user;
    private Authentication authentication;
    private static Hasher hasher = new Hasher();


    public  UIForUser(IVehicleRepository repo) {
        this.repo = repo;
        this.scanner = new Scanner(System.in);
        this.userRepo = new UserRepository();
        this.authentication=new Authentication(userRepo);

    }
    private boolean adminMenu(){
        System.out.println("\n--WITAJ W MENU ADMINA");
        System.out.println("1. Lista pojazdów");
        System.out.println("2. Dodaj pojazd");
        System.out.println("3. Usuń pojazd");
        System.out.println("4. Lista użytkowników i ich wypożyczeń");
        System.out.println("5.Usuń użytkownika");
        System.out.println("6. Wyjdź");

        String adminInput = scanner.nextLine();
        if(adminInput.equals("1")){
            showVehicles();
        }
        else if(adminInput.equals("2")){
            System.out.println("Podaj dane pojazdu w formacie CSV");
            String csvInput = scanner.nextLine();
            boolean ok = false;
            String[] parts = csvInput.split(";");

            if(parts[0].equals("CAR")){
                ok=repo.add(new Car(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Boolean.parseBoolean(parts[6])));
            }else if(parts[0].equals("MOTORCYCLE")){
                String category = parts[7];

                ok=repo.add(new Motorcycle(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Boolean.parseBoolean(parts[6]), parts[7]));
            }
            if(ok) {
                System.out.println("Pojazd dodany");
            }
            else {
                System.out.println("Błąd id");
            }
        }
        else if(adminInput.equals("3")){
            System.out.println("Podaj id pojazd");
            String idInput = scanner.nextLine();
            repo.remove(idInput);
            System.out.println("Pojazd usunięty");


        }
        else if(adminInput.equals("4")){
            List<User> users = userRepo.getUsers();
            for(User u : users){
                System.out.println(u);
                String vid = u.getRentedVehicle();
                if(vid != null && ! vid.isEmpty()){
                    Vehicle v = repo.getVehicle(vid);
                    if(v != null){
                        System.out.println("Dane pojazdu: "+v.toString());
                    }else{
                        System.out.println("Brak danych pojazdu: "+vid);
                    }
                }else{
                    System.out.println("Brak pojazdów");
                }
            }
        }
        else if(adminInput.equals("5")){
            System.out.println("Usuń użytkownika");
            String userName = scanner.nextLine();
            userRepo.deleteUser(userName);
            System.out.println("Użytkownik usunięty");
        }
        else if(adminInput.equals("6")){
            return false;
        }
        return true;

    }

    private boolean userMenu(){
        System.out.println("\n-- WYPOŻYCZALNIA POJAZDÓW--");
        System.out.println("1.Lista pojazdów");
        System.out.println("2.Wypożycz pojazd");
        System.out.println("3.Zwróć pojazd");
        System.out.println("4.Pokaż swoje dane");
        System.out.println("5. Wyjdź");

        String userInput = scanner.nextLine();
        if(userInput.equals("1")){
            showVehicles();
        }
        else if(userInput.equals("2")) {
            System.out.println("Podaj id");
            String id = scanner.nextLine();
            User currentUser = userRepo.getUser(user.getLogin());
            if (currentUser != null && !currentUser.getRentedVehicle().isEmpty()) {
                System.out.println("Już masz wypożyczony pojazd");
            } else if (repo.rentVehicle(id)) {
                currentUser.setRentedVehicle(id);

                userRepo.update(currentUser);
                this.user = currentUser;
                System.out.println("Pojazd został wypożyczony");

            }else{
                System.out.println("Błąd");
            }
        }
        else if(userInput.equals("3")){
            System.out.println("Podaj id");
            String id = scanner.nextLine();
            User currentUser = userRepo.getUser(user.getLogin());
            if(currentUser == null && currentUser.getRentedVehicle().isEmpty()){
                System.out.println("Brak pojazdów");
            }
            else if(repo.returnVehicle(id)){
                currentUser.setRentedVehicle("");
                userRepo.update(currentUser);
                this.user=currentUser;
                System.out.println("Pojazd oddany");
            }else{
                System.out.println("Błąd");
            }
        }
        else if(userInput.equals("4")){
            User cur = userRepo.getUser(user.getLogin());
            System.out.println("Moje dane: " + cur.toString());
            String rentedId = cur.getRentedVehicle();
            if(rentedId != null && !rentedId.trim().isEmpty()) {
                Vehicle v = repo.getVehicle(rentedId);
                if (v != null) {
                    System.out.println("Dane pojazdu: " + v.toString());
                }
            }
        }
        else if(userInput.equals("5")){
            return false;
        }
        else{
            System.out.println("Zły input");
        }
        return true;


    }
    private void registerMenu(){

            System.out.println("\n--REJESTRACJA--");
            System.out.println("Login: ");
            String login = scanner.nextLine();

            if(userRepo.getUser(login) != null){
                System.out.println("Użytkownik już istnieje");
                return;
            }
            System.out.println("Password: ");
            String password = scanner.nextLine();
            String hashedPassowrd = hasher.hash(password);
            userRepo.addUser(new User(login, hashedPassowrd, Role.USER.toString(), " "));
    }
    private boolean loginMenu() {
        boolean run = true;

        while (run) {
            System.out.println("\n--LOGOWANIE--");
            System.out.println("Login: ");
            String login = scanner.nextLine();
            System.out.println("Password: ");
            String password = scanner.nextLine();

            user = authentication.authenticate(login, password);
            if (user == null) {
                System.out.println("Invalid username or password");
                return false;
            }
            if (user.getRole().equals("ADMIN")) {
                boolean inMenu = true;
                while (inMenu) {
                    inMenu = adminMenu();
                }
            }
            if (user.getRole().equals("USER")) {
                boolean inMenu = true;
                while (inMenu) {
                    inMenu = userMenu();
                }

            }
        }
        return true;
    }
    public void start(){
        boolean run = true;
        while(run) {
            System.out.println("\n--MENU--");
            System.out.println("1. Logowanie ");

            System.out.println("2. Rejestracja ");
            String input = scanner.nextLine();
            if (input.equals("1")) {
                loginMenu();
            }
            else if (input.equals("2")) {
                registerMenu();
            }
        }
    }
    private void showVehicles(){
        for(Vehicle v : repo.getVehicles()){
            System.out.println(v.toString());
        }
    }
    private void rentVehicles(String id){
        repo.rentVehicle(id);
    }
    private void returnVehicle(String id){
        repo.returnVehicle(id);
    }
}
