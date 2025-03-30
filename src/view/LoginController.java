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
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Button loginButton;
    
    @FXML
    private Button signupButton; // Not strictly required but can be injected if needed

    @FXML
    private TextField username;
    
    @FXML
    private Label errorLabel;

    private static ArrayList<String> users = new ArrayList<String>(Arrays.asList("admin"));

    @FXML
    void handleSubmitAction(ActionEvent event) throws IOException {
        String user = username.getText().trim();
        if (users.contains(user)) {
            errorLabel.setText("");
            System.out.println(user + " logged in!");
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
        // Create a TextInputDialog with an empty default value.
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sign Up");
        dialog.setHeaderText("Create a New Account");
        dialog.setContentText("Please enter a new username:");

        // Show the dialog and wait for the user input.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUser = result.get().trim();
            if (newUser.isEmpty()) {
                errorLabel.setText("Username cannot be empty");
            } else if (users.contains(newUser)) {
                errorLabel.setText("Username already exists");
            } else {
                users.add(newUser);
                errorLabel.setText("User created! Please log in.");
                System.out.println("New user created: " + newUser);
            }
        }
    }

}
