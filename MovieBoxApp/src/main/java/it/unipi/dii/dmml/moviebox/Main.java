package it.unipi.dii.dmml.moviebox;

import it.unipi.dii.dmml.moviebox.persistence.Neo4jDriver;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            stage.setOnCloseRequest((WindowEvent we) -> {
                Neo4jDriver.getInstance().closeConnection();
                System.exit(0);
            });
            Parent root = FXMLLoader.load(getClass().getResource("/it/unipi/dii/dmml/moviebox/layout/login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("    MovieBox");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("img/MovieBoxLogo.png")));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
