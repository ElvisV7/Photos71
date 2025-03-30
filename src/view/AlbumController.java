package view;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AlbumController {

    @FXML
    private void handleAlbumClick(MouseEvent event) throws IOException {
        VBox clickedAlbum = null;
        if (event.getSource() instanceof VBox) {
            clickedAlbum = (VBox) event.getSource();
        } else if (event.getTarget() instanceof Node) {
            clickedAlbum = (VBox) ((Node) event.getTarget()).getParent();
        }
        
        // Retrieve album name (for logging or further use)
        String albumName = ((javafx.scene.control.Label) clickedAlbum.getChildren().get(1)).getText();
        System.out.println("Album clicked: " + albumName);
        
        // Load the photo view scene (adjust the file and scene size as needed)
        Parent photoViewRoot = FXMLLoader.load(getClass().getResource("/view/photoView.fxml"));
        Scene photoScene = new Scene(photoViewRoot, 600, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(photoScene);
        stage.show();
    }
    
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Parent loginView = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginScene = new Scene(loginView, 600, 400);  // Use your original login dimensions
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
}
