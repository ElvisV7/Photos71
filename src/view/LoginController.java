package view;

import java.io.IOException;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private TextField username;

    private static ArrayList<String> users = new ArrayList<String>(Arrays.asList("admin"));
    
    @FXML
    void handleSubmitAction(ActionEvent event) throws IOException{
    	String user = username.getText();
    	if(users.contains(user)) {
    		System.out.println(user + " logged in!");
    		Parent newRoot = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
	        Scene newScene = new Scene(newRoot, 600, 400);
	        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	        currentStage.setScene(newScene);
	        currentStage.setResizable(true);
	        currentStage.show();
    	}
    }
    
    @FXML
    private void handleAlbumClick(MouseEvent event) throws IOException {
        // Identify which album was clicked. For example, get album name from the Label:
        VBox clickedAlbum = (VBox) event.getSource();
        Label albumLabel = (Label) clickedAlbum.getChildren().get(1);  // assuming the Label is the second child
        String albumName = albumLabel.getText();
        System.out.println("Album clicked: " + albumName);

        // Load the album view (albumView.fxml)
        Parent albumView = FXMLLoader.load(getClass().getResource("/view/albumView.fxml"));
        Scene scene = new Scene(albumView);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
