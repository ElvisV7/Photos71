package view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import model.Album;
import model.User;

/**
 * Controller for the album view.
 * Displays each album with its icon, name, number of photos, and date range.
 * Provides options to remove or rename albums (except for the default "Stock Images").
 * 
 * @author Elvis Vasquez
 */
public class AlbumController {

    // Holds the currently logged-in user.
    public static User currentUser;
    
    @FXML
    private TilePane albumTilePane;
    
    @FXML
    private void initialize() {
        // Populate albums if a user is logged in.
        if (currentUser != null) {
            populateAlbums();
        }
    }
    
    /**
     * Populates the TilePane dynamically with the user's albums.
     */
    private void populateAlbums() {
        albumTilePane.getChildren().clear();
        for (Album album : currentUser.getAlbums()) {
            albumTilePane.getChildren().add(createAlbumBox(album));
        }
    }
    
    /**
     * Removes the specified album from the user's list and refreshes the display.
     *
     * @param album the album to remove
     */
    private void removeAlbum(Album album) {
        currentUser.getAlbums().remove(album);
        populateAlbums();  // Refresh the TilePane
    }
    
    /**
     * Opens a dialog to rename an album.
     *
     * @param album the album to rename
     */
    private void renameAlbum(Album album) {
        int index = currentUser.getAlbums().indexOf(album);
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Please enter a new name for the album:");
        dialog.setContentText("Name:");
        
        // Set the graphic to display the folder icon.
        ImageView imageView = new ImageView(new Image(loadDataImageSafely("folder_icon.png")));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        dialog.setGraphic(imageView);
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(loadDataImageSafely("icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newAlbumName = result.get().trim();
            if (newAlbumName.equals("Stock Images")) {
                showError("The name \"Stock Images\" is reserved.");
                return;
            }
            if (newAlbumName.isEmpty()) {
                showError("Album name cannot be empty.");
                return;
            }
            ArrayList<Album> userAlbums = currentUser.getAlbums();
            if (userAlbums.contains(new Album(newAlbumName))) {
                showError("Album name: " + newAlbumName + " is already in use!");
                return;
            }
            showInfo("Album Name Changed", "Album name was changed!");
            currentUser.getAlbums().get(index).changeName(newAlbumName);
        }
        populateAlbums();  // Refresh the TilePane
    }
    
    /**
     * Creates a visual box (VBox) representing the album with its icon, name, photo count, and date range.
     * Also adds remove and rename buttons if the album is not "Stock Images".
     *
     * @param album the album to represent
     * @return a VBox containing the album's UI components
     */
    private VBox createAlbumBox(Album album) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);

        // Load the folder icon image from the external data folder.
        Image folderImage = new Image( ("folder_icon.png"));
        ImageView folderIcon = new ImageView(folderImage);
        folderIcon.setFitWidth(150);    // medium-sized icon width
        folderIcon.setFitHeight(150);   // medium-sized icon height
        folderIcon.setPreserveRatio(true);

        Label nameLabel = new Label(album.getName());
        Label numberLabel = new Label("Number of photos: " + album.getPhotos().size());
        
        // If the album is not empty, sort the photos by date and show the date range.
        if (album.getPhotos().size() != 0) {
            album.getPhotos().sort((p1, p2) -> p1.getDate().compareTo(p2.getDate()));

            // Format the earliest and latest dates.
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String earliestDate = sdf.format(album.getPhotos().get(0).getDate().getTime()); 
            String latestDate = sdf.format(album.getPhotos().get(album.getPhotos().size() - 1).getDate().getTime());
            
            Label datesLabel = new Label("Dates: " + earliestDate + " - " + latestDate);
            
            box.getChildren().addAll(folderIcon, nameLabel, numberLabel, datesLabel);
        } else {
            box.getChildren().addAll(folderIcon, nameLabel, numberLabel);
        }
        
        // Only add Remove and Rename buttons if the album is not "Stock Images".
        if (!"Stock Images".equals(album.getName())) {
            Button removeButton = new Button("Remove");
            removeButton.setOnAction(e -> removeAlbum(album));
            Button renameButton = new Button("Rename");
            renameButton.setOnAction(e -> renameAlbum(album));
            box.getChildren().addAll(removeButton, renameButton);
        }

        // Set a click handler on the box to open the album (if the click target is not a button).
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
    
    /**
     * Opens the specified album by loading the PhotoView FXML and switching scenes.
     *
     * @param album the album to open
     * @param event the triggering mouse event
     * @throws IOException if the FXML cannot be loaded
     */
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
    
    /**
     * Handles the Logout action by switching back to the login scene.
     *
     * @param event the action event triggered by clicking the Logout button
     * @throws IOException if the login FXML cannot be loaded
     */
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Parent loginView = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginScene = new Scene(loginView, 600, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
    
    /**
     * Handles the Add Album action by prompting the user for a new album name and creating a new album.
     *
     * @param event the action event triggered by clicking the Add Album button
     */
    @FXML
    private void handleAddAlbum(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Album");
        dialog.setHeaderText("Create a New Album");
        dialog.setContentText("Please enter album name:");
        
        // Set the graphic using the folder icon from the data directory.
        ImageView imageView = new ImageView(new Image(loadDataImageSafely("folder_icon.png")));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        dialog.setGraphic(imageView);
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(loadDataImageSafely("folder_icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String albumName = result.get().trim();
            if (!albumName.isEmpty()) {
                boolean exists = currentUser.getAlbums().stream()
                        .anyMatch(a -> a.getName().equalsIgnoreCase(albumName));
                if (exists) {
                    showError("Album name: " + albumName + " is already in use!");
                    return;
                } else {
                    Album newAlbum = new Album(albumName);
                    currentUser.getAlbums().add(newAlbum);
                    albumTilePane.getChildren().add(createAlbumBox(newAlbum));
                }
            } else {
                showError("Album name cannot be empty!");
            }
        }
    }
    
    /**
     * Helper method to show an informational alert.
     *
     * @param title the title of the alert
     * @param message the informational message
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(loadDataImageSafely("icon.png")));
        alert.showAndWait();
    }
    
    /**
     * Helper method to show an error alert.
     *
     * @param message the error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(loadDataImageSafely("icon.png")));
        alert.showAndWait();
    }
    
    /**
     * Helper method to load an image from the external "data" directory.
     * This method builds a file URL from the project’s root "data" folder.
     *
     * @param fileName the name of the image file (e.g. "icon.png")
     * @return a String representing the file URL for the image; if not found, returns a minimal fallback image URL.
     */
    private static String loadDataImageSafely(String fileName) {
        String baseDir = System.getProperty("user.dir") + File.separator + "data";
        String fullPath = baseDir + File.separator + fileName;
        File imageFile = new File(fullPath);
        if (imageFile.exists()) {
            return imageFile.toURI().toString();
        } else {
            System.err.println("Warning: " + fullPath + " not found. Using fallback image.");
            return "data:,";
        }
    }
}
