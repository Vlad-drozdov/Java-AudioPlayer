package PlaylistItem;

import MainWindow.MainController;
import MainWindow.PlayerService;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistManager implements PlaylistItemModel {

    private static final HashMap<String, PlaylistController> playlists = new HashMap<>();
    private static VBox playlistsParent;
    private static PlaylistController currentPlaylist;
    private static boolean playlistIsLoad = false;
    private static int number = 1;

    public static void loadPlaylists(){
        List<String> IDs = PlaylistMemory.loadAllPlaylists();
        if (IDs!=null && !IDs.isEmpty()) {
            for (int i = 0; i < IDs.size(); i++) {
                String id = IDs.get(i);

                loadPlaylist(id);

                if (!playlists.isEmpty()) {
                    if (playlists.get(id).playlistNotEmpty() && !playlistIsLoad){
                        playlistIsLoad = true;
                        playlists.get(id).selectPlaylist();
                    }
                }
            }
            number++;
        }else{
            addPlaylist();
        }
    }


    public static void setParent(VBox playlists) {
        playlistsParent = playlists;
    }


    public static String getName(){
        return "Новий плейліст "+ ((playlists.isEmpty()) ? "" :playlists.size()) ;
    }

    public static void addPlaylist(){
        PlaylistController controller = loadVisual();
        Playlist playlist = new Playlist(getName(), controller, String.valueOf(number));
        number++;
        playlists.put(playlist.getId(),controller);
        controller.setPlaylist(playlist);
        if (playlists.size()==1){
            changePlaylist(playlist, controller);
        }
    }

    public static PlaylistController loadPlaylist(String id){
        PlaylistController controller = loadVisual();
        Playlist playlist = new Playlist(getName(),id, controller);
        playlist.loadMemory();
        number = Integer.parseInt(playlist.getNumber());
        playlists.put(playlist.getId(),controller);
        controller.setPlaylist(playlist);
        return controller;
    }

    public static void delPlaylist(String id, HBox view) {
        // Удалить из UI
        playlistsParent.getChildren().remove(view);

        // Получаем номер удаляемого плейлиста
        PlaylistController delController = playlists.get(id);
        int delNum = delController.getNumber();

        PlaylistMemory.del(id);

        // Если это был последний — просто уменьшаем счётчик
        if (delNum == number - 1) {
            number--;
            return;
        }

        // Сдвигаем все номера плейлистов с большим номером
        for (Map.Entry<String, PlaylistController> entry : playlists.entrySet()) {
            PlaylistController controller = entry.getValue();
            int currentNum = controller.getNumber();

            if (currentNum > delNum) {
                controller.setNumber(currentNum - 1);
//                System.out.println(controller.getPLaylist().getTitle() + " -> " + (currentNum - 1));
            }
        }

        number--;
        playlists.remove(id);
    }


    public static void changePlaylist(Playlist playlist, PlaylistController controller){
        changeSelectedPlaylist(controller);
        PlayerService.setPlaylist(playlist);
    }

    public static void changePlaylist(){
        if (playlists.isEmpty()) {
            addPlaylist();
        }
        PlaylistController controller = playlists.values().iterator().next();
        changeSelectedPlaylist(controller);
        controller.PlaylistPlay(null);
    }

    private static PlaylistController loadVisual(){
        PlaylistController controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/FXML/MainWindow/PlaylistItem.fxml"));
            HBox playlistItem = loader.load();
            playlistItem.getStylesheets().add(PlaylistManager.class.getResource("/FXML/MainWindow/AudioPlayer-style.css").toExternalForm());
            playlistsParent.getChildren().add(playlistItem);
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

    private static void changeSelectedPlaylist(PlaylistController controller){
        if (currentPlaylist!=controller){
            if (playlists.size()!=1){
                currentPlaylist.delSelected();
            }
            currentPlaylist = controller;
            currentPlaylist.addSelected();
        }
    }

    public static void updateVisualCurrentPlaylist(){
        currentPlaylist.updatePlaylistVisual();
    }
}
