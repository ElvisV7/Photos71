package util;

import java.io.*;
import java.util.ArrayList;
import model.User;

/**
 * A utility class that provides methods for persisting the user data
 * to disk and loading it back into the application.
 * <p>
 * The users are stored in a file called "users.dat" in the "users" directory
 * under the project's working directory.
 * </p>
 * 
 * @author Elvis Vasquez
 */
public class PersistenceManager {
    private static final String USERS_DIR = System.getProperty("user.dir") 
            + File.separator + "users";
    private static final String USERS_FILE = USERS_DIR + File.separator + "users.dat";
    
    /**
     * Saves the given list of users to disk.
     * If the "users" directory does not exist, it is created.
     *
     * @param users the list of users to save
     * @throws IOException if an I/O error occurs while writing the file
     */
    public static void saveUsers(ArrayList<User> users) throws IOException {
        File dir = new File(USERS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            out.writeObject(users);
        }
    }
    
    /**
     * Loads the list of users from disk.
     * If the file does not exist, a new, empty list is returned.
     *
     * @return the list of users loaded from disk, or an empty list if the file does not exist
     * @throws IOException if an I/O error occurs while reading the file
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
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
