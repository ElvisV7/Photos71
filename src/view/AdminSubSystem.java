package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminSubSystem {

    @FXML
    private FlowPane userFlowPane;
    
    @FXML
    public void initialize() {
        populateUsers();
    }
    
    private void populateUsers() {
        // Use the central user list from admin.
        ArrayList<User> temp = app.Photos.admin.listUsers();
        if (temp == null) {
            temp = new ArrayList<>();
        }
        userFlowPane.getChildren().clear();
        for (User user : temp) {
            userFlowPane.getChildren().add(createUserBox(user));
        }
    }
    
    private VBox createUserBox(User user) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        
        // Use a special icon for admin.
        String iconPath = "user_icon.png";
        if ("admin".equals(user.getUsername())) {
            iconPath = "admin_icon.png";
        }
        Image userImage = new Image(getClass().getResourceAsStream("/view/" + iconPath));
        ImageView userIcon = new ImageView(userImage);
        userIcon.setFitWidth(150);
        userIcon.setFitHeight(150);
        userIcon.setPreserveRatio(true);
        
        Label nameLabel = new Label("admin".equals(user.getUsername()) ? "admin (self)" : user.getUsername());
        box.getChildren().addAll(userIcon, nameLabel);
        
        // Add a "Delete User" button for non-admin users.
        if (!"admin".equals(user.getUsername())) {
            Button deleteButton = new Button("Delete User");
            deleteButton.setOnAction(e -> {
                app.Photos.admin.deleteUser(user);
                populateUsers(); // Refresh the list after deletion.
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
        
        // Set the graphic to display the icon
        ImageView imageView = new ImageView(new Image("/view/user_icon.png"));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        dialog.setGraphic(imageView);
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newUserName = result.get().trim();
            if("admin".equals(newUserName) || app.Photos.admin.listUsers().contains(new User(newUserName))) {
            	Alert alert = new Alert(AlertType.ERROR);
            	alert.setTitle("Error");
	        	alert.setHeaderText(null);
	            alert.setContentText("Username: " + newUserName +" is already in use!");
	            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
	            alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
	            alert.showAndWait();
	            return;
            }
            if(newUserName.isEmpty()) {
            	Alert alert = new Alert(AlertType.ERROR);
            	alert.setTitle("Error");
	        	alert.setHeaderText(null);
	            alert.setContentText("Username cannot be empty!");
	            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
	            alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
	            alert.showAndWait();
	            return;
            }
            if (!newUserName.isEmpty() && !"admin".equals(newUserName)) {
                app.Photos.admin.addUser(newUserName);
                populateUsers();
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
