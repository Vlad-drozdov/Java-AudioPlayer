package org.example.audioplayer;

import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class PlayerService implements Model {

    private MediaPlayer player;
    private IController iController;

    private final Image imgPlay = load("/org/example/audioplayer/icons/play.png");
    private final Image imgPause = load("/org/example/audioplayer/icons/pause.png");

    private final Image imgRepeat = load("/org/example/audioplayer/icons/repeat.png");
    private final Image imgRepeatSong = load("/org/example/audioplayer/icons/repeat_song.png");

    private boolean play = false;
    private Timer timer = null;
    private boolean timerIsRun = false;

    private PlayList playList = new PlayList();
//    String folderPath = "src/main/resources/org/example/audioplayer/songs2";
//    File folder = new File(folderPath);

    public PlayerService(IController iController) {
        this.iController = iController;

//        try {
//            if (folder.exists() && folder.isDirectory()) {
//                File[] files = folder.listFiles();
//                if (files != null) {
//                    for (File file : files) {
//                        if (file.isFile()) {
//                            playList.addSong(new Song(file));
//                        }
//                    }
//                }
//            } else {
//                System.out.println("Папка не знайдена або це не директорія");
//            }
//            if (!playList.isEmpty()){
//                initPlayer(playList.getRandomSong(), false);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        if (playList.loadMemory()){
            initPlayer(playList.getRandomSong(), false);
            iController.dataHandler(dataLoader(null,null));
        }
    }

    protected void initPlayer(Song song, Boolean isPlay) {
        if (player != null) {
            player.dispose();
        }
        player = new MediaPlayer(song.getMedia());
        if (timer != null){
            timer.stopTimer();
            timerIsRun = false;
        }
        timer = new Timer(player, iController,playList.getCurrentSong());
        timer.start();
        timer.pauseCounting();
        timerIsRun = true;

        player.setOnReady(() -> {
//            System.out.println("Плеєр готовий");

            if (isPlay) {
                play = true;
                player.play();
                timer.startCounting();
            }
        });
        player.setOnPlaying(() -> {
            play = true;
            timer.startCounting();
        });
        player.setOnPaused(() -> {
            play = false;
            timer.pauseCounting();
        });
        player.setOnEndOfMedia(() -> {
            javafx.application.Platform.runLater(() -> {
                iController.dataHandler(songEnd());
                timer.startCounting();
            });
            play = false;
            timer.stopTimer();
        });
        player.setOnError(() -> System.out.println("Помилка у плеєрі: " + player.getError()));

        play = false;
    }

    private Image load(String path) {
        return new Image(Objects.requireNonNull(getClass().getResource(path), "Resource not found: " + path).toExternalForm());
    }

    @Override
    public Data clickPlay() {
        Image img;
        if (player == null) {
            System.out.println("MediaPlayer ще не існує");
            return dataLoader(imgPlay,null);
        }

        if (play) {

            player.pause();
            timer.pauseCounting();
            img = imgPlay;
        } else {

            player.play();
            if (!timerIsRun){
                timerIsRun = true;
                timer.start();
            }
            timer.startCounting();
            img = imgPause;
        }
        Song song = playList.getCurrentSong();
        return dataLoader(img, timer.getTime());
    }

    @Override
    public Data clickLast() {
        if (!playList.isEmpty()){
            initPlayer(playList.previousSong(), true);
            return dataLoader(imgPause, timer.getTime());
        }
        return dataLoader(imgPlay,null);
    }

    @Override
    public Data clickNext() {
        if (!playList.isEmpty()) {
            initPlayer(playList.nextSong(), true);
            return dataLoader(imgPause, timer.getTime());
        }
        return dataLoader(imgPlay,null);
    }

    public Data songEnd(){
        if (!playList.isEmpty()) {
            initPlayer(playList.nextSongIfItEnd(), true);
            return dataLoader(imgPause, timer.getTime());
        }
        return dataLoader(imgPlay,null);
    }

    @Override
    public Data clickRandom() {
        if (!playList.isEmpty()) {
            initPlayer(playList.getRandomSong(), true);
            return dataLoader(imgPause, null);
        }
        return dataLoader(imgPlay,null);
    }

    @Override
    public void sliderEdit(double value) {
        if (!playList.isEmpty()) {
            double percent = value;
            Duration total = player.getTotalDuration();
            Duration newTime = total.multiply(percent / 100);
            timer.setNewTime(newTime);
            player.seek(newTime);
        }
        else {
            iController.updateSlider(0);
        }
    }

    @Override
    public Image clickRepeat() {
        playList.changeRepeatSong();
        if (playList.isRepeatSong()){
            return imgRepeatSong;
        }
        return imgRepeat;
    }

    @Override
    public Data chooseFiles(Window window) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Виберіть аудіофайли");

        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.flac"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        List<File> files = chooser.showOpenMultipleDialog(window);
        return playList.chooseFiles(this,files);
    }

    public boolean isAudioFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".aac") || name.endsWith(".m4a");
    }

    public Data addFiles(List<File> files) {
        if (files == null || files.isEmpty()) return null;
        return playList.chooseFiles(this, files);
    }

    @Override
    public Data dataLoader(Image playButtonImage, String time) {
        if (!playList.isEmpty()) {
            Song song = playList.getCurrentSong();
            return new Data(song.getTitle(), song.getCover(), song.getLength(), playButtonImage, time);
        }
        return null;
    }
}

class Timer extends Thread {
    private volatile boolean running = true;
    private volatile boolean paused = true;

    private String time = "00:00";
    private Duration newTime;

    private final MediaPlayer player;
    private final IController iController;
    private double secSize;

    public Timer(MediaPlayer player, IController iController, Song song) {
        this.player = player;
        secSize = (int) song.getTotalSec();
        this.iController = iController;
        setDaemon(true);
    }

    public void startCounting() {
        paused = false;
    }

    public void pauseCounting() {
        paused = true;
    }

    public void stopTimer() {
        running = false;
        interrupt();
    }

    public String getTime(){
        return time;
    }

    public void setNewTime(Duration time){
        newTime = time;
    }


    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(900);

                if (paused) continue;

                Duration now = player.getCurrentTime();
                if (newTime!=null){
                    now = newTime;
                    newTime = null;
                }
                int totalSec = (int) now.toSeconds();
                int min = totalSec / 60;
                int sec = totalSec % 60;
                time = String.format("%02d:%02d", min, sec);
                double prozent = (totalSec/secSize)*100;
                javafx.application.Platform.runLater(() -> {
                    iController.setTime(time);
                    iController.updateSlider(prozent);
                });
            } catch (InterruptedException e) {
                if (!running) break;
            }
        }
    }
}
