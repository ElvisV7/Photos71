package view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class SlideshowController implements Initializable {

    @FXML
    private ImageView photoView;
    
    @FXML
    private StackPane photoDisplayPane;
    
    // List of photos in the slideshow
    private List<Photo> photos;
    // Current index in the photo list
    private int currentIndex = 0;
    
    /**
     * Call this method to set the list of photos for the slideshow.
     */
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        if (photos != null && !photos.isEmpty()) {
            currentIndex = 0;
            showPhoto(currentIndex);
        }
    }
    
    /**
     * Displays the photo at the specified index.
     */
    private void showPhoto(int index) {
        if (photos == null || photos.isEmpty()) return;
        Photo photo = photos.get(index);
        // Use the helper method from PhotoViewController to get the proper URL.
        String url = PhotoViewController.getImageURL(photo.getPath());
        Image image = new Image(url, true);
        photoView.setImage(image);
    }
    
    /**
     * Moves to the previous photo in the list.
     */
    @FXML
    private void handlePrevious(ActionEvent event) {
        if (photos != null && !photos.isEmpty()) {
            currentIndex = (currentIndex - 1 + photos.size()) % photos.size();
            showPhoto(currentIndex);
        }
    }
    
    /**
     * Moves to the next photo in the list.
     */
    @FXML
    private void handleNext(ActionEvent event) {
        if (photos != null && !photos.isEmpty()) {
            currentIndex = (currentIndex + 1) % photos.size();
            showPhoto(currentIndex);
        }
    }
    
    /**
     * Closes the slideshow window.
     */
    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) photoDisplayPane.getScene().getWindow();
        stage.close();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind the ImageView's width and height to the parent container so the image resizes automatically.
        photoView.fitWidthProperty().bind(photoDisplayPane.widthProperty());
        photoView.fitHeightProperty().bind(photoDisplayPane.heightProperty());
        photoView.setPreserveRatio(true);
    }
}
