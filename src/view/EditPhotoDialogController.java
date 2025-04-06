package view;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Photo;

/**
 * Controller for the Edit Photo dialog.
 * It allows the user to edit the caption and add tags (with three different tag types).
 * Tags are stored in the format "type:value".
 *
 * @author Elvis Vasquez
 */
public class EditPhotoDialogController {

    @FXML 
    private TextField captionField;
    
    @FXML 
    private ComboBox<String> tagTypeCombo;
    
    @FXML 
    private VBox tagInputArea;
    
    // List to store tags in the format "type:value"
    private final ArrayList<String> tags = new ArrayList<>();
    
    // The photo that is currently being edited.
    private Photo photo;
    
    /**
     * Sets the photo to be edited and pre-fills the caption.
     * (If your Photo class later supports retrieving existing tags, you could load them into the tags list here.)
     *
     * @param photo the Photo object to edit
     */
    public void setPhoto(Photo photo) {
        this.photo = photo;
        // Pre-fill caption with the photo's current caption.
        captionField.setText(photo.getCaption());
        // (Optional: load photo's existing tags into the tags list, if needed.)
    }
    
    @FXML
    public void initialize() {
        // Populate the tag type combo box with default tag types.
        tagTypeCombo.setItems(FXCollections.observableArrayList("location", "people", "other"));
        tagTypeCombo.getSelectionModel().selectFirst();
        updateTagInputArea();
        
        // Update the tag input area when the tag type selection changes.
        tagTypeCombo.setOnAction(e -> updateTagInputArea());
    }
    
    /**
     * Updates the tag input area (the VBox) based on the selected tag type.
     * For "location": shows one text field.
     * For "people": shows a text field with an "Add" button.
     * For "other": shows two text fields for tag type and tag value.
     */
    private void updateTagInputArea() {
        tagInputArea.getChildren().clear();
        String selected = tagTypeCombo.getSelectionModel().getSelectedItem();
        if ("location".equals(selected)) {
            TextField tf = new TextField();
            tf.setPromptText("Enter location");
            tagInputArea.getChildren().add(tf);
        } else if ("people".equals(selected)) {
            TextField tf = new TextField();
            tf.setPromptText("Enter person's name");
            Button addButton = new Button("Add");
            addButton.setOnAction(e -> {
                String person = tf.getText().trim();
                if (!person.isEmpty()) {
                    // Save each person tag in the format "person:name"
                    tags.add("person:" + person);
                    tf.clear();
                }
            });
            HBox hbox = new HBox(5, tf, addButton);
            tagInputArea.getChildren().add(hbox);
        } else if ("other".equals(selected)) {
            HBox hbox = new HBox(5);
            Label typeLabel = new Label("Tag Type:");
            TextField typeField = new TextField();
            typeField.setPromptText("Enter tag type");
            TextField valueField = new TextField();
            valueField.setPromptText("Enter tag value");
            hbox.getChildren().addAll(typeLabel, typeField, valueField);
            tagInputArea.getChildren().add(hbox);
        }
    }
    
    /**
     * Handles adding a tag based on the currently selected tag type.
     */
    @FXML
    private void handleAddTag() {
        String selected = tagTypeCombo.getSelectionModel().getSelectedItem();
        if ("location".equals(selected)) {
            TextField tf = (TextField) tagInputArea.getChildren().get(0);
            String loc = tf.getText().trim();
            if (!loc.isEmpty()) {
                tags.add("location:" + loc);
                tf.clear();
            }
        } else if ("people".equals(selected)) {
            // For people, the add button in the HBox already adds names.
            // Optionally, you could provide feedback or show a list of added names.
        } else if ("other".equals(selected)) {
            HBox hbox = (HBox) tagInputArea.getChildren().get(0);
            TextField typeField = (TextField) hbox.getChildren().get(1);
            TextField valueField = (TextField) hbox.getChildren().get(2);
            String tagType = typeField.getText().trim();
            String tagValue = valueField.getText().trim();
            if (!tagType.isEmpty() && !tagValue.isEmpty()) {
                tags.add(tagType + ":" + tagValue);
                typeField.clear();
                valueField.clear();
            }
        }
    }
    
    /**
     * Finalizes the editing by updating the photo's caption and tags, then closes the dialog.
     */
    @FXML
    private void handleDone() {
        photo.setCaption(captionField.getText().trim());
        // Update the photo's tags.
        photo.setTags(tags);
        // Close the dialog window.
        Stage stage = (Stage) captionField.getScene().getWindow();
        stage.close();
    }
}
