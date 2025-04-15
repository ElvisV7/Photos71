package view;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.User;
import app.Photos;

/**
 * Controller for the login screen.
 * Handles user login, signup, and logout actions.
 *  
 * @author Elvis Vasquez
 */
public class LoginController {

    @FXML
    private Button loginButton;
    
    @FXML
    private Button signupButton;
    
    @FXML
    private TextField username;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private TitledPane loginPane;

    @FXML
    public void initialize() {
        String imagePath = Photos.getDataFileURL("background.jpeg"); // this gives a file URL
        loginPane.setStyle("-fx-background-image: url('" + imagePath + "');"
                         + "-fx-background-size: cover;"
                         + "-fx-background-position: center center;"
                         + "-fx-background-repeat: no-repeat;");
    }
    
    /**
     * Handles the login process when the login button is pressed.
     * Special handling is applied if the username is "admin".
     *
     * @param event the ActionEvent triggered by the login button
     * @throws IOException if the FXML resource cannot be loaded
     */
    @FXML
    void handleSubmitAction(ActionEvent event) throws IOException {
        String uname = username.getText().trim();
        // Special handling for admin login.
        if ("admin".equals(uname)) {
            Parent newRoot = FXMLLoader.load(getClass().getResource("/view/adminSubSystem.fxml"));
            Scene newScene = new Scene(newRoot, 600, 400);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(newScene);
            currentStage.setResizable(true);
            currentStage.show();
            return;
        }
        
        // Use the shared user list from the admin instance.
        ArrayList<User> users = app.Photos.admin.listUsers();
        User temp = new User(uname);
        int index = users.indexOf(temp);
        if (index != -1) {
            User user = users.get(index);
            AlbumController.currentUser = user;  // Set current user for album display.
            errorLabel.setText("");
            Parent newRoot = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
            Scene newScene = new Scene(newRoot, 600, 400);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(newScene);
            currentStage.setResizable(true);
            currentStage.show();
        } else {
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Invalid username");
        }
    }
    
    /**
     * Handles the signup process when the signup button is pressed.
     * Opens a TextInputDialog to get a new username from the user.
     *
     * @param event the ActionEvent triggered by the signup button
     */
    @FXML
    void handleSignUp(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sign Up");
        dialog.setHeaderText("Create a New Account");
        dialog.setContentText("Please enter a new username:");
        
        // Set a graphic (icon) for the dialog using an image from the external data folder.
        Image userImage = new Image(loadImage("user_icon.png"));
        ImageView userIcon = new ImageView(userImage);
        userIcon.setFitWidth(150);
        userIcon.setFitHeight(150);
        userIcon.setPreserveRatio(true);
        dialog.setGraphic(userIcon);
        
        // Set the dialog icon.
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(loadImage("icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUser = result.get().trim();
            if (newUser.isEmpty()) {
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("Username cannot be empty");
            } else {
                ArrayList<User> users = app.Photos.admin.listUsers();
                if (users.contains(new User(newUser))) {
                    errorLabel.setStyle("-fx-text-fill: red;");
                    errorLabel.setText("Username already exists");
                } else {
                    app.Photos.admin.addUser(newUser);
                    // Use a slightly dark sky blue (e.g. #00aaff) for success.
                    errorLabel.setStyle("-fx-text-fill: #00aaff;");
                    errorLabel.setText("User created! Please log in.");
                }
            }
        }
    }
    
    /**
     * Handles the logout process.
     * Saves the user data and reloads the login scene.
     *
     * @param event the ActionEvent triggered by the logout action
     * @throws IOException if the login.fxml file cannot be loaded
     */
    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        try {
            util.PersistenceManager.saveUsers(app.Photos.admin.listUsers());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent loginView = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginScene = new Scene(loginView, 600, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
    
    /**
     * Helper method to load an image from the external data folder.
     * Assumes that the image files (e.g. icon.png, user_icon.png) are located
     * in a folder called "data" directly under the project directory.
     *
     * @param relativePath the path relative to the data folder (e.g. "/data/icon.png")
     * @return a String representing the file URL for the image
     */
    private String loadImage(String relativePath) {
        // Remove the leading slash from the relative path if present.
        String cleanPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        String baseDir = System.getProperty("user.dir") + File.separator + "data";
        String fullPath = baseDir + File.separator + cleanPath;
        return "file:" + fullPath;
    }
}
