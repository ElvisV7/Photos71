package view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Photo;

/**
 * Controller for the manual slideshow.
 * Displays one photo at a time and allows navigation through the list.
 * The ImageView is bound to the parent container so the image resizes automatically.
 * 
 * @author Elvis Vasquez
 */
public class SlideshowController implements Initializable {

    @FXML
    private ImageView photoView;
    
    @FXML
    private StackPane photoDisplayPane;
    
    // List of photos in the slideshow.
    private List<Photo> photos;
    // Current index in the photo list.
    private int currentIndex = 0;
    
    private Scene previousScene;
    private String previousTitle;

    /**
     * Stores the previous scene to allow returning to it.
     * 
     * @param scene the previous scene
     * @param title the previous window title
     */
    public void setPreviousScene(Scene scene, String title) {
        this.previousScene = scene;
        this.previousTitle = title;
    }
    
    /**
     * Sets the list of photos for the slideshow.
     * If the list is non-empty, displays the first photo.
     * 
     * @param photos the list of photos to show in the slideshow
     */
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        if (photos != null && !photos.isEmpty()) {
            currentIndex = 0;
            showPhoto(currentIndex);
        }
    }
    
    /**
     * Displays the photo at the given index.
     * Uses the helper method from PhotoViewController to generate a proper URL.
     *
     * @param index the index of the photo in the list to display
     */
    private void showPhoto(int index) {
        if (photos == null || photos.isEmpty()) return;
        Photo photo = photos.get(index);
        // Get the image URL using the helper method.
        String url = PhotoViewController.getImageURL(photo.getPath());
        Image image = new Image(url, true);
        photoView.setImage(image);
    }
    
    /**
     * Moves to the previous photo in the list.
     *
     * @param event the action event triggered by the previous button
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
     *
     * @param event the action event triggered by the next button
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
     *
     * @param event the action event triggered by the exit button
     */
    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) photoDisplayPane.getScene().getWindow();
        
        if (previousScene != null) {
            stage.setScene(previousScene);
            stage.setTitle(previousTitle);
        } else {
            // Fallback if no previous scene was set
            stage.close();
        }
    }
    
    /**
     * This method is automatically called when the FXML file is loaded.
     * It binds the ImageView's width and height to its parent container so that the image resizes automatically.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind the ImageView's dimensions to the StackPane's dimensions.
        photoView.fitWidthProperty().bind(photoDisplayPane.widthProperty());
        photoView.fitHeightProperty().bind(photoDisplayPane.heightProperty());
        photoView.setPreserveRatio(true);
    }
}
