package org.example.audioplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("AudioPlayer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 440, 600);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/example/audioplayer/AudioPlayer-style.css")).toExternalForm());

        Image icon = new Image(getClass().getResourceAsStream("/org/example/audioplayer/icon.png"));
        stage.getIcons().add(icon);

        stage.setTitle("AudioPlayer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}