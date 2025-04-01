package app;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Photos extends Application {
	public static view.Admin admin = view.Admin.getInstance(new ArrayList<view.User>()); // Global access to admin
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        primaryStage.setTitle("Photos");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/icon.png")));
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/view/application.css").toExternalForm());
        primaryStage.setResizable(false); // Disable window resizing
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
