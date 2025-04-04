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
    
    // This method is called to set the photo and update the UI.
    public void setPhoto(Photo photo) {
        // Update caption
        captionLabel.setText("Caption: " + photo.getCaption());
        // Format the date 
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        dateLabel.setText("Date and time of capture: " + df.format(photo.getDate().getTime()));
        
        /* Use for tags:
          for example:
          ArrayList<Tags> tempTags = currentUser.getTags();
          String tags = "";
          for(int i = 0; i < tempTags.size(); ++i){
          	tags += tempTags.get(i);
          }
          tagsLabel.setText("Tags: " + tags);
          */
    }
    
    // Optionally, keep the setImage if you want to set a specific image separately.
    public void setImage(Image image) {
        largeImage.setImage(image);
        largeImage.setFitWidth(900);
        largeImage.setFitHeight(600);
        largeImage.setPreserveRatio(true);
    }
    
    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }
    
    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (previousScene != null) {
            stage.setScene(previousScene);
        }
    }
}
