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
        dialog.setTitle("Create New User");
        dialog.setHeaderText("Enter new user's username:");
        dialog.setContentText("Username:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUserName = result.get().trim();
            if (!newUserName.isEmpty() && !"admin".equals(newUserName)) {
                app.Photos.admin.addUser(newUserName);
                System.out.println("User created: " + newUserName);
                populateUsers(); // refresh the list
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
