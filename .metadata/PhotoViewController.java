package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.PhotoService;

/**
 * Controller for the photo viewing screen.
 * Manages photo display, editing, copying/moving, searching, and slideshow functionality.
 * 
 * @author Elvis Vasquez
 */
public class PhotoViewController implements Initializable {

    @FXML private TilePane photoTilePane;
    @FXML private Button uploadPhotoButton;
    @FXML private Button createAlbumButton; // appears only when search results are active

    private Album album;
    // When search is active, these photos are shown.
    private List<Photo> searchResults = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // No additional initialization needed here.
    }

    /**
     * Sets the album whose photos will be displayed.
     * Hides the upload button if the album is the special "Stock Images" album.
     *
     * @param album the album to display
     * @throws MalformedURLException 
     */
    public void setAlbum(Album album) throws MalformedURLException {
        this.album = album;
        updatePhotoDisplay();
        // Hide upload button for the Stock Images album.
        if ("Stock Images".equals(album.getName())) {
            if (uploadPhotoButton != null) {
                uploadPhotoButton.setVisible(false);
            }
        } else {
            if (uploadPhotoButton != null) {
                uploadPhotoButton.setVisible(true);
            }
        }
        if (createAlbumButton != null) {
            createAlbumButton.setVisible(false);
        }
    }

    /**
     * Refreshes the photo display in the TilePane.
     * If a search is active, shows the search results; otherwise, shows all photos in the album.
     * @throws MalformedURLException 
     */
    private void updatePhotoDisplay() throws MalformedURLException {
        photoTilePane.getChildren().clear();
        List<Photo> displayPhotos = (searchResults != null) ? searchResults : album.getPhotos();
        for (Photo photo : displayPhotos) {
            addPhotoToTile(photo);
        }
    }

    /**
     * Opens the edit dialog for the specified photo.
     *
     * @param photo the photo to edit
     */
    private void editPhoto(Photo photo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/editPhotoDialog.fxml"));
            Parent root = loader.load();
            EditPhotoDialogController controller = loader.getController();
            controller.setPhoto(photo);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Photo");
            stage.getIcons().add(new Image(loadDataImage("icon.png")));
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            updatePhotoDisplay();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Copies the specified photo to another album (without removing it from the current album).
     *
     * @param photo the photo to copy
     */
    private void copyPhoto(Photo photo) {
        String imageUrl = PhotoService.getImageURL(photo.getPath());
        ArrayList<model.Album> albums = AlbumController.currentUser.getAlbums();
        ArrayList<String> albumNames = new ArrayList<>();
        for (model.Album a : albums) {
            albumNames.add(a.getName());
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(albumNames.get(0), albumNames);
        dialog.setTitle("Copy Photo");
        dialog.setHeaderText("Select the album to copy the photo to:");
        dialog.setContentText("Album:");
        
        ImageView iv = new ImageView(new Image(imageUrl));
        iv.setFitWidth(150);
        iv.setFitHeight(150);
        iv.setPreserveRatio(true);
        dialog.setGraphic(iv);
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(loadDataImage("icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String chosenAlbumName = result.get();
            model.Album destAlbum = albums.stream()
                    .filter(a -> a.getName().equals(chosenAlbumName))
                    .findFirst().orElse(null);
            if (destAlbum != null) {
                if (!destAlbum.getPhotos().contains(photo)) {
                    destAlbum.addPhoto(photo);
                    showInfo("Photo Copied", "Photo copied to album: " + chosenAlbumName);
                } else {
                    showInfo("Photo Already Exists", "This photo is already in album: " + chosenAlbumName);
                }
            }
        }
    }
    
    /**
     * Moves the specified photo from the current album to another album.
     * (Removal from the current album is performed.)
     *
     * @param photo the photo to move
     * @throws MalformedURLException 
     */
    private void movePhoto(Photo photo) throws MalformedURLException {
        String imageUrl = PhotoService.getImageURL(photo.getPath());
        ArrayList<model.Album> albums = AlbumController.currentUser.getAlbums();
        ArrayList<String> destinationNames = new ArrayList<>();
        for (model.Album a : albums) {
            if (!a.getName().equals(album.getName())) {
                destinationNames.add(a.getName());
            }
        }
        if (destinationNames.isEmpty()) {
            showInfo("No Destination", "No other album available to move the photo to.");
            return;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(destinationNames.get(0), destinationNames);
        dialog.setTitle("Move Photo");
        dialog.setHeaderText("Select the album to move the photo to:");
        dialog.setContentText("Album:");
        
        ImageView iv = new ImageView(new Image(imageUrl));
        iv.setFitWidth(150);
        iv.setFitHeight(150);
        iv.setPreserveRatio(true);
        dialog.setGraphic(iv);
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(loadDataImage("icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String chosenAlbumName = result.get();
            model.Album destAlbum = albums.stream()
                    .filter(a -> a.getName().equals(chosenAlbumName))
                    .findFirst().orElse(null);
            if (destAlbum != null) {
                album.getPhotos().remove(photo);
                destAlbum.addPhoto(photo);
                updatePhotoDisplay();
                showInfo("Photo Moved", "Photo moved to album: " + chosenAlbumName);
            }
        }
    }
    
    /**
     * Creates a visual tile for a photo, including thumbnail, labels, and action buttons.
     *
     * @param photo the photo to represent
     * @throws MalformedURLException 
     */
    private void addPhotoToTile(Photo photo) throws MalformedURLException {
        Image image;
        try {
            // Use the PhotoService to load the image.
            image = PhotoService.loadImage(photo.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        ImageView photoIcon = new ImageView(image);
        photoIcon.setFitWidth(150);
        photoIcon.setFitHeight(150);
        photoIcon.setPreserveRatio(true);
        
        // Create a remove button (only if not in the "Stock Images" album).
        Button removeButton = new Button();
        removeButton.setStyle("-fx-background-color: transparent;");
        Image removeIcon = new Image(loadDataImage("remove_icon.png"));
        ImageView removeIconView = new ImageView(removeIcon);
        removeIconView.setFitWidth(25);
        removeIconView.setFitHeight(25);
        removeButton.setGraphic(removeIconView);
        Tooltip.install(removeButton, new Tooltip("Remove"));
        removeButton.setOnAction(e -> {
            album.getPhotos().remove(photo);
            try {
				updatePhotoDisplay();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            e.consume();
        });
        
        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(photoIcon);
        if (!"Stock Images".equals(album.getName())) {
            imageContainer.getChildren().add(removeButton);
            StackPane.setAlignment(removeButton, Pos.TOP_RIGHT);
            StackPane.setMargin(removeButton, new Insets(5));
        }
        imageContainer.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof Button)) {
                openPhoto(photo, event);
            }
        });
        
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(5);
        Label nameLabel = new Label(new File(photo.getPath()).getName());
        Label captionLabel = new Label(photo.getCaption());
        if (!"Stock Images".equals(album.getName())) {
            Button editButton = new Button("Edit");
            editButton.setOnAction(e -> editPhoto(photo));
            Button copyButton = new Button("Copy");
            copyButton.setOnAction(e -> copyPhoto(photo));
            Button moveButton = new Button("Move");
            moveButton.setOnAction(e -> {
				try {
					movePhoto(photo);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
            HBox buttonsBox = new HBox(10, editButton, copyButton, moveButton);
            buttonsBox.setAlignment(Pos.CENTER);
            container.getChildren().addAll(imageContainer, nameLabel, captionLabel, buttonsBox);
        } else {
            container.getChildren().addAll(imageContainer, nameLabel, captionLabel);
        }
        photoTilePane.getChildren().add(container);
    }
    
    /**
     * Opens the photo detail view for the given photo.
     *
     * @param photo the photo to view in detail
     * @param event the mouse event that triggered the action
     */
    private void openPhoto(Photo photo, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photoDetail.fxml"));
            Parent detailRoot = loader.load();
            PhotoDetailController detailController = loader.getController();
            detailController.setPhoto(photo);
            
            FileInputStream imageStream = getImageInputStream(photo.getPath());
            if (imageStream == null) {
                // Try using PhotoService instead
                Image image = PhotoService.loadImage(photo.getPath());
                detailController.setImage(image);
            } else {
                detailController.setImage(new Image(imageStream));
            }
            
            Scene currentScene = photoTilePane.getScene();
            detailController.setPreviousScene(currentScene);
            Scene detailScene = new Scene(detailRoot, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(detailScene);
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Error opening photo: " + ex.getMessage());
        }
    }
    
    /**
     * Handles the uploading of new photos.
     *
     * @param event the action event triggered by the upload button
     */
    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        if (album != null && "Stock Images".equals(album.getName())) {
            showError("Upload not allowed for Stock Images album.");
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
            String username = AlbumController.currentUser.getUsername();
            Path albumDir = Path.of(app.Photos.usersDir, username, album.getName());
            try {
                Files.createDirectories(albumDir);
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }
            for (File file : files) {
                try {
                    Path destination = albumDir.resolve(file.getName());
                    byte[] data = Files.readAllBytes(file.toPath());
                    Files.write(destination, data);
                    java.nio.file.attribute.FileTime fileTime = Files.getLastModifiedTime(file.toPath());
                    Files.setLastModifiedTime(destination, fileTime);
                    
                    Photo newPhoto = new Photo(destination.toString());
                    
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Add a Caption");
                    dialog.setHeaderText("Please enter a caption for the photo (optional):");
                    dialog.setContentText("Caption:");
                    
                    String p = newPhoto.getPath();
                    String imageUrl = PhotoService.getImageURL(p);
                    ImageView iv = new ImageView(new Image(imageUrl));
                    iv.setFitWidth(150);
                    iv.setFitHeight(150);
                    iv.setPreserveRatio(true);
                    dialog.setGraphic(iv);
                    
                    Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                    dialogStage.getIcons().add(new Image(loadDataImage("icon.png")));
                    
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        String caption = result.get().trim();
                        if (!caption.isEmpty()) {
                            newPhoto.setCaption(caption);
                        }
                    }
                    album.addPhoto(newPhoto);
                    addPhotoToTile(newPhoto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Handles the action of returning to the album view.
     *
     * @param event the action event triggered by the Back button
     * @throws IOException if the home.fxml file cannot be loaded
     */
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent albumView = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
        Scene albumScene = new Scene(albumView, 600, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(albumScene);
        stage.show();
    }
    
    /**
     * Handles photo search based on either a date range or a tag search.
     * Only one type of search is allowed at a time.
     *
     * @param event the action event triggered by the Search button
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/searchDialog.fxml"));
            Parent root = loader.load();
            SearchDialogController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Search Photos");
            stage.getIcons().add(new Image(loadDataImage("icon.png")));
            stage.setScene(new Scene(root));
            stage.showAndWait();

            Optional<SearchDialogController.SearchCriteria> optionalCriteria = controller.getCriteria();
            if (optionalCriteria.isPresent()) {
                SearchDialogController.SearchCriteria criteria = optionalCriteria.get();
                List<Photo> filtered = new ArrayList<>(album.getPhotos());
                // CASE 1: Date Search (if both start and end dates are provided and tag fields are empty)
                if (criteria.startDate != null && criteria.endDate != null
                    && criteria.tagType1.isEmpty() && criteria.tagValue1.isEmpty()) {
                    filtered = filtered.stream()
                            .filter(photo -> {
                                LocalDate photoDate = photo.getDate().toInstant()
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate();
                                return !photoDate.isBefore(criteria.startDate)
                                        && !photoDate.isAfter(criteria.endDate);
                            })
                            .collect(Collectors.toList());
                }
                // CASE 2: Tag Search
                else if (!criteria.tagType1.isEmpty() && !criteria.tagValue1.isEmpty()) {
                    // If only one tag pair is provided, or the second pair/operator is incomplete:
                    if (criteria.tagType2.isEmpty() || criteria.tagValue2.isEmpty() || criteria.operator.isEmpty()){
                        filtered = filtered.stream()
                                .filter(photo ->
                                    photo.getTags().stream()
                                        .anyMatch(tag ->
                                            tag.getTagType().equalsIgnoreCase(criteria.tagType1) &&
                                            tag.getTagValue().equalsIgnoreCase(criteria.tagValue1)
                                        )
                                )
                                .collect(Collectors.toList());
                    }
                    // Otherwise, use two tag pairs with an operator.
                    else {
                        if (criteria.operator.equalsIgnoreCase("AND")) {
                            filtered = filtered.stream()
                                    .filter(photo ->
                                        photo.getTags().stream().anyMatch(tag ->
                                            tag.getTagType().equalsIgnoreCase(criteria.tagType1) &&
                                            tag.getTagValue().equalsIgnoreCase(criteria.tagValue1)
                                        )
                                        &&
                                        photo.getTags().stream().anyMatch(tag ->
                                            tag.getTagType().equalsIgnoreCase(criteria.tagType2) &&
                                            tag.getTagValue().equalsIgnoreCase(criteria.tagValue2)
                                        )
                                    )
                                    .collect(Collectors.toList());
                        } else if (criteria.operator.equalsIgnoreCase("OR")) {
                            filtered = filtered.stream()
                                    .filter(photo ->
                                        photo.getTags().stream().anyMatch(tag ->
                                            (tag.getTagType().equalsIgnoreCase(criteria.tagType1) &&
                                             tag.getTagValue().equalsIgnoreCase(criteria.tagValue1))
                                            ||
                                            (tag.getTagType().equalsIgnoreCase(criteria.tagType2) &&
                                             tag.getTagValue().equalsIgnoreCase(criteria.tagValue2))
                                        )
                                    )
                                    .collect(Collectors.toList());
                        } else {
                            showError("Invalid operator. Use either 'AND' or 'OR'.");
                            return;
                        }
                    }
                } else {
                    showError("Please enter either a complete date range or complete tag search criteria.");
                    return;
                }
                // Set the search results and update display.
                searchResults = filtered;
                updatePhotoDisplay();
                createAlbumButton.setVisible(!filtered.isEmpty());
            } else {
                showError("Search cancelled.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Clears any active search results.
     *
     * @param event the action event triggered by the Clear Search button
     * @throws MalformedURLException 
     */
    @FXML
    private void handleClearSearch(ActionEvent event) throws MalformedURLException {
        searchResults = null;
        updatePhotoDisplay();
        createAlbumButton.setVisible(false);
    }
    
    /**
     * Handles creation of a new album based on the current search results.
     *
     * @param event the action event triggered by the Create Album button
     */
    @FXML
    private void handleCreateAlbum(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album from Search Results");
        dialog.setHeaderText("Enter a name for the new album:");
        dialog.setContentText("Album name:");
        
        ImageView imageView = new ImageView(new Image(loadDataImage("folder_icon.png")));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        dialog.setGraphic(imageView);
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(loadDataImage("folder_icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newAlbumName = result.get().trim();
            if (newAlbumName.isEmpty()) {
                showError("Album name cannot be empty.");
                return;
            }
            boolean exists = AlbumController.currentUser.getAlbums().stream()
                    .anyMatch(album -> album.getName().equalsIgnoreCase(newAlbumName));
            if (exists) {
                showError("An album with the name \"" + newAlbumName + "\" already exists.");
                return;
            }
            Album newAlbum = new Album(newAlbumName);
            newAlbum.getPhotos().addAll(searchResults);
            AlbumController.currentUser.getAlbums().add(newAlbum);
            showInfo("Album Created", "Album created successfully: " + newAlbumName);
        }
    }
    
    /**
     * Handles the manual slideshow functionality.
     *
     * @param event the action event triggered by the Manual Slideshow button
     */
    @FXML
    private void handleManualSlideshow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/slideshow.fxml"));
            Parent root = loader.load();
            SlideshowController controller = loader.getController();
            controller.setPreviousScene(this.photoTilePane.getScene(), (String)((Stage)this.photoTilePane.getScene().getWindow()).getTitle());
            controller.setPhotos(album.getPhotos());
            
            // Get the current stage instead of creating a new one
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.setTitle("Manual Slideshow");
            currentStage.setScene(new Scene(root, 900, 600));
            
            // Optionally store the previous scene to allow returning to it
            // controller.setPreviousScene(currentStage.getScene());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Helper method to obtain a FileInputStream for an image given its path.
     * If the path starts with "file:", it is treated as a file URL.
     *
     * @param path the image path
     * @return a FileInputStream for the image
     */
    private FileInputStream getImageInputStream(String path) {
        try {
            if (path.startsWith("file:")) {
                // Just extract the actual file path from the file: URL
                String filePath = path.substring(5); // Remove "file:"
                
                // Handle Windows paths that start with a drive letter
                if (filePath.startsWith("/") && filePath.length() > 2 && filePath.charAt(2) == ':') {
                    filePath = filePath.substring(1); // Remove leading slash for Windows paths
                }
                
                File file = new File(filePath);
                if (file.exists()) {
                    return new FileInputStream(file);
                } else {
                    System.err.println("File not found: " + file.getAbsolutePath());
                    return null;
                }
            } else if (path.startsWith("/")) {
                // Handle resource paths
                try {
                    URL resourceUrl = getClass().getResource(path);
                    if (resourceUrl != null) {
                        return new FileInputStream(new File(resourceUrl.toURI()));
                    } else {
                        System.err.println("Resource not found: " + path);
                        return null;
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                // Handle regular file paths
                File file = new File(path);
                if (file.exists()) {
                    return new FileInputStream(file);
                } else {
                    System.err.println("File not found: " + file.getAbsolutePath());
                    return null;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method to get a URL string for an image.
     *
     * @param path the image path
     * @return the URL string for the image
     */
    public static String getImageURL(String path) {
        if (path.startsWith("file:")) {
            return path; // Already a file URL.
        } else if (path.startsWith("/")) {
            URL resourceUrl = PhotoViewController.class.getResource(path);
            if (resourceUrl != null) {
                return resourceUrl.toExternalForm();
            } else {
                throw new RuntimeException("Resource not found: " + path);
            }
        } else {
            return "file:" + path;
        }
    }
    
    /**
     * Helper method to show informational alerts.
     *
     * @param title   the title of the alert
     * @param message the message content of the alert
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(loadDataImage("icon.png")));
        alert.showAndWait();
    }
    
    /**
     * Helper method to show error alerts.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(loadDataImage("icon.png")));
        alert.showAndWait();
    }
    
    /**
     * Helper method to load an image from the external "data" directory.
     * Assumes the "data" folder is located directly under the project directory.
     *
     * @param fileName the name of the image file (e.g. "icon.png")
     * @return a String representing the file URL for the image
     */
    private static String loadDataImage(String fileName) {
        return app.Photos.getDataFileURL(fileName);
    }

}
