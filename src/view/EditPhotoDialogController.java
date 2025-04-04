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

public class EditPhotoDialogController {

    @FXML private TextField captionField;
    @FXML private ComboBox<String> tagTypeCombo;
    @FXML private VBox tagInputArea;
    
    // Store tags in the format "type:value"
    private final ArrayList<String> tags = new ArrayList<>();
    
    // The photo being edited.
    private Photo photo;
    
    public void setPhoto(Photo photo) {
        this.photo = photo;
        // Pre-fill caption with the photo's current caption.
        captionField.setText(photo.getCaption());
        // (Optional: if Photo supports retrieving tags, load them into tags list.)
    }
    
    @FXML
    public void initialize() {
        // Populate the combo box with default tag types.
        tagTypeCombo.setItems(FXCollections.observableArrayList("location", "people", "other"));
        tagTypeCombo.getSelectionModel().selectFirst();
        updateTagInputArea();
        
        // When the selection changes, update the input area.
        tagTypeCombo.setOnAction(e -> updateTagInputArea());
    }
    
    // Update the tag input area based on the selected tag type.
    private void updateTagInputArea() {
        tagInputArea.getChildren().clear();
        String selected = tagTypeCombo.getSelectionModel().getSelectedItem();
        if ("location".equals(selected)) {
            // For location, show one text field.
            TextField tf = new TextField();
            tf.setPromptText("Enter location");
            tagInputArea.getChildren().add(tf);
        } else if ("people".equals(selected)) {
            // For people, show a text field and an "Add" button.
            TextField tf = new TextField();
            tf.setPromptText("Enter person's name");
            Button addButton = new Button("Add");
            addButton.setOnAction(e -> {
                String person = tf.getText().trim();
                if (!person.isEmpty()) {
                    // Store each person as "person:name"
                    tags.add("person:" + person);
                    tf.clear();
                }
            });
            HBox hbox = new HBox(5, tf, addButton);
            tagInputArea.getChildren().add(hbox);
        } else if ("other".equals(selected)) {
            // For other, show two text fields: one for tag type and one for tag value.
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
            // For "people," the add button in the HBox already adds names.
            // Optionally, you could display a summary alert here.
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
    
    @FXML
    private void handleDone() {
        // Update the photo's caption.
        photo.setCaption(captionField.getText().trim());
        // Update the photo's tags. (Assuming your Photo class has a setTags(List<String>) method.)
        photo.setTags(tags);
        // Close the dialog window.
        Stage stage = (Stage) captionField.getScene().getWindow();
        stage.close();
    }
}
