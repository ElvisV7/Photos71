package view;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AlbumController {

	@FXML
	private void handleAlbumClick(MouseEvent event) throws IOException {
	    VBox clickedAlbum = null;
	    if (event.getSource() instanceof VBox) {
	        clickedAlbum = (VBox) event.getSource();
	    } else if (event.getTarget() instanceof Node) {
	        clickedAlbum = (VBox) ((Node) event.getTarget()).getParent();
	    }
	    
	    // Retrieve album name for logging or further use.
	    Label albumLabel = (Label) clickedAlbum.getChildren().get(1);
	    String albumName = albumLabel.getText();
	    System.out.println("Album clicked: " + albumName);
	    
	    // Load the photo view scene.
	    Parent photoViewRoot = FXMLLoader.load(getClass().getResource("/view/photoView.fxml"));
	    Scene photoScene = new Scene(photoViewRoot, 600, 400);
	    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    stage.setScene(photoScene);
	    stage.show();
	}

}
