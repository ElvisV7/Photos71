package view;

import java.util.ArrayList;

public class Admin extends User {
    private static Admin instance;
    private static ArrayList<User> users;
    
    private Admin(ArrayList<User> users) {
        super("admin");
        this.users = users;
        this.users.add(this);
    }
    
    public static Admin getInstance(ArrayList<User> users) {
        if(instance == null) {
            instance = new Admin(users);
        }
        return instance;
    }
    
    //Accessors
    public void addUser(String username) {
    	this.instance.users.add(new User(username));
    }
    
    public void deleteUser(User user) {
    	this.instance.users.remove(user);
    }
    
    public ArrayList<User> listUsers() {
    	return this.instance.users;
    }   
}
