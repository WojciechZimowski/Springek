package org.example.repositories.impl;

import org.example.db.JsonFileStorage;
import org.example.repositories.IUserRepository;
import org.example.models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserRepository implements IUserRepository {
    private final JsonFileStorage<User> jsonFileStorage;
    private static List<User> users=new ArrayList<>();

    public UserRepository(JsonFileStorage<User> jsonFileStorage) {
        this.jsonFileStorage = jsonFileStorage;
        this.users = jsonFileStorage.load();
    }


//    public UserRepository() {
//        File file = new File(path);
//        if(!file.exists()){
//            return;
//        }
//
//        this.users.clear();
//        try(BufferedReader br = new BufferedReader(new FileReader(file))){
//            String line;
//            while((line=br.readLine())!=null){
//                String[] lineArray = line.split(";");
//                String rentedId =(lineArray.length>3)? lineArray[3]:"";
//
//
//                users.add(new User(lineArray[0],lineArray[1],lineArray[2],rentedId));
//            }
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    @Override
//    public User getUser(String login) {
//        return users.stream().filter(u->u.getLogin().equals(login)).findFirst().map(User::new).orElse(null);
//    }
//
//    @Override
//    public List<User> getUsers() {
//        List<User> copy = new ArrayList<>();
//        for (User user : users) {
//            copy.add(new User(user));
//        }
//        return copy;
//    }
//
//    @Override
//    public boolean update(User user) {
//        for(int i=0;i<users.size();i++){
//            if(users.get(i).getLogin().equals(user.getLogin())){
//                users.set(i,new User(user));
//                try(PrintWriter pw = new PrintWriter(new FileOutputStream(path))){
//                    for(User u : users){
//                        pw.println(u.toCSV());
//                    }
//                }catch(FileNotFoundException e){
//                    e.printStackTrace();
//                }
//                return true;
//            }
//        }
//        return false;
//    }
//    public boolean addUser(User user){
//        if(getUser(user.getLogin())!=null){
//            System.out.println("Użytkownik już istneiej");
//            return false;
//        }
//        users.add(user);
//        try(PrintWriter pw = new PrintWriter(new FileOutputStream(path))){
//            for(User u : users){
//                pw.println(u.toCSV());
//            }
//        }catch(FileNotFoundException e){
//            e.printStackTrace();
//        }
//        return true;
//    }
//    public boolean deleteUser(String login){
//        User u = getUser(login);
//        if(u==null){
//            System.out.println("Użytkownikk nie istnieje");
//            return false;
//        }
////        if(u.getRentedVehicle()!=null){
////            System.out.println("Użytkownik ma pojazd");
////            return false;
////        }
//
//            users.removeIf(user->user.getLogin().equals(login));
//            try(PrintWriter pw = new PrintWriter(new FileOutputStream(path))){
//                for(User us : users){
//                    pw.println(us.toCSV());
//                }
//             }catch(FileNotFoundException e){
//                 e.printStackTrace();
//        }
//            return true;
//    }

    @Override
    public List<User> findAll() {
        this.users = jsonFileStorage.load();
        return this.users.stream().map(User::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(String id) {
        return jsonFileStorage.load().stream().filter(u->u.getId().equals(id)).findFirst().map(User::copy);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return jsonFileStorage.load().stream().filter(u->u.getLogin().equals(login)).findFirst().map(User::copy);
    }

    @Override
    public User save(User user) {
    this.users=jsonFileStorage.load();
    this.users.removeIf(u->u.getId().equals(user.getId()) ||  u.getLogin().equals(user.getLogin()));
    User copy=user.copy();
    this.users.add(copy);
    jsonFileStorage.save(this.users);

        return copy;
    }

    @Override
    public void deleteById(String id) {
        this.users=jsonFileStorage.load();
        this.users.removeIf(u->u.getId().equals(id));
        jsonFileStorage.save(users);
    }


}
