package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Admin extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static Admin instance;
    private static ArrayList<User> users; // centralized user list

    // Private constructor: assign the shared users list and ensure admin is included.
    private Admin(ArrayList<User> users) {
        super("admin");
        Admin.users = users;
        if (!users.contains(this)) {
            users.add(this);
        }
    }
    
    // Factory method: if an Admin already exists in the list, return it; otherwise, create a new one.
    public static Admin getInstance(ArrayList<User> userList) {
        // If an instance exists, return it.
        if (instance == null) {
            instance = new Admin(userList);
        } else {
           
        }
        return instance;
    }
    

    public ArrayList<User> listUsers() {
        return users;
    }
    
    // Adds a new user (if not already present).
    public void addUser(String username) {
        User newUser = new User(username);
        if (!users.contains(newUser)) {
            users.add(newUser);
        }
    }
    
    public void addUser(User newUser) {
        if (!users.contains(newUser)) {
            users.add(newUser);
        }
    }
    
    // Deletes a user (except admin).
    public void deleteUser(User user) {
        if (!"admin".equals(user.getUsername())) {
            users.remove(user);
        }
    }
    
    @Override
    public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Admin)) return false;
         Admin admin = (Admin) o;
         return Objects.equals(getUsername(), admin.getUsername());
    }
    
    @Override
    public int hashCode() {
         return Objects.hash(getUsername());
    }
}
