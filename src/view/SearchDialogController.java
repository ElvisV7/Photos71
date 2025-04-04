package view;

import java.time.LocalDate;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SearchDialogController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField tagTypeField;
    @FXML private TextField tagValueField;
    
    private SearchCriteria criteria;
    
    public static class SearchCriteria {
        public LocalDate startDate;
        public LocalDate endDate;
        public String tagType;
        public String tagValue;
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        criteria = new SearchCriteria();
        criteria.startDate = startDatePicker.getValue();
        criteria.endDate = endDatePicker.getValue();
        criteria.tagType = tagTypeField.getText().trim();
        criteria.tagValue = tagValueField.getText().trim();
        
        // Close the dialog.
        Stage stage = (Stage) startDatePicker.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        criteria = null;
        Stage stage = (Stage) startDatePicker.getScene().getWindow();
        stage.close();
    }
    
    public Optional<SearchCriteria> getCriteria() {
        return Optional.ofNullable(criteria);
    }
}
