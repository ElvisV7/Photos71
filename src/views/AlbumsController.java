package views;

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

public class AlbumsController {

    @FXML
    private void handleAlbumClick(MouseEvent event) throws IOException {
        // Try to get the VBox that was clicked.
        // If the event source isn't the VBox itself (due to bubbling), we attempt to retrieve its parent.
        VBox clickedAlbum = null;
        if (event.getSource() instanceof VBox) {
            clickedAlbum = (VBox) event.getSource();
        } else if (event.getTarget() instanceof Node) {
            clickedAlbum = (VBox) ((Node) event.getTarget()).getParent();
        }
        
        // Assuming that the VBox's second child is a Label holding the album name.
        Label albumLabel = (Label) clickedAlbum.getChildren().get(1);
        String albumName = albumLabel.getText();
        System.out.println("Album clicked: " + albumName);
        
        // Load the album view FXML. You may use the album name to customize the next scene.
        Parent albumRoot = FXMLLoader.load(getClass().getResource("/views/albumView.fxml"));
        Scene albumScene = new Scene(albumRoot);
        
        // Retrieve the current stage from the event source.
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(albumScene);
        stage.show();
    }
}
