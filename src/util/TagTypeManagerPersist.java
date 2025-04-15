package util;

import java.io.*;

/**
 * @author Tyler Gehringer
 */

public class TagTypeManagerPersist {
    private static final String FILE_NAME = "tagTypes.dat";
    
    public static void persist(TagTypeManager manager) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(manager);
            System.out.println("TagTypeManager persisted successfully.");
        } catch (IOException e) {
            System.err.println("Error persisting TagTypeManager: " + e.getMessage());
        }
    }
    
    public static TagTypeManager load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No persisted TagTypeManager found, using default.");
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            TagTypeManager manager = (TagTypeManager) in.readObject();
            System.out.println("TagTypeManager loaded successfully.");
            return manager;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading TagTypeManager: " + e.getMessage());
            return null;
        }
    }
}
