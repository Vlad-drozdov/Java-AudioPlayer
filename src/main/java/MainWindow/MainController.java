package MainWindow;

import PlaylistItem.PlaylistManager;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import CustomComponents.VolumeSlider;
import Logic.DiscordRpcManager;
import Listeners.KeyboardListener;
import Listeners.MediaKeyListener;
import Logic.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainController implements PlayerController {

    private double xOffset = 0;
    private double yOffset = 0;

    private PlayerModel model;
    private final static DiscordRpcManager discordRpcManager = new DiscordRpcManager();
    private final MediaKeyListener mediaKeyListener = new MediaKeyListener(this);
    private final KeyboardListener keyboardListener = new KeyboardListener(this);

    @FXML private AnchorPane main_window;

    @FXML private StackPane dndOverlay;
    @FXML private ImageView dndIcon;
    @FXML private Label dropZone;
    @FXML private Region dimRect;

    @FXML private HBox titleBar;
    @FXML private Button minimizeBtn;
    @FXML private Button closeBtn;
    @FXML private  Button chooseFiles;
    @FXML private ImageView appIcon;

    @FXML private ImageView rotatingCover;
    @FXML private ImageView centerImage;
    @FXML private StackPane diskPane;
    private RotateTransition rotate;

    @FXML private ImageView playIcon;
    @FXML private ImageView repeatIcon;
    @FXML public ImageView randomIcon;
    @FXML private Slider slider;
    @FXML public VolumeSlider volumeBar;
    @FXML private Label timeNow;
    @FXML private Label timeSize;
    @FXML private Label songTitle;

    @FXML private StackPane plsOverlay;
    @FXML public VBox playlists;
    @FXML public Label playlistTitle;
    @FXML public TextField playlistEditTitle;
    @FXML public Label playlistCount;

    @FXML private void initialize() {

        model = InitMainWindow.init(this); // Подгрузка модели

        DiscordRpcManager.start(); // Создания профиля для статуса в дискорде

        buildTitleBar(); // Кастомная шапка окна

        plateRotate(); // Вращение пластинки

        // Отвечает за работу Тайм-Лайна
        sliderOnMousePressed();
        sliderOnMouseReleased();


        dropOnDrag(); // Отвечает за перетаскивание файлов в приложение
        Platform.runLater(() -> {
            if (main_window.getScene() != null) {
                main_window.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyboardListener);
            }
        }); // привязка keyboardListener
        PlaylistManager.setParent(playlists);
        PlaylistManager.loadPlaylists();
    }

    // Инициализация окна
    private void buildTitleBar() {
        appIcon.setImage(new Image(getClass().getResourceAsStream("/icons/icon.png")));
        titleBar.setOnMousePressed(this::handleMousePressed);
        titleBar.setOnMouseDragged(this::handleMouseDragged);
        main_window.setOnDragOver(event -> {
            if (event.getGestureSource() != main_window && event.getDragboard().hasFiles()) {
                List<File> files = event.getDragboard().getFiles();
                boolean hasMp3 = files.stream().anyMatch(file -> file.getName().toLowerCase().endsWith(".mp3"));
                if (hasMp3){
                    event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
                    dndOverlay.setVisible(true);
                }
            }
            event.consume();
        });

        closeBtn.setOnAction(e -> {
            DiscordRpcManager.stop();
            Platform.exit();
            System.exit(0);
        });
        minimizeBtn.setOnAction(e -> {
            Stage stage = (Stage) minimizeBtn.getScene().getWindow();
            stage.setIconified(true);
        });
    }
    private void dropOnDrag() {
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
                    if (isAudioFile(file)) {
                        files.add(file);
                    }
                }
                dataHandler(model.addFiles(files));
            }
            dndOverlay.setVisible(false);
            event.setDropCompleted(success);
            event.consume();
        });
    }



    // Обработка нажатий миши
    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }
    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }


    // Обработка нажатий кнопок на основном окне
    public void ClickLast(ActionEvent actionEvent) { dataHandler(model.clickLast()); }

    public void ClickPlay(ActionEvent actionEvent) { dataHandler(model.clickPlay()); }

    public void ClickNext(ActionEvent actionEvent) {dataHandler(model.clickNext());}

    public void ClickRandom(ActionEvent actionEvent) { randomIcon.setImage(model.clickRandom()); }

    public void ClickRandomBox(ActionEvent actionEvent){dataHandler(model.clickRandomBox());}

    public void ClickRepeat(ActionEvent actionEvent){repeatIcon.setImage(model.clickRepeat());}

    public void ClickPlaylists(ActionEvent actionEvent) {
        plsOverlay.setVisible(true);
        plsOverlay.setMouseTransparent(false);

        TranslateTransition show = new TranslateTransition(Duration.millis(200), plsOverlay);
        show.setToX(0);
        show.play();
    }

    // Добавление файлов через кнопку
    public void chooseFiles(ActionEvent actionEvent) { dataHandler(model.chooseFiles(chooseFiles.getScene().getWindow())); }


    // Обработка кнопок в оверлее плейлистов
    public void plsExitClick(ActionEvent actionEvent) {
        TranslateTransition hide = new TranslateTransition(Duration.millis(200), plsOverlay);
        hide.setToX(480);
        hide.setOnFinished(e -> plsOverlay.setVisible(false));
        hide.play();

        plsOverlay.setMouseTransparent(true);
    }

    public void addPlaylist(ActionEvent actionEvent) {
        PlaylistManager.addPlaylist();
    }

    // Обработка слайдеров
    private void sliderOnMousePressed(){
        slider.setOnMousePressed(event -> {
            double mouseX = event.getX();
            double sliderWidth = slider.getWidth();

            // Ограничиваем значение между 0 и sliderWidth
            mouseX = Math.max(0, Math.min(mouseX, sliderWidth));

            double percent = mouseX / sliderWidth * 100;
            slider.setValue(percent);

            model.sliderEdit(percent);
        });

    }

    private void sliderOnMouseReleased(){
        slider.setOnMouseReleased(event -> {
            double mouseX = event.getX();
            double sliderWidth = slider.getWidth();
            mouseX = Math.max(0, Math.min(mouseX, sliderWidth));

            double percent = mouseX / sliderWidth * 100;
            slider.setValue(percent);

            model.sliderEdited(percent);
        });
    }

    @Override public void updateTime(String time) { timeNow.setText(time); }

    public void volumeEdit(MouseEvent mouseEvent) { model.volumeEdit(volumeBar.getVolume()); }

    @Override public void updateSlider(double value) { slider.setValue(value); }

    public void setVolume(int volume){ volumeBar.setVolume(volume); }

    public int getVolume(){ return volumeBar.getVolume(); }



    // Отвечают за обработку и поворот пластинки
    private void plateRotate() {
        rotate = new RotateTransition(Duration.seconds(15), rotatingCover);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
    }

    @Override public void playRotation() { rotate.play(); }

    @Override public void stopRotation() { rotate.stop(); }

    @Override public void resetRotation() {
        stopRotation();
        rotatingCover.setRotate(0);
        playRotation();
    }

    @Override
    public void loadImage() {
        if (centerImage.getImage()==null){
            centerImage.setVisible(false);
            centerImage.setImage(new Image(getClass().getResourceAsStream("/icons/plateCenter.png")));
            centerImage.toFront();
        }
    }

    @Override
    public void centerVisible(boolean b) {
        centerImage.setVisible(b);
    }


    // Обработка полученных команд от модели
    public void dataHandler(Data data){
        if (data != null){
            songTitle.setText(data.getSongTitle());
            DiscordRpcManager.update(data.getSongTitle());
            timeSize.setText(data.getSongLength());
            rotatingCover.setImage(data.getSongImage());
            playIcon.setImage(data.getPlayButtonImage());
        }
    }

    public boolean isAudioFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".mp3"); // || name.endsWith(".wav") || name.endsWith(".aac") || name.endsWith(".m4a")
    }

}

