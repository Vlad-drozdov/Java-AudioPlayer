package Logic;

import MainWindow.PlayerService;
import PlaylistItem.Playlist;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import MainWindow.PlayerController;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class Player {

    private static MediaPlayer player;
    private static PlayerController iController;
    private static Playlist playlist;



    private static double volumeLevel = 1;
    private static boolean play = false;

    private static boolean timeLineEditing = false;
    private static Duration newTime;

    public Player(PlayerController iController, Playlist playList){
        Player.iController = iController;
        playlist = playList;
        if (!playList.isEmpty()){
            initPlayer(playList.getRandomSong(), false);
            iController.dataHandler(dataLoader(null));
            iController.loadImage();
        }
    }

    public static void setPlaylist(Playlist playList){
        playlist = playList;
        initPlayer(playList.getRandomSong(),play);
    }


    // Функции для работы библиотеки плеера
    public static void initPlayer(Song song, Boolean isPlay) {
        if (player != null) {
            player.dispose();
        }
         iController.loadImage();
        if (song == null){
            iController.dataHandler(new Data(null,null,null,null));
            iController.centerVisible(false);
            return;
        }
        else {
            iController.dataHandler(new Data(song.getTitle(),song.getCover(),song.getLength(),(play)?ImageLoader.getImgPause():ImageLoader.getImgPlay()));
            iController.centerVisible(true);
        }

        player = new MediaPlayer(song.getMedia());
        player.setVolume(volumeLevel);

        player.currentTimeProperty().addListener((obs, oldVal, newVal) -> {

            int totalSec = (int) newVal.toSeconds();
            String timeStr = String.format("%02d:%02d", totalSec / 60, totalSec % 60);
            double percent;

            percent = ((double) totalSec / (int) playlist.getCurrentSong().getTotalSec()) * 100;

            iController.updateTime(timeStr);
            if (!timeLineEditing){
                iController.updateSlider(percent);
            }

        });

        player.setOnReady(() -> {
//            System.out.println("Плеєр готовий");

            if (isPlay) {
                play = true;
                player.play();

            }
        });
        player.setOnPlaying(() -> {
            play = true;

        });
        player.setOnPaused(() -> {
            play = false;

        });
        player.setOnEndOfMedia(() -> {
            javafx.application.Platform.runLater(() -> {
                iController.dataHandler(songEnd());

            });
            play = false;

        });
        player.setOnError(() -> System.out.println("Помилка у плеєрі: " + player.getError()));

        play = false;
    }
    public static Data songEnd(){
        if (!playlist.isEmpty()) {
            initPlayer(playlist.nextSongIfItEnd(), true);
            return dataLoader(ImageLoader.getImgPause());
        }
        return dataLoader(ImageLoader.getImgPlay());
    }

    public Data clickPlay() {
        if (playlist.isEmpty()) return dataLoader(ImageLoader.getImgPlay());


        Image img;
        if (player == null) {
            System.out.println("MediaPlayer ще не існує");
            return dataLoader(ImageLoader.getImgPlay());
        }

        if (play) {

            player.pause();

            img = ImageLoader.getImgPlay();
            iController.stopRotation();
        } else {

            player.play();

            img = ImageLoader.getImgPause();
            iController.playRotation();
        }
        PlayerService.ClickPlayToPlaylist();
        return dataLoader(img);
    }

    public Data clickLast() {
        iController.resetRotation();
        if (!playlist.isEmpty()){
            initPlayer(playlist.previousSong(), play);
            return dataLoader(ImageLoader.getImgPause());
        }
        return dataLoader(ImageLoader.getImgPlay());
    }

    public Data clickNext() {
        iController.resetRotation();
        if (!playlist.isEmpty()) {
            initPlayer(playlist.nextSong(), play);
            return dataLoader(ImageLoader.getImgPause());
        }
        return dataLoader(ImageLoader.getImgPlay());
    }

    public Image clickRepeat() {
        playlist.changeRepeatSong();
        if (playlist.isRepeatSong()){
            return ImageLoader.getImgRepeatSong();
        }
        return ImageLoader.getImgRepeat();
    }

    public Image clickRandom() {
        playlist.changeRandomSong();
        if (playlist.isRandomSong()){
            return ImageLoader.getImgRandom();
        }
        return ImageLoader.getImgNotRandom();
    }

    public Data clickRandomBox() {
        iController.resetRotation();
        if (!playlist.isEmpty()) {
            initPlayer(playlist.getRandomSong(), true);
            return dataLoader(ImageLoader.getImgPause());
        }
        return dataLoader(ImageLoader.getImgPlay());
    }

    public void sliderEdit(double value) { timeLineEditing = true; }

    public void sliderEdited(double percent) {
        if (!playlist.isEmpty()) {
            Duration total = player.getTotalDuration();
            newTime = total.multiply(percent / 100);

            player.seek(newTime);

            if (!play){
                String timeStr = String.format("%02d:%02d", (int) newTime.toSeconds() / 60, (int) newTime.toSeconds() % 60);
                iController.updateTime(timeStr);
            }

            timeLineEditing = false;
        } else {
            iController.updateSlider(0);
        }
    }

    public void volumeEdit(double volume) {
        if (!playlist.isEmpty()) {
            volumeLevel = volume/100;
            player.setVolume(volumeLevel);
        }
    }

    public static Data chooseFiles(Window window) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Виберіть аудіофайли");

        chooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.flac"),
                new FileChooser.ExtensionFilter(".mp3", "*.mp3"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        List<File> files = chooser.showOpenMultipleDialog(window);

        if (!playlist.isEmpty()) iController.loadImage();
        return playlist.chooseFiles(files);
    }

    //Функции загрузки данных
    public static Data dataLoader(Image playButtonImage) {
        if (!playlist.isEmpty()) {
            Song song = playlist.getCurrentSong();
            return new Data(song.getTitle(), song.getCover(), song.getLength(), playButtonImage);
        }
        return null;
    }

    public static boolean isPlay(){ return play;}
    public static void setPlay(boolean play){ Player.play = play; }



    public static class ImageLoader {
        private static final Image imgPlay = load("/icons/play.png");
        private static final Image imgPause = load("/icons/pause.png");
        private static final Image imgPlayHover = load("/icons/play_hover.png");
        private static final Image imgPauseHover = load("/icons/pause_hover.png");

        private static final Image imgRepeat = load("/icons/repeat.png");
        private static final Image imgRepeatSong = load("/icons/repeat_song.png");

        private static final Image imgNotRandom = load("/icons/notRandom.png");
        private static final Image imgRandom = load("/icons/random.png");

        private static Image load(String path) {
            return new Image(Objects.requireNonNull(Player.class.getResource(path), "Resource not found: " + path).toExternalForm());
        }

        public static Image getImgPlay(){ return imgPlay; }
        public static Image getImgPlayHover(){ return imgPlayHover; }

        public static Image getImgPause() { return imgPause; }
        public static Image getImgPauseHover(){ return imgPauseHover; }

        public static Image getImgRepeat() { return imgRepeat; }

        public static Image getImgRepeatSong() { return imgRepeatSong; }

        public static Image getImgNotRandom() { return imgNotRandom; }

        public static Image getImgRandom() { return imgRandom; }
    }
}
