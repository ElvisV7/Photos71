package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * 
 * 
 * @author Tyler Gehringer
 *
 */

public class CustomWarningController {

    @FXML
    private Label titleLabel;
    
    @FXML
    private Label messageLabel;
    
    public void setWarning(String title, String message) {
        titleLabel.setText(title);
        messageLabel.setText(message);
    }
    
    @FXML
    private void handleOK() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}
