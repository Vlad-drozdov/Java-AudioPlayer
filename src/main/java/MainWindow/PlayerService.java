package MainWindow;

import Logic.Data;
import Logic.Player;
import PlaylistItem.Playlist;
import PlaylistItem.PlaylistManager;
import javafx.scene.image.Image;
import javafx.stage.Window;


import java.io.File;
import java.util.List;



public class PlayerService implements PlayerModel {


    private static Player player;
    private static PlayerController iController;
    private static Playlist currentPlaylist;

    public PlayerService(PlayerController iController) {
        PlayerService.iController = iController;
    }

    public static void setPlaylist(Playlist playlist){
        if (playlist.equals(currentPlaylist)) return;
        currentPlaylist = playlist;
        if (player==null){
            player = new Player(iController, currentPlaylist);
        }
        else{
            Player.setPlaylist(currentPlaylist);
        }
    }

    public static void ClickPlayToPlayer(){
        Data data = null;
        if (!currentPlaylist.isEmpty() && player != null) {
            data = player.clickPlay();
            PlaylistManager.updateVisualCurrentPlaylist();
        }
        else {
            iController.updateSlider(0);
            iController.updateTime("00:00");
        }
        iController.dataHandler(data);
    }

    public static void ClickPlayToPlaylist(){
        PlaylistManager.updateVisualCurrentPlaylist();
    }


    // Функции плеера
    @Override
    public Data clickPlay() {
        return (!currentPlaylist.isEmpty() && player != null) ? player.clickPlay() : null;
    }

    @Override
    public Data clickLast() {
        return (!currentPlaylist.isEmpty() && player != null) ? player.clickLast() : null;
    }

    @Override
    public Data clickNext() {
        return (!currentPlaylist.isEmpty() && player != null) ? player.clickNext() : null;
    }

    @Override
    public Image clickRepeat() {
        return (!currentPlaylist.isEmpty() && player != null)
                ? player.clickRepeat()
                : Player.ImageLoader.getImgRepeat();
    }

    @Override
    public Image clickRandom() {
        return (!currentPlaylist.isEmpty() && player != null)
                ? player.clickRandom()
                : Player.ImageLoader.getImgNotRandom();
    }

    @Override
    public Data clickRandomBox() {
        return (!currentPlaylist.isEmpty() && player != null) ? player.clickRandomBox() : null;
    }

    @Override
    public void sliderEdit(double value) {
        if (!currentPlaylist.isEmpty() && player != null) {
            player.sliderEdit(value);
        }
    }

    @Override
    public void sliderEdited(double percent) {
        if (!currentPlaylist.isEmpty() && player != null) {
            player.sliderEdited(percent);
        }
    }

    @Override
    public void volumeEdit(double volume) {
        if (!currentPlaylist.isEmpty() && player != null) {
            player.volumeEdit(volume);
        }
    }





    public static boolean currentPlaylistIsIt(Playlist playlist){
        return currentPlaylist.equals(playlist);
    }


    // Функции загрузки файлов песен
    @Override public Data chooseFiles(Window window) {
        if (currentPlaylist != null){
            setPlaylist(currentPlaylist);
            return  Player.chooseFiles(window);
        }
        return new Data(null,null,null,null);
    }

    @Override public Data addFiles(List<File> files) {
        if (files == null || files.isEmpty()) return null;
        iController.loadImage();
        return currentPlaylist.chooseFiles(files);
    }

}