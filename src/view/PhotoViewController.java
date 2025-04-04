package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import model.PhotoService;  // Business logic for image loading

public class PhotoViewController implements Initializable {

    @FXML
    private TilePane photoTilePane;
    
    // Injected from the FXML (if defined)
    @FXML
    private Button uploadPhotoButton;
    @FXML
    private Button createAlbumButton; // Appears only when search results are active

    private Album album;
    // When search is active, these photos are shown.
    private List<Photo> searchResults = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // No additional initialization needed here.
    }

    public void setAlbum(Album album) {
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

    private void updatePhotoDisplay() {
        photoTilePane.getChildren().clear();
        List<Photo> displayPhotos = (searchResults != null) ? searchResults : album.getPhotos();
        for (Photo photo : displayPhotos) {
            addPhotoToTile(photo);
        }
    }

    private void addPhotoToTile(Photo photo) {
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
        
        // Create a remove button (only if not in Stock Images).
        Button removeButton = new Button();
        removeButton.setStyle("-fx-background-color: transparent;");
        Image removeIcon = new Image(getClass().getResourceAsStream("/view/remove_icon.png"));
        ImageView removeIconView = new ImageView(removeIcon);
        removeIconView.setFitWidth(25);
        removeIconView.setFitHeight(25);
        removeButton.setGraphic(removeIconView);
        Tooltip.install(removeButton, new Tooltip("remove"));
        removeButton.setOnAction(e -> {
            album.getPhotos().remove(photo);
            updatePhotoDisplay();
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
        
        // Create extra buttons if the album is not "Stock Images"
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
            moveButton.setOnAction(e -> movePhoto(photo));
            HBox buttonsBox = new HBox(10, editButton, copyButton, moveButton);
            buttonsBox.setAlignment(Pos.CENTER);
            container.getChildren().addAll(imageContainer, nameLabel, captionLabel, buttonsBox);
        } else {
            container.getChildren().addAll(imageContainer, nameLabel, captionLabel);
        }
        photoTilePane.getChildren().add(container);
    }

    private void editPhoto(Photo photo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/editPhotoDialog.fxml"));
            Parent root = loader.load();
            EditPhotoDialogController controller = loader.getController();
            controller.setPhoto(photo);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Photo");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            updatePhotoDisplay();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void copyPhoto(Photo photo) {
        String imageUrl = PhotoService.getImageURL(photo.getPath());
        ArrayList<Album> albums = AlbumController.currentUser.getAlbums();
        ArrayList<String> albumNames = new ArrayList<>();
        for (Album a : albums) {
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
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String chosenAlbumName = result.get();
            Album destAlbum = albums.stream()
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
    
    private void movePhoto(Photo photo) {
        String imageUrl = PhotoService.getImageURL(photo.getPath());
        ArrayList<Album> albums = AlbumController.currentUser.getAlbums();
        ArrayList<String> destinationNames = new ArrayList<>();
        for (Album a : albums) {
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
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String chosenAlbumName = result.get();
            Album destAlbum = albums.stream()
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
                    dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
                    
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
    
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent albumView = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
        Scene albumScene = new Scene(albumView, 600, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(albumScene);
        stage.show();
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/searchDialog.fxml"));
            Parent root = loader.load();
            SearchDialogController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Search Photos");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
            stage.setScene(new Scene(root));
            stage.showAndWait();

            Optional<SearchDialogController.SearchCriteria> optionalCriteria = controller.getCriteria();
            if (optionalCriteria.isPresent()) {
                SearchDialogController.SearchCriteria criteria = optionalCriteria.get();
                List<Photo> filtered = new ArrayList<>(album.getPhotos());
                
                // Allow either a date range search or a tag search (not both).
                if (criteria.startDate != null && criteria.endDate != null
                        && criteria.tagType.isEmpty() && criteria.tagValue.isEmpty()) {
                    filtered = filtered.stream()
                            .filter(photo -> {
                                LocalDate photoDate = photo.getDate().toInstant()
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate();
                                return !photoDate.isBefore(criteria.startDate)
                                        && !photoDate.isAfter(criteria.endDate);
                            })
                            .collect(Collectors.toList());
                } else if (!criteria.tagType.isEmpty() && !criteria.tagValue.isEmpty()
                        && criteria.startDate == null && criteria.endDate == null) {
                    filtered = filtered.stream()
                            .filter(photo ->
                                photo.getTags().stream()
                                    .anyMatch(tag -> tag.equalsIgnoreCase(criteria.tagType + ":" + criteria.tagValue))
                            )
                            .collect(Collectors.toList());
                } else {
                    showError("Please enter either a date range OR a tag search, not both.");
                    return;
                }
                searchResults = filtered;
                updatePhotoDisplay();
                createAlbumButton.setVisible(!filtered.isEmpty());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void handleClearSearch(ActionEvent event) {
        searchResults = null;
        updatePhotoDisplay();
        createAlbumButton.setVisible(false);
    }
    
    @FXML
    private void handleCreateAlbum(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album from Search Results");
        dialog.setHeaderText("Enter a name for the new album:");
        dialog.setContentText("Album name:");
        
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/view/folder_icon.png")));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        dialog.setGraphic(imageView);
        
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/view/folder_icon.png")));
        
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
    
    @FXML
    private void handleManualSlideshow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/slideshow.fxml"));
            Parent root = loader.load();
            SlideshowController controller = loader.getController();
            controller.setPhotos(album.getPhotos());
            
            Stage stage = new Stage();
            stage.setTitle("Manual Slideshow");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void openPhoto(Photo photo, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photoDetail.fxml"));
            Parent detailRoot = loader.load();
            PhotoDetailController detailController = loader.getController();
            detailController.setPhoto(photo);
            detailController.setImage(new Image(getImageInputStream(photo.getPath())));
            Scene currentScene = photoTilePane.getScene();
            detailController.setPreviousScene(currentScene);
            Scene detailScene = new Scene(detailRoot, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(detailScene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private FileInputStream getImageInputStream(String path) {
        try {
            if (path.startsWith("/")) {
                URL resourceUrl = getClass().getResource(path);
                if (resourceUrl != null) {
                    return new FileInputStream(new File(resourceUrl.toURI()));
                } else {
                    throw new RuntimeException("Resource not found: " + path);
                }
            } else {
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
    
    public static String getImageURL(String path) {
        if (path.startsWith("/")) {
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
    
    // Helper method to show informational alerts.
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
        alert.showAndWait();
    }
    
    // Helper method to show error alerts.
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
        alert.showAndWait();
    }
    
    // Helper method to get a URL string for an image.
    public static String getImageURLFromPath(String path) {
        if (path.startsWith("/")) {
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
}
