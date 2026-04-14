package org.example;

import org.example.models.*;
import org.example.repositories.*;
import org.example.services.AuthService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class UIForUser {
    private final IVehicleRepository vehicleRepo;
    private final IUserRepository userRepo;
    private final RentalRepository rentalRepo;
    private final AuthService authService;
    private final Scanner scanner;
    private User currentUser;

    public UIForUser(IVehicleRepository vehicleRepo, IUserRepository userRepo,
                     RentalRepository rentalRepo, AuthService authService) {
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
        this.authService = authService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n-- WYPOŻYCZALNIA --");
            System.out.println("1. Logowanie\n2. Rejestracja\n3. Wyjdź");
            String input = scanner.nextLine();
            if (input.equals("1")) loginMenu();
            else if (input.equals("2")) registerMenu();
            else if (input.equals("3")) break;
        }
    }

    private void loginMenu() {
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
        vehicleRepo.findAll().forEach(v -> {
            boolean isRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isPresent();
            System.out.println(v + (isRented ? " [WYPOŻYCZONY]" : " [WOLNY]"));
        });
    }

    private void showAvailableVehicles() {
        vehicleRepo.findAll().stream()
                .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .forEach(System.out::println);
    }

    private void addVehicle() {
        try {
            System.out.print("Kategoria: ");
            String cat = scanner.nextLine();
            System.out.print("Marka: ");
            String brand = scanner.nextLine();
            System.out.print("Model: ");
            String model = scanner.nextLine();
            System.out.print("Rok: ");
            int year = Integer.parseInt(scanner.nextLine());
            System.out.print("Cena: ");
            double price = Double.parseDouble(scanner.nextLine());

            Vehicle v = Vehicle.builder()
                    .id(UUID.randomUUID().toString())
                    .category(cat).brand(brand).model(model)
                    .year(year).price(price).build();
            vehicleRepo.save(v);
            System.out.println("Dodano!");
        } catch (Exception e) {
            System.out.println("Błąd danych!");
        }
    }

    private void deleteVehicle() {
        List<Vehicle> allVehicles = vehicleRepo.findAll();
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
            vehicleRepo.deleteById(idToDelete);
            System.out.println("Pojazd został trwale usunięty.");
        } catch (NumberFormatException e) {
            System.out.println("Błąd: Wpisz cyfrę!");
        }
    }

    private void deleteUser() {
        List<User> allUsers = userRepo.findAll();
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

            userRepo.deleteById(selectedUser.getId());
            System.out.println("Użytkownik " + selectedUser.getLogin() + " został usunięty.");
        } catch (NumberFormatException e) {
            System.out.println("Błąd: Wpisz cyfrę!");
        }
    }

    private void rentVehicle() {

        List<Vehicle> available = vehicleRepo.findAll().stream()
                .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .toList();

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
            Rental r = Rental.builder()
                    .id(UUID.randomUUID().toString())
                    .vehicleId(selected.getId()) // Program sam bierze to długie ID
                    .userId(currentUser.getId())
                    .rentDateTime(LocalDateTime.now().toString())
                    .build();

            rentalRepo.save(r);
            System.out.println("Wypożyczono: " + selected.getBrand());
        } catch (Exception e) {
            System.out.println("Wpisz poprawną cyfrę!");
        }
    }

    private void returnVehicle() {

        List<Rental> myActiveRentals = rentalRepo.findAll().stream()
                .filter(r -> r.getUserId().equals(currentUser.getId()) && r.isActive())
                .toList();

        if (myActiveRentals.isEmpty()) {
            System.out.println("Nie masz obecnie żadnych wypożyczonych pojazdów.");
            return;
        }


        System.out.println("\n--- TWOJE WYPOŻYCZENIA ---");
        for (int i = 0; i < myActiveRentals.size(); i++) {
            Rental r = myActiveRentals.get(i);

            String carInfo = vehicleRepo.findById(r.getVehicleId())
                    .map(v -> v.getBrand() + " " + v.getModel() + " [" + v.getPlate() + "]")
                    .orElse("Nieznany pojazd (ID: " + r.getVehicleId() + ")");

            System.out.println((i + 1) + ". " + carInfo + " (Data: " + r.getRentDateTime() + ")");
        }


        System.out.print("\nWybierz numer auta do zwrotu (lub 0 aby anulować): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > myActiveRentals.size()) {
                System.out.println("Nieprawidłowy numer!");
                return;
            }


            Rental selectedRental = myActiveRentals.get(choice - 1);
            selectedRental.setReturnDateTime(LocalDateTime.now().toString());

            rentalRepo.save(selectedRental);
            System.out.println("Pojazd został pomyślnie zwrócony!");

        } catch (NumberFormatException e) {
            System.out.println("Błąd: Wpisz poprawną cyfrę!");
        }
    }

    private void showMyData() {

        userRepo.findByLogin(currentUser.getLogin()).ifPresentOrElse(u -> {
            System.out.println("Zalogowany jako: " + u.getLogin());
            System.out.println("Aktywne wypożyczenia:");


            rentalRepo.findAll().stream()
                    .filter(r -> r.getUserId().equals(u.getId()) && r.isActive())
                    .forEach(r -> {
                        vehicleRepo.findById(r.getVehicleId())
                                .ifPresent(v -> System.out.println("- " + v));
                    });
        }, () -> System.out.println("Błąd sesji."));
    }

    private void showAllUsersAndRentals() {
        userRepo.findAll().forEach(u -> {
            System.out.println("\nUżytkownik: " + u.getLogin() + " [" + u.getRole() + "]");


            List<Rental> userRentals = rentalRepo.findAll().stream()
                    .filter(r -> r.getUserId().equals(u.getId()))
                    .toList();

            if (userRentals.isEmpty()) {
                System.out.println("  (Brak historii wypożyczeń)");
            } else {
                userRentals.forEach(r -> {
                    vehicleRepo.findById(r.getVehicleId()).ifPresentOrElse(v -> {
                        String status = r.isActive() ? "[W TRAKCIE]" : "[ZWRÓCONO: " + r.getReturnDateTime() + "]";
                        System.out.println("  -> " + v + " | " + status);
                    }, () -> System.out.println("  -> Błąd: Nie znaleziono auta o ID: " + r.getVehicleId()));
                });
            }
        });
    }
}