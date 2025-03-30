package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class PhotoDetailController {

    @FXML
    private ImageView largeImage;
    
    // Reference to the previous scene (photo view)
    private Scene previousScene;
    
    // Called by the PhotoViewController to pass the selected image.
    public void setImage(Image image) {
        largeImage.setImage(image);
        largeImage.setFitWidth(900);   // Set max width
        largeImage.setFitHeight(600);  // Set max height
        largeImage.setPreserveRatio(true); // Maintain aspect ratio
    }

    
    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }
    
    @FXML
    private void handleExit(ActionEvent event) {
        // Return to the previous scene (photo view)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (previousScene != null) {
            stage.setScene(previousScene);
        }
    }
}
