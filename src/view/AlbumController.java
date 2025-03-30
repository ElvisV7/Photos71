package view;

import java.io.IOException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class AlbumController {

    // Holds the currently logged-in user.
    public static User currentUser;
    
    @FXML
    private FlowPane albumFlowPane;
    
    @FXML
    private void initialize() {
        // Ensure the default album "Stock Images" exists.
        if (currentUser != null) {
            boolean defaultExists = currentUser.getAlbums().stream()
                    .anyMatch(a -> a.getName().equals("Stock Images"));
            if (!defaultExists) {
                Album defaultAlbum = new Album("Stock Images");
                // Optionally add preloaded stock photos:
                // defaultAlbum.addPhoto(new Photo("path/to/stock/photo1.jpg"));
                currentUser.getAlbums().add(defaultAlbum);
            }
            populateAlbums();
        }
    }
    
    // Populate the FlowPane dynamically with the user's albums.
    private void populateAlbums() {
        albumFlowPane.getChildren().clear();
        for (Album album : currentUser.getAlbums()) {
            albumFlowPane.getChildren().add(createAlbumBox(album));
        }
    }
    
    // Create a visual folder box for the given album.
    private VBox createAlbumBox(Album album) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        
        // Load the folder icon image.
        Image folderImage = new Image(getClass().getResourceAsStream("/view/folder_icon.png"));
        ImageView folderIcon = new ImageView(folderImage);
        folderIcon.setFitWidth(150);    // medium-sized icon width
        folderIcon.setFitHeight(150);   // medium-sized icon height
        folderIcon.setPreserveRatio(true);
        
        Label nameLabel = new Label(album.getName());
        
        box.getChildren().addAll(folderIcon, nameLabel);
        
        // When this folder is clicked, open the album.
        box.setOnMouseClicked((MouseEvent event) -> {
            try {
                openAlbum(album, event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        return box;
    }
    
    // Open the specified album by passing it to the PhotoViewController.
    private void openAlbum(Album album, MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photoView.fxml"));
        Parent photoViewRoot = loader.load();
        PhotoViewController controller = loader.getController();
        controller.setAlbum(album);
        Scene photoScene = new Scene(photoViewRoot, 600, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(photoScene);
        stage.show();
    }
    
    // Handler for the Logout button.
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Parent loginView = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginScene = new Scene(loginView, 600, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
    
    // Handler for the Add Album button.
    @FXML
    private void handleAddAlbum(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Album");
        dialog.setHeaderText("Create a New Album");
        dialog.setContentText("Please enter album name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String albumName = result.get().trim();
            if (!albumName.isEmpty()) {
                boolean exists = currentUser.getAlbums().stream()
                        .anyMatch(a -> a.getName().equals(albumName));
                if (exists) {
                    System.out.println("Album already exists.");
                } else {
                    Album newAlbum = new Album(albumName);
                    currentUser.getAlbums().add(newAlbum);
                    albumFlowPane.getChildren().add(createAlbumBox(newAlbum));
                }
            }
        }
    }
}
