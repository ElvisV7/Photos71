package view;

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

public class LoginController {

    @FXML
    private Button loginButton;
    
    @FXML
    private Button signupButton;
    
    @FXML
    private TextField username;
    
    @FXML
    private Label errorLabel;
    
    // Removed local static 'users'; we now rely on the central list managed by Admin.
    
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
    
    @FXML
    void handleSignUp(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sign Up");
        dialog.setHeaderText("Create a New Account");
        dialog.setContentText("Please enter a new username:");
        
        // Set a graphic for the dialog.
        Image userImage = new Image(getClass().getResourceAsStream("/view/user_icon.png"));
        ImageView userIcon = new ImageView(userImage);
        userIcon.setFitWidth(150);
        userIcon.setFitHeight(150);
        userIcon.setPreserveRatio(true);
        dialog.setGraphic(userIcon);
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
        
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
                    // Use a slightly dark sky blue, for example, hex code #00aaff.
                    errorLabel.setStyle("-fx-text-fill: #00aaff;");
                    errorLabel.setText("User created! Please log in.");
                }
            }
        }
    }

    
    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        // Optionally save state (using the centralized user list).
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
}
