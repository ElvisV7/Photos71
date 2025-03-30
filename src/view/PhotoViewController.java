package view;

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

import java.io.File;

public class PhotoViewController implements Initializable {

    @FXML
    private TilePane photoTilePane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initially, the album may be empty.
        // If you have existing photos to load, do so here.
        // For demonstration, suppose you already have some photos:
        String[] photoPaths = {"/app/icon.png","/view/folder_icon.png"};
        for (String path : photoPaths) {
            addPhoto(path);
        }
    }
    
    // Method to add a photo icon from a resource path
    private void addPhoto(String path) {
        Image photo = new Image(getClass().getResourceAsStream(path));
        ImageView photoIcon = new ImageView(photo);
        photoIcon.setFitWidth(150);  // medium-sized icon width
        photoIcon.setFitHeight(150); // medium-sized icon height
        photoIcon.setPreserveRatio(true);
        
        // Make the photo clickable:
        photoIcon.setOnMouseClicked((MouseEvent event) -> {
            openPhoto(photo, event);
        });
        
        photoTilePane.getChildren().add(photoIcon);
    }
    
    // Handler for uploading new photos (existing code)
    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo(s) to Upload");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null && !files.isEmpty()) {
            for (File file : files) {
                try {
                    Image photo = new Image(new FileInputStream(file));
                    ImageView photoIcon = new ImageView(photo);
                    photoIcon.setFitWidth(150);   // medium-sized icon width
                    photoIcon.setFitHeight(150);  // medium-sized icon height
                    photoIcon.setPreserveRatio(true);
                    
                    // Make the photo clickable to open in a larger view.
                    photoIcon.setOnMouseClicked((MouseEvent e) -> {
                        openPhoto(photo, e);
                    });
                    
                    // Create a VBox to hold the ImageView and a Label with the file name.
                    VBox photoContainer = new VBox();
                    photoContainer.setAlignment(Pos.CENTER);
                    photoContainer.setSpacing(5); // optional spacing between image and label
                    
                    Label photoLabel = new Label(file.getName());
                    
                    photoContainer.getChildren().addAll(photoIcon, photoLabel);
                    
                    photoTilePane.getChildren().add(photoContainer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    
    // Method to open a clicked photo in a larger view
    private void openPhoto(Image photo, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photoDetail.fxml"));
            // Load the enlarged photo view layout
            Node detailRoot = loader.load();
            PhotoDetailController detailController = loader.getController();
            detailController.setImage(photo);
            
            // Get the current scene so we can return to it later.
            Scene currentScene = photoTilePane.getScene();
            detailController.setPreviousScene(currentScene);
            
            // Create a new scene with the same dimensions as the current one.
            Scene detailScene = new Scene((javafx.scene.Parent) detailRoot, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(detailScene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent albumView = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
        Scene albumScene = new Scene(albumView, 600, 400); // Use the original album view dimensions
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(albumScene);
        stage.show();
    }

}
