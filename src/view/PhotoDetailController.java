package view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.Photo;

/*
 * This class handles the detailed view window of photos
 * 
 * @author Elvis Vasquez
 */

public class PhotoDetailController {

    @FXML
    private ImageView largeImage;
    
    @FXML
    private Label captionLabel;
    
    @FXML
    private Label dateLabel;
    
    @FXML
    private Label tagsLabel;
    
    private Scene previousScene;
    
    /**
     * Sets the photo data and updates the UI.
     * @param photo the Photo object whose details are to be shown
     */
    public void setPhoto(Photo photo) {
        // Update caption
        captionLabel.setText("Caption: " + photo.getCaption());
        
        // Format the date 
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        dateLabel.setText("Date and time of capture: " + df.format(photo.getDate().getTime()));
        
        StringBuilder tagsDisplay = new StringBuilder();
        for (model.Tag tag : photo.getTags()) {
            tagsDisplay.append(tag.toString()).append(" ");
        }
        tagsLabel.setText("Tags: " + tagsDisplay.toString().trim());
    }
    
    /**
     * Sets the image to be displayed.
     * @param image the Image to display in the detail view
     */
    public void setImage(Image image) {
        largeImage.setImage(image);
        largeImage.setFitWidth(900);
        largeImage.setFitHeight(600);
        largeImage.setPreserveRatio(true);
    }
    
    /**
     * Stores the previous scene so that the user can return to it.
     * @param scene the previous Scene
     */
    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }
    
    /**
     * Handles the Exit action, returning to the previous scene.
     * @param event the action event triggered by clicking the Exit button
     */
    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (previousScene != null) {
            stage.setScene(previousScene);
        }
    }
}
