package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {
    private static List<User> users=new ArrayList<>();
    private final String path = "users.csv";
    public UserRepository() {
        load();
    }
    @Override
    public User getUser(String login) {
        return users.stream().filter(u->u.getLogin().equals(login)).findFirst().map(User::new).orElse(null);
    }

    @Override
    public List<User> getUsers() {
        List<User> copy = new ArrayList<>();
        for (User user : users) {
            copy.add(new User(user));
        }
        return copy;
    }

    @Override
    public boolean update(User user) {
        for(int i=0;i<users.size();i++){
            if(users.get(i).getLogin().equals(user.getLogin())){
                users.set(i,new User(user));
                save();
                return true;
            }
        }
        return false;
    }

   @Override
    public void save() {
        try(PrintWriter pw = new PrintWriter(new FileOutputStream(path))){
            for(User user : users){
                pw.println(user.toCSV());
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

    }

    @Override
    public void load() {
        File file = new File(path);
        if(!file.exists()){
            return;
        }

        this.users.clear();
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line=br.readLine())!=null){
                String[] lineArray = line.split(";");
                String rentedId =(lineArray.length>3)? lineArray[3]:"";
                users.add(new User(lineArray[0],lineArray[1],lineArray[2],rentedId));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
