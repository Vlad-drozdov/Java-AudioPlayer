package org.example.audioplayer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;

public class MainController implements IController {
    private double xOffset = 0;
    private double yOffset = 0;

    private Model model;

    @FXML private HBox titleBar;
    @FXML private Button minimizeBtn;
    @FXML private Button closeBtn;
    @FXML private ImageView appIcon;
    @FXML private ImageView musicPhoto;
    @FXML private ImageView playIcon;
//    @FXML private Slider slider;
    @FXML private Label timeNow;
    @FXML private Label timeSize;
    @FXML private Label songTitle;


    @FXML private void initialize() {
        model = Init.init();
        model.setIController(this);
        dataHandler(model.dataLoader(null));
        appIcon.setImage(new Image(getClass().getResourceAsStream("/org/example/audioplayer/icon.png")));
//        if (model instanceof PlayerService playerService) {
//            playerService.bindProgress(slider);
//        }
        titleBar.setOnMousePressed(this::handleMousePressed);
        titleBar.setOnMouseDragged(this::handleMouseDragged);


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

    private void dataHandler(Data data){
        songTitle.setText(data.getSongTitle());
        timeSize.setText(data.getSongLength());
        musicPhoto.setImage(data.getSongImage());
        playIcon.setImage(data.getPlayButtonImage());
    }


    @Override
    public void setTime(String time) {
        timeNow.setText(time);
    }
}

