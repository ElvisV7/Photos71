package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;

public class PhotoViewController implements Initializable {

    @FXML
    private TilePane photoTilePane;

    // This field will hold the current album instance passed from the albums page.
    private Album album;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // No demo photos loaded here.
        // The album will be set later via setAlbum(), which will update the display.
    }
    
    // This method is called by the previous controller when an album is opened.
    public void setAlbum(Album album) {
        this.album = album;
        updatePhotoDisplay();
    }
    
    // Update the TilePane by clearing it and adding each photo from the album.
    private void updatePhotoDisplay() {
        photoTilePane.getChildren().clear();
        if(album != null) {
            for(Photo photo : album.getPhotos()) {
                addPhotoToTile(photo);
            }
        }
    }
    
    // Create a visual container for the photo and add it to the tile pane.
    private void addPhotoToTile(Photo photo) {
        Image image = new Image(getImageInputStream(photo.getPath()));
        ImageView photoIcon = new ImageView(image);
        photoIcon.setFitWidth(150);
        photoIcon.setFitHeight(150);
        photoIcon.setPreserveRatio(true);
        
        // Make the photo clickable to open in a larger view.
        photoIcon.setOnMouseClicked((MouseEvent event) -> {
            openPhoto(image, event);
        });
        
        // Create a container (VBox) with the ImageView and a label for the file name.
        VBox photoContainer = new VBox();
        photoContainer.setAlignment(Pos.CENTER);
        photoContainer.setSpacing(5);
        
        // Derive file name from the photo's path.
        String fileName = new File(photo.getPath()).getName();
        Label photoLabel = new Label(fileName);
        
        photoContainer.getChildren().addAll(photoIcon, photoLabel);
        photoTilePane.getChildren().add(photoContainer);
    }
    
    private FileInputStream getImageInputStream(String path) {
        try {
            // If the path starts with "/", assume it's a resource path bundled with the app.
            if (path.startsWith("/")) {
                java.net.URL resourceUrl = getClass().getResource(path);
                if (resourceUrl != null) {
                    return new FileInputStream(new File(resourceUrl.toURI()));
                } else {
                    throw new RuntimeException("Resource not found: " + path);
                }
            } else {
                // Otherwise, assume it's an absolute file system path.
                File file = new File(path);
                if (file.exists()) {
                    return new FileInputStream(file);
                } else {
                    throw new RuntimeException("File not found: " + path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    // Handler for uploading new photos.
    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo(s) to Upload");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null && !files.isEmpty() && album != null) {
            for (File file : files) {
                try {
                    // Create a new Photo with the file's absolute path.
                    Photo newPhoto = new Photo(file.getAbsolutePath());
                    // Add the photo to the album.
                    album.addPhoto(newPhoto);
                    // Add the photo icon to the display.
                    addPhotoToTile(newPhoto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Open a clicked photo in a larger view.
    private void openPhoto(Image photo, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photoDetail.fxml"));
            Parent detailRoot = loader.load();
            PhotoDetailController detailController = loader.getController();
            detailController.setImage(photo);
            
            // Save the current scene so we can return to it later.
            Scene currentScene = photoTilePane.getScene();
            detailController.setPreviousScene(currentScene);
            
            // Create a new scene for the detail view with fixed dimensions.
            Scene detailScene = new Scene(detailRoot, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(detailScene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent albumView = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
        Scene albumScene = new Scene(albumView, 600, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(albumScene);
        stage.show();
    }
}
