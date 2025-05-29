package org.example.audioplayer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainController implements IController {
    private double xOffset = 0;
    private double yOffset = 0;

    private Model model;

    @FXML private VBox main_window;
    @FXML private StackPane dndOverlay;
    @FXML private ImageView dndIcon;
    @FXML private Label dropZone;
    @FXML private Region dimRect;
    @FXML private HBox titleBar;
    @FXML private Button minimizeBtn;
    @FXML private Button closeBtn;
    @FXML private  Button chooseFiles;
    @FXML private ImageView appIcon;
    @FXML private ImageView musicPhoto;
    @FXML private ImageView playIcon;
    @FXML private ImageView repeatIcon;
    @FXML private Slider slider;
    @FXML private Label timeNow;
    @FXML private Label timeSize;
    @FXML private Label songTitle;


    @FXML private void initialize() {
        model = Init.init(this);
        appIcon.setImage(new Image(getClass().getResourceAsStream("/org/example/audioplayer/icons/icon.png")));
        titleBar.setOnMousePressed(this::handleMousePressed);
        titleBar.setOnMouseDragged(this::handleMouseDragged);
        main_window.setOnDragOver(event -> {
            if (event.getGestureSource() != main_window && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
                dndOverlay.setVisible(true);
            }
            event.consume();
        });

        main_window.setOnDragExited(event -> {
            dndOverlay.setVisible(false);
            event.consume();
        });

        main_window.setOnDragDropped(event -> {
            var db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                List<File> files = new ArrayList<>();
                for (File file : db.getFiles()) {
                    if (model.isAudioFile(file)) {
                        files.add(file);
                    }
                }
                dataHandler(model.addFiles(files));
            }
            dndOverlay.setVisible(false);
            event.setDropCompleted(success);
            event.consume();
        });


        closeBtn.setOnAction(e -> Platform.exit());
        minimizeBtn.setOnAction(e -> {
            Stage stage = (Stage) minimizeBtn.getScene().getWindow();
            stage.setIconified(true);
        });
    }
    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }
    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    public void ClickLast(ActionEvent actionEvent) {
        dataHandler(model.clickLast());

    }
    public void ClickPlay(ActionEvent actionEvent) {
        dataHandler(model.clickPlay());

    }
    public void ClickNext(ActionEvent actionEvent) {dataHandler(model.clickNext());}

    public void ClickRandom(ActionEvent actionEvent){dataHandler(model.clickRandom());}

    public void ClickRepeat(ActionEvent actionEvent){repeatIcon.setImage(model.clickRepeat());}

    public void sliderEdit(MouseEvent mouseEvent) {
        model.sliderEdit(slider.getValue());
    }


    public void dataHandler(Data data){
        if (data != null){
            songTitle.setText(data.getSongTitle());
            timeSize.setText(data.getSongLength());
            musicPhoto.setImage(data.getSongImage());
            playIcon.setImage(data.getPlayButtonImage());
            timeNow.setText(data.getTime());
        }
    }


    @Override
    public void setTime(String time) {
        timeNow.setText(time);
    }

    @Override
    public void updateSlider(double value) {
        slider.setValue(value);
    }

    public void chooseFiles(ActionEvent actionEvent) {
        dataHandler(model.chooseFiles(chooseFiles.getScene().getWindow()));
    }
}

