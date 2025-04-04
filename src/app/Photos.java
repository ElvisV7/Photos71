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
import util.PersistenceManager;
import view.Admin;
import view.Album;
import view.Photo;
import view.User;

public class Photos extends Application {
    // Global admin instance (which owns the user list)
    public static Admin admin;
    public static User stock;
    // Base directory for user data
    public static String usersDir = System.getProperty("user.dir") + File.separator + "users";
    
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
        
        // Check if an admin and stock user already exists in loadedUsers.
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
            stock.getAlbums().get(stock.getAlbums().indexOf(album)).addPhoto(new Photo("/app/icon.png"));
            stock.getAlbums().get(stock.getAlbums().indexOf(album)).addPhoto(new Photo("/view/admin_icon.png"));
            stock.getAlbums().get(stock.getAlbums().indexOf(album)).addPhoto(new Photo("/view/background.jpeg"));
            stock.getAlbums().get(stock.getAlbums().indexOf(album)).addPhoto(new Photo("/view/folder_icon.png"));
            stock.getAlbums().get(stock.getAlbums().indexOf(album)).addPhoto(new Photo("/view/remove_icon.png"));
            stock.getAlbums().get(stock.getAlbums().indexOf(album)).addPhoto(new Photo("/view/user_icon.png"));
        } 
        
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        primaryStage.setTitle("Photos");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
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
