package view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;

/**
 * Controller for the admin subsystem.
 * Allows the admin to list, create, and delete users.
 *  
 * @author Elvis Vasquez
 */
public class AdminSubSystemController {

    @FXML
    private FlowPane userFlowPane;
    
    @FXML
    public void initialize() {
        populateUsers();
    }
    
    /**
     * Populates the userFlowPane with visual boxes for each user.
     */
    private void populateUsers() {
        // Use the central user list from the admin instance.
        ArrayList<User> temp = app.Photos.admin.listUsers();
        if (temp == null) {
            temp = new ArrayList<>();
        }
        userFlowPane.getChildren().clear();
        for (User user : temp) {
            userFlowPane.getChildren().add(createUserBox(user));
        }
    }
    
    /**
     * Creates a VBox representing a user with an icon and username,
     * plus a delete button for non-admin users.
     *
     * @param user the user to represent
     * @return a VBox containing the user's UI components
     */
    private VBox createUserBox(User user) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        
        // Select a special icon for admin; otherwise, use the regular user icon.
        String iconFile = "user_icon.png";
        if ("admin".equals(user.getUsername())) {
            iconFile = "admin_icon.png";
        }
        // Use the loadDataImage helper to get the file URL.
        Image userImage = new Image(loadDataImage(iconFile));
        ImageView userIcon = new ImageView(userImage);
        userIcon.setFitWidth(150);
        userIcon.setFitHeight(150);
        userIcon.setPreserveRatio(true);
        
        Label nameLabel = new Label("admin".equals(user.getUsername()) ? "admin (self)" : user.getUsername());
        box.getChildren().addAll(userIcon, nameLabel);
        
        // Only show the delete button for non-admin users.
        if (!"admin".equals(user.getUsername())) {
            Button deleteButton = new Button("Delete User");
            deleteButton.setOnAction(e -> {
                app.Photos.admin.deleteUser(user);
                populateUsers(); // Refresh the user list after deletion.
            });
            box.getChildren().add(deleteButton);
        }
        
        return box;
    }
    
    /**
     * Handles the action of creating a new user.
     * Opens a TextInputDialog to collect the new username, validates it,
     * and then adds the user if valid.
     *
     * @param event the action event triggered by clicking the Create User button
     */
    @FXML
    private void handleCreateUserButton(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New User");
        dialog.setHeaderText("Enter new user's username:");
        dialog.setContentText("Username:");
        
        // Set the graphic (icon) using the file from the data folder.
        ImageView imageView = new ImageView(new Image(loadDataImage("user_icon.png")));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        dialog.setGraphic(imageView);
        
        // Set the dialog icon.
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(loadDataImage("icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUserName = result.get().trim();
            if ("admin".equals(newUserName) || app.Photos.admin.listUsers().contains(new User(newUserName))) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Username: " + newUserName + " is already in use!");
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(loadDataImage("icon.png")));
                alert.showAndWait();
                return;
            }
            if (newUserName.isEmpty()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Username cannot be empty!");
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(loadDataImage("icon.png")));
                alert.showAndWait();
                return;
            }
            // If valid, add the new user.
            if (!newUserName.isEmpty() && !"admin".equals(newUserName)) {
                app.Photos.admin.addUser(newUserName);
                populateUsers();
            }
        }
    }
    
    /**
     * Handles the logout action by loading the login FXML and switching scenes.
     *
     * @param event the action event triggered by clicking the Logout button
     * @throws IOException if the login.fxml file cannot be loaded
     */
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Parent loginView = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginScene = new Scene(loginView, 600, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
    
    /**
     * Helper method to load an image from the external "data" directory.
     * Assumes the "data" folder is located directly under the project directory.
     *
     * @param fileName the name of the image file (e.g. "icon.png")
     * @return a String representing the file URL for the image
     */
    private static String loadDataImage(String fileName) {
        String baseDir = System.getProperty("user.dir") + File.separator + "data";
        String fullPath = baseDir + File.separator + fileName;
        return "file:" + fullPath;
    }
}
