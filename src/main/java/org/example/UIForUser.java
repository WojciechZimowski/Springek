package org.example;

import org.example.models.*;
import org.example.services.*;
import org.example.services.hibernateService.AuthServiceInterface;
import org.example.services.hibernateService.RentalServiceInterface;
import org.example.services.hibernateService.UserServiceInterface;
import org.example.services.hibernateService.VehicleServiceInterface;

import java.time.LocalDateTime;
import java.util.*;

public class UIForUser {
    private final VehicleServiceInterface vehicleService;
    private final UserServiceInterface userService;
    private final RentalServiceInterface rentalService;
    private final VehicleCategoryConfigService configService;
    private final AuthServiceInterface authService;
    private final Scanner scanner;
    private User currentUser;


    public UIForUser(VehicleCategoryConfigService configService, VehicleServiceInterface vehicleService1, UserServiceInterface userService1, RentalServiceInterface rentalService1, AuthServiceInterface authService1) {

        this.configService = configService;
        this.vehicleService = vehicleService1;
        this.userService = userService1;
        this.rentalService = rentalService1;
        this.authService = authService1;

        this.scanner = new Scanner(System.in);
    }


    public void start() {
        while (true) {
            try {
                System.out.println("\n-- WYPOŻYCZALNIA --");
                System.out.println("1. Logowanie\n2. Rejestracja\n3. Wyjdź");
                String input = scanner.nextLine();
                if (input.equals("1")) loginMenu();
                else if (input.equals("2")) registerMenu();
                else if (input.equals("3")) break;
            }catch (RuntimeException e){
                System.out.println("Błąd przy łączeniu z bazą danych "+ e.getMessage());
            }
        }
    }

    private void loginMenu() {
        try {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Hasło: ");
            String password = scanner.nextLine();

            authService.login(login, password).ifPresentOrElse(u -> {
                this.currentUser = u;
                System.out.println("Zalogowano jako: " + u.getRole());
                if (u.getRole() == Role.ADMIN) {
                    while (adminMenu()) ;
                } else {
                    while (userMenu()) ;
                }
            }, () -> System.out.println("Błędne dane logowania!"));
        }catch (RuntimeException e){
            System.out.println("Nie udało się połączyć"+ e.getMessage());
        }
    }

    private void registerMenu() {
        System.out.print("Nowy login: ");
        String login = scanner.nextLine();
        System.out.print("Nowe hasło: ");
        String password = scanner.nextLine();
        if (authService.register(login, password)) {
            System.out.println("Rejestracja udana!");
        } else {
            System.out.println("Login zajęty!");
        }
    }

