package MainWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/FXML/MainWindow/AudioPlayer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 600);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/FXML/MainWindow/AudioPlayer-style.css")).toExternalForm());

        Image icon = new Image(getClass().getResourceAsStream("/icons/icon.png"));
        stage.getIcons().add(icon);

        stage.setTitle("AudioPlayer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger("org.jaudiotagger");
        logger.setLevel(Level.SEVERE);

        // опционально убрать обработчики, чтобы точно не было вывода
        Logger rootLogger = Logger.getLogger("");
        Arrays.stream(rootLogger.getHandlers()).forEach(handler -> handler.setLevel(Level.SEVERE));

        launch();
    }
}