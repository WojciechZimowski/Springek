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
            } catch (RuntimeException e) {
                System.out.println("Błąd przy łączeniu z bazą danych " + e.getMessage());
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
        } catch (RuntimeException e) {
            System.out.println("Nie udało się połączyć " + e.getMessage());
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
        System.out.println("1. Lista wszystkich pojazdów\n2. Dodaj pojazd\n3. Usuń pojazd\n4. Lista użytkowników i ich wypożyczeń\n5. Usuń użytkownika\n6. Wyjdź");
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
        System.out.println("1. Lista dostępnych pojazdów\n2. Wypożycz pojazd\n3. Zwróć pojazd\n4. Pokaż swoje dane\n5. Wyjdź");
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
        List<Vehicle> allVehicles = vehicleService.findAllVehicles();
        for (Vehicle v : allVehicles) {
            boolean isRented = rentalService.vehicleHasActiveRental(v.getId());
            System.out.println("[" + v.getId() + "] " + v.getBrand() + " " + v.getModel() + " - " + v.getPlate() + (isRented ? " [WYPOŻYCZONY]" : " [WOLNY]"));
        }
    }

    private void showAvailableVehicles() {
        List<Vehicle> available = vehicleService.findAvailableVehicles();
        if (available.isEmpty()) {
            System.out.println("Brak dostępnych pojazdów.");
            return;
        }
        for (Vehicle v : available) {
            System.out.println("[" + v.getId() + "] " + v.getBrand() + " " + v.getModel() + " (Tablice: " + v.getPlate() + ", Cena: " + v.getPrice() + "zł)");
        }
    }

    private void addVehicle() {
        try {
            System.out.println("Dostępne kategorie: ");
            configService.findAllCategories().forEach(c -> System.out.println(c.getCategory() + " "));

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
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Błąd danych!");
        }
    }

    private void deleteVehicle() {
        System.out.print("\nPodaj pełne ID pojazdu do USUNIĘCIA (lub wciśnij Enter aby anulować): ");
        String idToDelete = scanner.nextLine().trim();
        if (idToDelete.isEmpty()) return;

        try {
            vehicleService.removeVehicle(idToDelete);
            System.out.println("Pojazd został pomyślnie usunięty.");
        } catch (RuntimeException e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void deleteUser() {
        List<User> allUsers = userService.findAllUsers();
        if (allUsers.isEmpty()) {
            System.out.println("Brak użytkowników.");
            return;
        }

        System.out.println("\n--- LISTA UŻYTKOWNIKÓW ---");
        for (User u : allUsers) {
            System.out.println("ID: [" + u.getId() + "] | Login: " + u.getLogin() + " [" + u.getRole() + "]");
        }

        System.out.print("\nPodaj pełne ID użytkownika do USUNIĘCIA (lub wciśnij Enter aby anulować): ");
        String selectedId = scanner.nextLine().trim();
        if (selectedId.isEmpty()) return;

        if (selectedId.equals(currentUser.getId())) {
            System.out.println("Błąd: Nie możesz usunąć własnego konta!");
            return;
        }

        try {
            userService.deleteUser(selectedId, currentUser.getId());
            System.out.println("Użytkownik został pomyślnie usunięty.");
        } catch (RuntimeException e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void rentVehicle() {
        System.out.println("\n--- DOSTĘPNE POJAZDY ---");
        showAvailableVehicles();

        System.out.print("\nWklej pełne ID pojazdu, który chcesz wypożyczyć (lub wciśnij Enter aby anulować): ");
        String vehicleId = scanner.nextLine().trim();

        if (vehicleId.isEmpty()) {
            System.out.println("Anulowano.");
            return;
        }

        try {
            rentalService.rentVehicle(currentUser.getId(), vehicleId);
            System.out.println("Pojazd został pomyślnie wypożyczony!");
        } catch (RuntimeException e) {
            System.err.println("Błąd wypożyczenia: " + e.getMessage());
        }
    }

    private void returnVehicle() {
        Optional<Rental> myActiveRentals = rentalService.findActiveRentalByUserId(currentUser.getId());

        if (myActiveRentals.isEmpty()) {
            System.out.println("Nie masz obecnie żadnych wypożyczonych pojazdów.");
            return;
        }

        Rental rental = myActiveRentals.get();
        Vehicle v = rental.getVehicle();
        System.out.println("\n--- TWOJE WYPOŻYCZENIA ---");
        System.out.println("- " + v.getBrand() + " " + v.getModel() + " [ID: " + v.getId() + "] (Wypożyczono: " + rental.getRentDateTime() + ")");
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
                r -> System.out.println("- " + r.getVehicle().getBrand() + " " + r.getVehicle().getModel() + " [" + r.getVehicle().getPlate() + "]"),
                () -> System.out.println("  (Brak aktywnych wypożyczeń)")
        );
    }

    private void showAllUsersAndRentals() {
        List<User> allUsers = userService.findAllUsers();
        for (User u : allUsers) {
            System.out.println("\nUżytkownik: " + u.getLogin() + " [" + u.getRole() + "] ID: " + u.getId());
            List<Rental> userRentals = rentalService.findUserRentals(u.getId());

            if (userRentals.isEmpty()) {
                System.out.println("  (Brak historii wypożyczeń)");
            } else {
                for (Rental r : userRentals) {
                    Vehicle v = r.getVehicle();
                    String status = r.isActive() ? "[W TRAKCIE]" : "[ZWRÓCONO]";
                    System.out.println("  -> " + v.getBrand() + " " + v.getModel() + " | " + status);
                }
            }
        }
    }
}