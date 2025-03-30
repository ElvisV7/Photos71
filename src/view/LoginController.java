package view;

import java.io.IOException;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
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
    
    // Hold users in memory (the Admin user is added by default)
    private static ArrayList<User> users = new ArrayList<>(Arrays.asList(Admin.getInstance()));
    
    @FXML
    void handleSubmitAction(ActionEvent event) throws IOException {
        String uname = username.getText().trim();
        User temp = new User(uname);
        int index = users.indexOf(temp);
        if (index != -1) {
            User user = users.get(index);
            // Set the current user for album display.
            AlbumController.currentUser = user;
            errorLabel.setText("");
            System.out.println(user.getUsername() + " logged in!");
            Parent newRoot = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
            Scene newScene = new Scene(newRoot, 600, 400);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(newScene);
            currentStage.setResizable(true);
            currentStage.show();
        } else {
            errorLabel.setText("Invalid username");
        }
    }

    
    @FXML
    void handleSignUp(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sign Up");
        dialog.setHeaderText("Create a New Account");
        dialog.setContentText("Please enter a new username:");
        
        // Retrieve the Stage for the dialog and set an icon image.
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUser = result.get().trim();
            if (newUser.isEmpty()) {
                errorLabel.setText("Username cannot be empty");
            } else if (users.contains(new User(newUser))) {
                errorLabel.setText("Username already exists");
            } else {
                users.add(new User(newUser));
                errorLabel.setText("User created! Please log in.");
                System.out.println("New user created: " + newUser);
            }
        }
    }
}
