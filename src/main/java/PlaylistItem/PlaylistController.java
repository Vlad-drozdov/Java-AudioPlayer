package PlaylistItem;

import Logic.Player;
import MainWindow.PlayerService;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;


public class PlaylistController implements PlaylistItemController {



    private PlaylistItemModel model;
    private Playlist playlist;
    private boolean play = false;
    private int number;


    @FXML private HBox playlistObject;
    @FXML public ImageView playlistImg;
    @FXML private Label playlistTitle;
    @FXML private TextField playlistEditTitle;
    @FXML private Label playlistCount;
    @FXML private Button playlistPlay;
    @FXML public ImageView btnPlayImg;
    @FXML private Button playlistDel;
    private RotateTransition rotate;

    @FXML private void initialize() {
        model = InitPlaylistItem.init();
        rotate = new RotateTransition(Duration.seconds(15), playlistImg);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
    }

    public void EditPlaylistTitle(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            playlistEditTitle.setText(playlistTitle.getText());
            playlistTitle.setVisible(false);
            playlistEditTitle.setVisible(true);
            playlistEditTitle.setManaged(true);
            playlistEditTitle.requestFocus();
            playlistEditTitle.selectAll();

            playlistEditTitle.setOnAction(event -> finishEditing());


            playlistEditTitle.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) finishEditing();
            });
        }
    }

    private void finishEditing() {
        String newTitle = playlistEditTitle.getText().trim();
        if (!newTitle.isEmpty()) {
            playlist.setTitle(newTitle);
            playlistTitle.setText(newTitle);
            playlist.updateMemory();
        }

        playlistEditTitle.setVisible(false);
        playlistEditTitle.setManaged(false);
        playlistTitle.setVisible(true);
    }

    public void PlaylistPlay(ActionEvent actionEvent) {
        PlaylistManager.changePlaylist(playlist, this);
        PlayerService.ClickPlayToPlayer();
        updatePlaylistVisual();
    }

    public void updatePlaylistVisual() {
        if (!playlist.isEmpty()){
            play = !play;
            Player.setPlay(play);
            if (Player.isPlay()&&play){
                playRotation();
                btnPlayImg.setImage(Player.ImageLoader.getImgPauseHover());
            }else {
                stopRotation();
                btnPlayImg.setImage(Player.ImageLoader.getImgPlayHover());
            }
        }
    }

    public void CursorEnteredOnPlay(MouseEvent mouseEvent) {
        if (Player.isPlay()&&play){
            btnPlayImg.setImage(Player.ImageLoader.getImgPauseHover());
        }else {
            btnPlayImg.setImage(Player.ImageLoader.getImgPlayHover());
        }


    }

    public void CursorExitedFromPlay(MouseEvent mouseEvent) {
        if (Player.isPlay()&&play){
            btnPlayImg.setImage(Player.ImageLoader.getImgPause());
        }else {
            btnPlayImg.setImage(Player.ImageLoader.getImgPlay());
        }
    }

    public void PlaylistDel(ActionEvent actionEvent) {
        PlaylistManager.delPlaylist(playlist.getId(),playlistObject);
        if (PlayerService.currentPlaylistIsIt(playlist)){
            PlaylistManager.changePlaylist();
        }
    }



    public void ClickOnBlock(MouseEvent mouseEvent) {
        selectPlaylist();
    }

    public void CursorEnteredOnBlock(MouseEvent mouseEvent) { playlistPlay.setVisible(true); }

    public void CursorExitedFromBlock(MouseEvent mouseEvent) { playlistPlay.setVisible(false); }


    public void playRotation() { rotate.play(); }

    public void stopRotation() { rotate.stop(); }

    public void resetRotation() {
        stopRotation();
        playlistImg.setRotate(0);
    }

    public void selectPlaylist(){
        PlaylistManager.changePlaylist(playlist, this);
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        playlistTitle.setText(playlist.getTitle());
        playlistCount.setText(String.valueOf(playlist.getCount()));
        number = Integer.parseInt(playlist.getNumber());
        playlist.updateMemory();
    }

    public Playlist getPLaylist(){
        if (playlist==null) return null;
        return playlist;
    }

    public int getNumber() { return number; }
    public void setNumber(int number) {
        this.number = number;
        playlist.setNumber(String.valueOf(number));
        playlist.updateMemory();
    }

    public void updateInfo() {
        if (playlist!=null) playlistCount.setText(String.valueOf(playlist.getCount()));
    }

    public boolean playlistNotEmpty(){
        return !playlist.isEmpty();
    }

    public void delSelected(){
        playlistObject.getStyleClass().remove("selectedPlaylist");
        play = false;
        resetRotation();
    }

    public void addSelected(){ playlistObject.getStyleClass().add("selectedPlaylist"); }
}
