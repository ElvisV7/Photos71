package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AdminSubSystem {

    @FXML
    private FlowPane userFlowPane;
    
    @FXML
    public void initialize() {
        // Automatically populate the FlowPane when the admin page loads.
        populateUsers();
    }
    
    private void populateUsers() {
        ArrayList<User> temp = app.Photos.admin.listUsers();
        userFlowPane.getChildren().clear();
        for (User user : temp) {
            userFlowPane.getChildren().add(createUserBox(user));
        }
    }
    
    private VBox createUserBox(User user) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        
        // Determine which icon to use: a special one for admin, a regular one for others.
        String iconPath;
        if ("admin".equals(user.getUsername())) {
            iconPath = "/view/admin_icon.png"; // Ensure this file exists in your resources.
        } else {
            iconPath = "/view/user_icon.png";
        }
        // Assume the icons are in the /view folder.
        Image userImage = new Image(getClass().getResourceAsStream(iconPath));

        ImageView userIcon = new ImageView(userImage);
        userIcon.setFitWidth(150);
        userIcon.setFitHeight(150);
        userIcon.setPreserveRatio(true);
        
        Label nameLabel = new Label("admin".equals(user.getUsername())? "admin (self)" : user.getUsername());
        box.getChildren().addAll(userIcon, nameLabel);
        
        // Add a "Delete User" button for non-admin users.
        if (!"admin".equals(user.getUsername())) {
            Button deleteButton = new Button("Delete User");
            deleteButton.setOnAction(e -> {
                app.Photos.admin.deleteUser(user);
                populateUsers(); // refresh the FlowPane after deletion
            });
            box.getChildren().add(deleteButton);
        }
        
        return box;
    }

    @FXML
    private void handleCreateUserButton(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create a New Account");
        dialog.setHeaderText("Please enter a new username:");
        dialog.setContentText("Username:");
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUserName = result.get().trim();
            if(newUserName.equals("admin")) {
            	Alert alert = new Alert(AlertType.ERROR);
            	alert.setTitle("Error");
            	alert.setHeaderText(null);
                alert.setContentText("Invalid username!");
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
                alert.showAndWait();
                return;
            }
            if (newUserName.isEmpty()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Username cannot be empty");
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
                alert.showAndWait();
                return;
            }
            if (!"admin".equals(newUserName)) {
                ArrayList<User> users = app.Photos.admin.listUsers();
                if (users.contains(new User(newUserName))) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Username already exists");
                    Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                    alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
                    alert.showAndWait();
                    return;
                } else {
                    app.Photos.admin.addUser(newUserName);
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("User Created");
                    alert.setHeaderText(null);
                    alert.setContentText("User created!");
                    Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                    alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
                    alert.showAndWait();
                    System.out.println("User created: " + newUserName);
                    populateUsers(); // Refresh the user list.
                }
            }
        }
    }

    
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Parent loginView = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginScene = new Scene(loginView, 600, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
}
