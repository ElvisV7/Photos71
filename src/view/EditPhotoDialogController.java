package view;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Photo;
import util.TagTypeManager;

/**
 * Controller for the Edit Photo dialog.
 * It allows the user to edit the caption and add tags (with three different tag types).
 * Tags are stored in the format "type:value".
 * 
 * @author Elvis Vasquez & Tyler Gehringer
 */
public class EditPhotoDialogController {
    @FXML 
    private TextField captionField;
    
    @FXML 
    private ComboBox<String> tagTypeCombo;
    
    @FXML 
    private VBox tagInputArea;
    
    // List to store tags (each represented as model.Tag)
    private final ArrayList<model.Tag> tags = new ArrayList<>();
    
    // The photo that is currently being edited.
    private Photo photo;
    
    private TagTypeManager tagTypeManager;
    
    /**
     * Sets the photo to be edited and pre-fills the caption.
     *
     * @param photo the Photo object to edit
     */
    public void setPhoto(Photo photo) {
        this.photo = photo;
        captionField.setText(photo.getCaption());
        
        if (photo.getTags() != null) {
            tags.clear();
            tags.addAll(photo.getTags());
        }
    }
    
    @FXML
    public void initialize() {
        tagTypeManager = TagTypeManager.loadTagTypes();
        
        ArrayList<String> presetTypes = new ArrayList<>(tagTypeManager.getAllowedTagTypes().keySet());
        presetTypes.add("other");
        tagTypeCombo.setItems(FXCollections.observableArrayList(presetTypes));
        tagTypeCombo.getSelectionModel().selectFirst();
        updateTagInputArea();
        
        tagTypeCombo.setOnAction(e -> updateTagInputArea());
    }
    
    private void updateTagInputArea() {
        tagInputArea.getChildren().clear();
        String selected = tagTypeCombo.getSelectionModel().getSelectedItem();
        if ("location".equalsIgnoreCase(selected)) {
            TextField tf = new TextField();
            tf.setPromptText("Enter location");
            tagInputArea.getChildren().add(tf);
        } else if ("people".equalsIgnoreCase(selected)) {
            TextField tf = new TextField();
            tf.setPromptText("Enter person's name");
            Button addButton = new Button("Add");
            addButton.setOnAction(e -> {
                String person = tf.getText().trim();
                if (!person.isEmpty()) {
                    // Save the person tag in the format "person:name"
                    tags.add(new model.Tag("person", person));
                    tf.clear();
                }
            });
            HBox hbox = new HBox(5, tf, addButton);
            tagInputArea.getChildren().add(hbox);
        } else if ("other".equalsIgnoreCase(selected)) {
            HBox hbox = new HBox(5);
            Label typeLabel = new Label("Tag Type:");
            TextField typeField = new TextField();
            Label valueLabel = new Label("Tag Value:");
            TextField valueField = new TextField();
            valueField.setPromptText("Enter tag value");
            Label multi = new Label("Allow Multiple?");
            
            ComboBox<String> allowCombo = new ComboBox<>(FXCollections.observableArrayList("Yes", "No"));
            allowCombo.getSelectionModel().select("No");
            
            hbox.getChildren().addAll(typeLabel, typeField, valueLabel, valueField, multi, allowCombo);
            tagInputArea.getChildren().add(hbox);
        } else {
            // For any other custom preset tag from TagTypeManager
            TextField tf = new TextField();
            tf.setPromptText("Enter tag value for " + selected);
            tagInputArea.getChildren().add(tf);
        }
    }
    
