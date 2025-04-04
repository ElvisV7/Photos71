package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.text.SimpleDateFormat;

public class AlbumController {

    // Holds the currently logged-in user.
    public static User currentUser;
    
    @FXML
    private TilePane albumTilePane;
    
    @FXML
    private void initialize() {
        // populate albums
        if (currentUser != null) {
            populateAlbums();
        }
    }
    
    // Populate the TilePane dynamically with the user's albums.
    private void populateAlbums() {
        albumTilePane.getChildren().clear();
        for (Album album : currentUser.getAlbums()) {
            albumTilePane.getChildren().add(createAlbumBox(album));
        }
    }
    
    private void removeAlbum(Album album) {
        currentUser.getAlbums().remove(album);
        populateAlbums();  // Refresh the TilePane
    }
    
    private void renameAlbum(Album album) {
        int index = currentUser.getAlbums().indexOf(album);
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Please enter a new name for the album:");
        dialog.setContentText("Name:");
        
        // Set the graphic to display the icon
        ImageView imageView = new ImageView(new Image("/view/folder_icon.png"));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        dialog.setGraphic(imageView);
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newAlbumName = result.get().trim();
            if(newAlbumName.equals("Stock Images")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid name!");
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
                alert.showAndWait();
                return;
            }
            if (newAlbumName.isEmpty()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Album name cannot be empty");
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
                alert.showAndWait();
                return;
            }
            ArrayList<Album> userAlbums = currentUser.getAlbums();
            if (userAlbums.contains(new Album(newAlbumName))) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Album name: " + newAlbumName + " is already in use!");
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
                alert.showAndWait();
                return;
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Album Name Changed");
            alert.setHeaderText(null);
            alert.setContentText("Album name was changed!");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
            alert.showAndWait();
            currentUser.getAlbums().get(index).changeName(newAlbumName);
        }
        populateAlbums();  // Refresh the TilePane
    }
    
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
        Label numberLabel = new Label("Number of photos: " + album.getPhotos().size());
        
        // Sort the list based on dates from earliest to latest, but only if the album
        // isn't empty
        if(album.getPhotos().size() != 0) {
        	album.getPhotos().sort((p1, p2) -> p1.getDate().compareTo(p2.getDate()));

            // Format the earliest date (first element) as a String
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String earliestDate = sdf.format(album.getPhotos().get(0).getDate().getTime()); 
            String latestDate = sdf.format(album.getPhotos().get(album.getPhotos().size()-1).getDate().getTime());
            
            Label datesLabel = new Label("Dates: " + earliestDate + " - " + latestDate);
            
            // Add the folder icon and album name to the box.
            box.getChildren().addAll(folderIcon, nameLabel, numberLabel, datesLabel);
        }
        else {
	        // Add the folder icon and album name to the box.	
	        box.getChildren().addAll(folderIcon, nameLabel, numberLabel);
        }
	    // Only add "Remove" and "Rename" buttons if the album is not "Stock Images"
        if (!"Stock Images".equals(album.getName())) {
            Button removeButton = new Button("Remove");
            removeButton.setOnAction(e -> removeAlbum(album));
            Button renameButton = new Button("Rename");
            renameButton.setOnAction(e -> renameAlbum(album));
            box.getChildren().addAll(removeButton, renameButton);
        }

        // When clicking the album box (but not on the buttons), open the album.
        box.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof Button)) {
                try {
                    openAlbum(album, event);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
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
        
        // Set the graphic to display the icon
        ImageView imageView = new ImageView(new Image("/view/folder_icon.png"));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        dialog.setGraphic(imageView);
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/view/folder_icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String albumName = result.get().trim();
            if (!albumName.isEmpty()) {
                boolean exists = currentUser.getAlbums().stream()
                        .anyMatch(a -> a.getName().equals(albumName));
                if (exists) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Album name: " + albumName + " is already in use!");
                    Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                    alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/view/folder_icon.png")));
                    alert.showAndWait();
                    return;
                } else {
                    Album newAlbum = new Album(albumName);
                    currentUser.getAlbums().add(newAlbum);
                    albumTilePane.getChildren().add(createAlbumBox(newAlbum));
                }
            }else {
            	Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Album name cannot be empty!");
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/view/folder_icon.png")));
                alert.showAndWait();
            }
        }
    }
}
