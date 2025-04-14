package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Admin;
import model.Album;
import model.Photo;
import model.User;
import util.PersistenceManager;

public class Photos extends Application {
    // Global admin instance (which owns the user list)
    public static Admin admin;
    public static User stock;
    // Base directory for user data
    public static String usersDir = System.getProperty("user.dir") + File.separator + "users";
    
    /**
     * Helper method to build a file URL for images in the external "data" folder.
     * This method uses File.toURI() to ensure that the path is correctly escaped.
     *
     * @param filename the name of the file (e.g., "icon.png")
     * @return a well-formed file URL (e.g., "file:///C:/.../data/icon.png")
     */
    public static String getDataFileURL(String filename) {
        File file = new File(System.getProperty("user.dir") + File.separator + "data", filename);
        return file.toURI().toString();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        ArrayList<User> loadedUsers;
        try {
            loadedUsers = PersistenceManager.loadUsers();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            loadedUsers = new ArrayList<>();
        }
        if (loadedUsers == null) {
            loadedUsers = new ArrayList<>();
        }
        
        // Check if an admin and stock user already exist in loadedUsers.
        boolean adminExists = loadedUsers.stream().anyMatch(u -> "admin".equals(u.getUsername()));
        boolean stockExists = loadedUsers.stream().anyMatch(u -> "stock".equals(u.getUsername()));
        if (!adminExists) {
            // Create admin if not found.
            admin = Admin.getInstance(loadedUsers);
        } else {
            // Reuse the existing admin instance from the loaded list.
            admin = Admin.getInstance(loadedUsers);
        }
        if (!stockExists) {
            // Create stock user if not found.
            stock = new User("stock");
            admin.addUser(stock);
            Album album = new Album("stock");
            stock.getAlbums().add(album);
            // Use our helper to get the file URLs for stock photos.
            album.addPhoto(new Photo(getDataFileURL("icon.png")));
            album.addPhoto(new Photo(getDataFileURL("admin_icon.png")));
            album.addPhoto(new Photo(getDataFileURL("background.jpeg")));
            album.addPhoto(new Photo(getDataFileURL("folder_icon.png")));
            album.addPhoto(new Photo(getDataFileURL("remove_icon.png")));
            album.addPhoto(new Photo(getDataFileURL("user_icon.png")));
        }
        
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        primaryStage.setTitle("Photos");
        // Set the application icon using our helper.
        primaryStage.getIcons().add(new Image(getDataFileURL("icon.png")));
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/view/application.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Save the users list on application exit.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                PersistenceManager.saveUsers(admin.listUsers());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
