package view;

import java.time.LocalDate;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the search dialog.
 * <p>
 * This dialog collects search criteria from the user. The criteria consist of a date range
 * (start and end dates) or a tag-based search (tag type and tag value). The criteria are
 * considered disjoint – the user should provide either a date range <em>or</em> tag criteria,
 * but not both. In this implementation, if both types of criteria are provided, the tag fields
 * are cleared (i.e. the date range takes precedence).
 * </p>
 * 
 * @author Elvis Vasquez & Tyler Gehringer
 */
public class SearchDialogController {

    @FXML 
    private DatePicker startDatePicker;
    @FXML 
    private DatePicker endDatePicker;
    @FXML 
    private TextField tagTypeField1;
    @FXML 
    private TextField tagValueField1;
    @FXML 
    private TextField tagTypeField2;
    @FXML 
    private TextField tagValueField2;
    @FXML 
    private TextField operatorField;
    
    private SearchCriteria criteria;
    
    /**
     * Represents the search criteria provided by the user.
     */
    public static class SearchCriteria {
        public LocalDate startDate;
        public LocalDate endDate;
        public String tagType1;
        public String tagValue1;
        public String tagType2;
        public String tagValue2 ;
        public String operator;
    }
    
    /**
     * Called when the user clicks the Search button.
     * <p>
     * This method retrieves the values from the date pickers and text fields. It then validates
     * the criteria so that only one type of search is allowed. In this implementation, if the user
     * provides both a date range and tag search criteria, the tag criteria are cleared (date range
     * takes precedence).
     * </p>
     *
     * @param event the ActionEvent triggered by clicking the Search button.
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        // Retrieve values from UI controls.
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        String tagType1 = tagTypeField1.getText().trim();
        String tagValue1 = tagValueField1.getText().trim();
        String tagType2 = tagTypeField2.getText().trim();   // these controls you add
        String tagValue2 = tagValueField2.getText().trim();
        String operator = operatorField.getText().trim(); 
        
        // Validation: Allow either date range OR tag search but not both.
        if (!tagType1.isEmpty() || !tagValue1.isEmpty()) {
            // Option 1: Clear tag fields so that only the date range remains.
            start = null;
            end = null;
            // Option 2: Alternatively, you could show an error alert and not close the dialog.
            // For example:
            // showError("Please enter either a date range or tag search criteria, not both.");
            // return;
        }
        
        // Create and set the criteria.
        criteria = new SearchCriteria();
        criteria.startDate = start;
        criteria.endDate = end;
        criteria.tagType1 = tagType1;
        criteria.tagValue1 = tagValue1;
        criteria.tagType2 = tagType2;
        criteria.tagValue2 = tagValue2;
        criteria.operator = operator;
        
        // Close the dialog.
        Stage stage = (Stage) startDatePicker.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Called when the user clicks the Cancel button.
     * <p>
     * Clears any search criteria and closes the dialog.
     * </p>
     *
     * @param event the ActionEvent triggered by clicking the Cancel button.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        criteria = null;
        Stage stage = (Stage) startDatePicker.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Returns the search criteria entered by the user.
     *
     * @return an Optional containing the SearchCriteria if provided, or Optional.empty() if canceled.
     */
    public Optional<SearchCriteria> getCriteria() {
        return Optional.ofNullable(criteria);
    }
}