    /**
     * Handles adding a tag based on the currently selected tag type.
     */
    @FXML
    private void handleAddTag() {
        String selected = tagTypeCombo.getSelectionModel().getSelectedItem();
        if ("location".equalsIgnoreCase(selected)) {
            TextField tf = (TextField) tagInputArea.getChildren().get(0);
            String loc = tf.getText().trim();
            if (!loc.isEmpty()) {
                boolean alreadyExists = tags.stream()
                    .anyMatch(tag -> tag.getTagType().equalsIgnoreCase("location"));
                if (alreadyExists) {
                    showCustomWarning("Location Tag Error", "Only one location tag is allowed for a photo.");
                    tf.clear();
                    return;
                } else {
                    tags.add(new model.Tag("location", loc));
                }
                tf.clear();
            }
        } else if ("people".equals(selected)) {
            // For 'people', the add button in the HBox already adds names.
        } else if ("other".equals(selected)) {
            HBox hbox = (HBox) tagInputArea.getChildren().get(0);
            TextField typeField = (TextField) hbox.getChildren().get(1);
            TextField valueField = (TextField) hbox.getChildren().get(3);
            ComboBox<String> allowCombo = (ComboBox<String>) hbox.getChildren().get(5);
            String tagType = typeField.getText().trim();
            String tagValue = valueField.getText().trim();
            String allowMulti = allowCombo.getSelectionModel().getSelectedItem();
            
            if (!tagType.isEmpty() && !tagValue.isEmpty()) {
<<<<<<< HEAD
                boolean isMultiAllowed = allowMulti.equalsIgnoreCase("Yes");
                if (!isMultiAllowed) {
                    boolean alreadyExists = tags.stream()
                        .anyMatch(tag -> tag.getTagType().equalsIgnoreCase(tagType));
                    if (alreadyExists) {
                        showCustomWarning("Tag Multiplicity Error", "Only one '" + tagType + "' tag is allowed for a photo.");
                        return; 
                    }
                }
                tagTypeManager.addTagType(tagType, isMultiAllowed);
=======
            	boolean isMultiAllowed = allowMulti.equalsIgnoreCase("Yes");
            	boolean alreadyExists = tags.stream()
                         .anyMatch(tag -> tag.getTagType().equalsIgnoreCase(tagType));
            	if (alreadyExists && !isMultiAllowed) {
                    	showCustomWarning("Tag Multiplicity Error", "Only one '" + tagType + "' tag is allowed for a photo.");
                        return; 
                    }
            	
            	 Boolean currentMultiAllowed = tagTypeManager.getAllowedTagTypes().get(tagType.toLowerCase());
                 if (currentMultiAllowed != null && !currentMultiAllowed && isMultiAllowed) {
                     showCustomWarning("Tag Multiplicity Error",
                             "The tag type '" + tagType + "' is already set as a single-value tag. " +
                             "Cannot add additional values even if 'Allow Multiple' is selected.");
                     return;
                 }
            	tagTypeManager.addTagType(tagType, isMultiAllowed);
>>>>>>> 04f801fa1394f36d540e72f6323e36aa5807f99e
                ArrayList<String> presetTypes = new ArrayList<>(tagTypeManager.getAllowedTagTypes().keySet());
                presetTypes.add("other");
                tagTypeCombo.setItems(FXCollections.observableArrayList(presetTypes));
                tags.add(new model.Tag(tagType, tagValue));
                typeField.clear();
                valueField.clear();
            }
        } else {
            TextField tf = (TextField) tagInputArea.getChildren().get(0);
            String val = tf.getText().trim();
            if (!val.isEmpty()) {
                if (!tagTypeManager.isMultipleAllowed(selected)) {
                    boolean alreadyExists = tags.stream()
                        .anyMatch(tag -> tag.getTagType().equalsIgnoreCase(selected));
                    if (alreadyExists) {
                        showCustomWarning("Tag Multiplicity Error", "Only one '" + selected + "' tag is allowed for a photo.");
                        tf.clear();
                        return;
                    }
                }
                tags.add(new model.Tag(selected, val));
                tf.clear();
            }
        }
    }
    
    /**
     * Handles removing a tag by reading the tag value from the appropriate input field.
     * It now checks if the first node in tagInputArea is a TextField or an HBox containing one.
     */
    @FXML
    private void handleRemoveTag() {
        String selected = tagTypeCombo.getSelectionModel().getSelectedItem();
        TextField tf = null;
        
        // Obtain the first node in the tag input area.
        Node node = tagInputArea.getChildren().get(0);
        if (node instanceof TextField) {
            tf = (TextField) node;
        } else if (node instanceof HBox) {
            // Iterate through children of the HBox to find a TextField.
            for (Node child : ((HBox) node).getChildren()) {
                if (child instanceof TextField) {
                    tf = (TextField) child;
                    break;
<<<<<<< HEAD
=======
                    }
                }
                
             if (tagToRemove != null) {

                 tags.remove(tagToRemove);
                 showCustomWarning("Tag Removed", "Removed tag: " + selected + ":" + valueToRemove);
                 tf.clear();
             } else {
                 showCustomWarning("Tag Not Found", 
                     "No tag of type '" + selected 
                      + "' with value '" + valueToRemove + "' was found.");
>>>>>>> 04f801fa1394f36d540e72f6323e36aa5807f99e
                }
            }
        }
        
        if (tf == null) {
            System.err.println("No valid text field found for removing a tag.");
            return;
        }
        
        String valueToRemove = tf.getText().trim();
        if (!valueToRemove.isEmpty()) {
            model.Tag tagToRemove = null;
            for (model.Tag t : tags) {
                if (t.getTagType().equalsIgnoreCase(selected)
                        && t.getTagValue().equalsIgnoreCase(valueToRemove)) {
                    tagToRemove = t;
                    break;
                }
            }
            
            if (tagToRemove != null) {
                tags.remove(tagToRemove);
                System.out.println("Removed tag: " + selected + ":" + valueToRemove);
                tf.clear();
            } else {
                showCustomWarning("Tag Not Found",
                        "No tag of type '" + selected + "' with value '" + valueToRemove + "' was found.");
            }
        }
    }
    
    /**
     * Finalizes the editing by updating the photo's caption and tags, then closes the dialog.
     */
    @FXML
    private void handleDone() {
        photo.setCaption(captionField.getText().trim());
        photo.setTags(tags);
        Stage stage = (Stage) captionField.getScene().getWindow();
        stage.close();
    }
    
    private void showCustomWarning(String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/customWarning.fxml"));
            Parent root = loader.load();
            
            CustomWarningController controller = loader.getController();
            controller.setWarning(title, message);
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
