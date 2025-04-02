package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
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
    
    // Inject the upload button
    @FXML
    private Button uploadPhotoButton;
    
    private Album album;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // You may populate your TilePane here or later via setAlbum.
    }
    
    public void setAlbum(Album album) {
        this.album = album;
        updatePhotoDisplay();
        
        // Hide the upload button if this is the "Stock Images" album.
        if ("Stock Images".equals(album.getName())) {
            uploadPhotoButton.setVisible(false);
        } else {
            uploadPhotoButton.setVisible(true);
        }
    }
    
    private void updatePhotoDisplay() {
        photoTilePane.getChildren().clear();
        if (album != null) {
            for (Photo photo : album.getPhotos()) {
                addPhotoToTile(photo);
            }
        }
    }
    
    private void addPhotoToTile(Photo photo) {
        // Load image and create an ImageView, etc.
        Image image = new Image(getImageInputStream(photo.getPath()));
        ImageView photoIcon = new ImageView(image);
        photoIcon.setFitWidth(150);
        photoIcon.setFitHeight(150);
        photoIcon.setPreserveRatio(true);
        
        photoIcon.setOnMouseClicked((MouseEvent event) -> {
            openPhoto(image, event);
        });
        
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(5);
        Label nameLabel = new Label(new java.io.File(photo.getPath()).getName());
        Label captionLabel = new Label(photo.getCaption());
        container.getChildren().addAll(photoIcon, nameLabel, captionLabel);
        photoTilePane.getChildren().add(container);
    }
    
    private FileInputStream getImageInputStream(String path) {
        // Implementation that distinguishes resource vs. file paths.
        try {
            if (path.startsWith("/")) {
                URL resourceUrl = getClass().getResource(path);
                if (resourceUrl != null) {
                    return new FileInputStream(new java.io.File(resourceUrl.toURI()));
                } else {
                    throw new RuntimeException("Resource not found: " + path);
                }
            } else {
                java.io.File file = new java.io.File(path);
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
    
    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        // Although the upload button should be hidden for "Stock Images",
        // we add an extra check here.
        if (album != null && "Stock Images".equals(album.getName())) {
        	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error");
        	alert.setHeaderText(null);
            alert.setContentText("Upload not allowed for Stock Images album.");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
            alert.showAndWait();
            return;
        }
        
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
                    // Create a new Photo using the absolute file path.
                    Photo newPhoto = new Photo(file.getAbsolutePath());
                    // Prompt user for a caption. If nothing is entered, it is assumed there is no caption.
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Add a Caption");
                    dialog.setHeaderText("Please enter a caption for the photo (optional):");
                    dialog.setContentText("Caption:");
                    
                    Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                    dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
                    
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        String caption = result.get().trim();
                        if(!caption.isEmpty()) {
                        	newPhoto.setCaption(caption);
                        }
                    }
                    // Add the photo to the album.
                    album.addPhoto(newPhoto);
                    // Update the display by adding the new photo.
                    addPhotoToTile(newPhoto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
    
    private void openPhoto(Image photo, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photoDetail.fxml"));
            Parent detailRoot = loader.load();
            PhotoDetailController detailController = loader.getController();
            detailController.setImage(photo);
            Scene currentScene = photoTilePane.getScene();
            detailController.setPreviousScene(currentScene);
            Scene detailScene = new Scene(detailRoot, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(detailScene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
