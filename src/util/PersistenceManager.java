package util;

import java.io.*;
import java.util.ArrayList;
import view.User;

public class PersistenceManager {
    private static final String USERS_DIR = System.getProperty("user.dir") + File.separator + "users";
    private static final String USERS_FILE = USERS_DIR + File.separator + "users.dat";
    
    // Save the list of users to disk
    public static void saveUsers(ArrayList<User> users) throws IOException {
        File dir = new File(USERS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            out.writeObject(users);
        }
    }
    
    // Load the list of users from disk; if none exists, return a new list.
    public static ArrayList<User> loadUsers() throws IOException, ClassNotFoundException {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<User>) in.readObject();
        }
    }
}