    private boolean adminMenu() {
        System.out.println("\n-- MENU ADMINA --");
        System.out.println("1. Lista wszystkich pojazdów\n2. Dodaj pojazd\n3. Usuń pojazd\n4. Lista użytkowników i ich wypożyczeń\n5. Usuń użytkownika\n 6.Wyjdź");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> showAllVehiclesWithStatus();
            case "2" -> addVehicle();
            case "3" -> deleteVehicle();
            case "4" -> showAllUsersAndRentals();
            case "5" -> deleteUser();
            case "6" -> {
                return false;
            }
        }
        return true;
    }

    private boolean userMenu() {
        System.out.println("\n-- MENU UŻYTKOWNIKA --");
        System.out.println("1. Lista dostępnych pojazdów\n2. Wypożycz pojazd\n3. Zwróć pojazd\n4. Pokaż swoje dane\n 5.Wyjdź");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> showAvailableVehicles();
            case "2" -> rentVehicle();
            case "3" -> returnVehicle();
            case "4" -> showMyData();
            case "5" -> {
                return false;
            }
        }
        return true;
    }

    private void showAllVehiclesWithStatus() {
        vehicleService.findAllVehicles().forEach(v -> {
            boolean isRented = rentalService.vehicleHasActiveRental(v.getId());
            System.out.println(v + (isRented ? " [WYPOŻYCZONY]" : " [WOLNY]"));
        });

    }

    private void showAvailableVehicles() {
        vehicleService.findAllVehicles().forEach(System.out::println);

    }

    private void addVehicle() {
        try {
            System.out.println("Dostępne kategorie: ");
            configService.findAllCategories().forEach(c-> System.out.println(c.getCategory()+" "));

            System.out.print("Kategoria: ");
            String cat = scanner.nextLine();
            VehicleCategoryConfig config = configService.getByCategory(cat);
            System.out.print("Marka: ");
            String brand = scanner.nextLine();
            System.out.print("Model: ");
            String model = scanner.nextLine();
            System.out.print("Rok: ");
            int year = Integer.parseInt(scanner.nextLine());
            System.out.print("Cena: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.println("Tablice: ");
            String plate = scanner.nextLine();
            Map<String, Object> attrs = new HashMap<>();
            config.getAttributes().forEach((name, type) -> {
                System.out.print("Podaj " + name + " (" + type + "): ");
                attrs.put(name, scanner.nextLine());
            });
            Vehicle v = Vehicle.builder()
                    .id(UUID.randomUUID().toString())
                    .category(cat).brand(brand).model(model)
                    .year(year).price(price).plate(plate).attributes(attrs).build();

            vehicleService.addVehicle(v);

            System.out.println("Dodano!");
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
        catch (Exception e) {
            System.out.println("Błąd danych!");
        }
    }

    private void deleteVehicle() {
        List<Vehicle> allVehicles = vehicleService.findAllVehicles();
        if (allVehicles.isEmpty()) {
            System.out.println("Brak pojazdów w bazie.");
            return;
        }

        System.out.println("\n--- LISTA POJAZDÓW DO USUNIĘCIA ---");
        for (int i = 0; i < allVehicles.size(); i++) {
            System.out.println((i + 1) + ". " + allVehicles.get(i));
        }

        System.out.print("\nWybierz numer pojazdu do USUNIĘCIA (lub 0 aby anulować): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > allVehicles.size()) {
                System.out.println("Nieprawidłowy numer!");
                return;
            }

            String idToDelete = allVehicles.get(choice - 1).getId();
            vehicleService.removeVehicle(idToDelete);
            System.out.println("Pojazd został trwale usunięty.");
        } catch (NumberFormatException e) {
            System.out.println("Błąd: Wpisz cyfrę!");
        }catch(RuntimeException e){
            System.out.println("Bład: "+e.getMessage());
        }
    }

    private void deleteUser() {
        List<User> allUsers = userService.findAllUsers();
        if (allUsers.isEmpty()) {
            System.out.println("Brak użytkowników.");
            return;
        }

        System.out.println("\n--- LISTA UŻYTKOWNIKÓW ---");
        for (int i = 0; i < allUsers.size(); i++) {
            User u = allUsers.get(i);
            System.out.println((i + 1) + ". " + u.getLogin() + " [" + u.getRole() + "]");
        }

        System.out.print("\nWybierz numer użytkownika do USUNIĘCIA (lub 0 aby anulować): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > allUsers.size()) {
                System.out.println("Nieprawidłowy numer!");
                return;
            }

            User selectedUser = allUsers.get(choice - 1);


            if (selectedUser.getId().equals(currentUser.getId())) {
                System.out.println("Błąd: Nie możesz usunąć własnego konta!");
                return;
            }

            userService.deleteUser(selectedUser.getId(),currentUser.getId());
            System.out.println("Użytkownik " + selectedUser.getLogin() + " został usunięty.");
        } catch (NumberFormatException e) {
            System.out.println("Błąd: Wpisz cyfrę!");
        } catch(RuntimeException e){
            System.out.println("Bład: "+e.getMessage());
        }
    }

    private void rentVehicle() {
        List<Vehicle> available = vehicleService.findAvailableVehicles();


        if (available.isEmpty()) {
            System.out.println("Brak dostępnych aut!");
            return;
        }

        System.out.println("\nWybierz numer auta:");
        for (int i = 0; i < available.size(); i++) {
            System.out.println((i + 1) + ". " + available.get(i).getBrand() + " " + available.get(i).getModel());
        }

        System.out.print("Wybór: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index < 0 || index >= available.size()) {
                System.out.println("Nieprawidłowy numer!");
                return;
            }

            Vehicle selected = available.get(index);
            rentalService.rentVehicle(currentUser.getId(), selected.getId());

            System.out.println("Wypożyczono: " + selected.getBrand());
        } catch (RuntimeException e) {
            System.err.println("Błąd wypożyczenia: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Wpisz poprawną cyfrę!");
        }
    }

    private void returnVehicle() {

        Optional<Rental> myActiveRentals = rentalService.findActiveRentalByUserId(currentUser.getId());

        if (myActiveRentals.isEmpty()) {
            System.out.println("Nie masz obecnie żadnych wypożyczonych pojazdów.");
            return;
        }
//url ma być zmienną srodowiskow,ą
        Rental rental = myActiveRentals.get();
        Vehicle v = rental.getVehicle();
        System.out.println("\n--- TWOJE WYPOŻYCZENIA ---");

//huh
        System.out.println("1. " + v.getBrand() + " " + v.getModel() + " (Wypożyczono: " + rental.getRentDateTime() + ")");
        System.out.print("\nCzy chcesz zwrócić ten pojazd? (Wpisz 1 aby potwierdzić, 0 aby anulować): ");

        try {
            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                rentalService.returnVehicle(currentUser.getId());
                System.out.println("Pojazd został pomyślnie zwrócony!");
            } else {
                System.out.println("Anulowano zwrot.");
            }
        } catch (RuntimeException e) {
            System.err.println("Błąd zwrotu: " + e.getMessage());
        }
    }





    private void showMyData() {
        System.out.println("Zalogowany jako: " + currentUser.getLogin());
        System.out.println("Twoje aktywne wypożyczenia:");

        rentalService.findActiveRentalByUserId(currentUser.getId()).ifPresentOrElse(
                r -> System.out.println("- " + r.getVehicle()),
                () -> System.out.println("  (Brak aktywnych wypożyczeń)")
        );
    }

    private void showAllUsersAndRentals() {
        userService.findAllUsers().forEach(u -> {
            System.out.println("\nUżytkownik: " + u.getLogin() + " [" + u.getRole() + "]");


           List<Rental> userRentals = rentalService.findUserRentals(u.getId());

            if (userRentals.isEmpty()) {
                System.out.println("  (Brak historii wypożyczeń)");
            } else {
                userRentals.forEach(r -> {
                    Vehicle v = r.getVehicle(); // Bezpośrednio pobieramy obiekt z encji
                    String status = r.isActive() ? "[W TRAKCIE]" : "[ZWRÓCONO]";
                    System.out.println("  -> " + v.getBrand() + " " + v.getModel() + " | " + status);
                });
            }
        });
    }
}