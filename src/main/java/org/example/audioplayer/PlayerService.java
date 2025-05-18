package org.example.audioplayer;

import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Objects;

public class PlayerService implements Model {
    private MediaPlayer player;

    private final Image imgPlay = load("/org/example/audioplayer/play.png");
    private final Image imgPause = load("/org/example/audioplayer/pause.png");
    private IController iController;
    private boolean play = false;

    private PlayList playList = new PlayList();
    String folderPath = "src/main/resources/org/example/audioplayer/songs1";
    File folder = new File(folderPath);



    {
        try {
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            playList.addSong(new Song(file));
                        }
                    }
                }
            } else {
                System.out.println("Папка не знайдена або це не директорія");
            }
            initPlayer(playList.getRandomSong(),false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initPlayer(Song song,Boolean isPlay) {
        if (player != null) {
            player.dispose();
        }
        player = new MediaPlayer(song.getMedia());

        player.setOnReady(() -> {
            System.out.println("Плеер готов");
            if (isPlay){
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
            clickNext();
            play = false;
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
            return dataLoader(imgPlay);
        }

        if (play) {
            player.pause();
            img = imgPlay;
        } else {
            player.play();
            img = imgPause;
        }
        Song song = playList.getCurrentSong();
        return dataLoader(img);
    }

    @Override
    public Data clickLast() {
        initPlayer(playList.previousSong(),true);
        Song song = playList.getCurrentSong();
        return new Data(song.getTitle(),song.getCover(),String.valueOf(song.getLength()),imgPause);
    }
    @Override
    public Data clickNext() {
        initPlayer(playList.nextSong(),true);
        Song song = playList.getCurrentSong();
        return new Data(song.getTitle(),song.getCover(),String.valueOf(song.getLength()),imgPause);
    }

    @Override
    public Data dataLoader(Image playButtonImage) {
        Song song = playList.getCurrentSong();
        return new Data(song.getTitle(),song.getCover(),song.getLength(),playButtonImage);
    }

    @Override
    public void setIController(MainController mainController) {
        iController = mainController;
    }

//    public void bindProgress(Slider slider) {
//        if (player == null) return;
//
//        player.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
//            double current = newTime.toSeconds();
//            double total = playList.getCurrentSong().getLength();
//            if (total > 0) {
//                slider.setValue(current / total);
//            }
//        });
//
//        player.setOnReady(() -> {
//            slider.setValue(0);
//        });
//    }
}

class Timer extends Thread {
    private volatile boolean running = true;
    private volatile boolean paused  = true;

    private final MediaPlayer player;
    private final IController iController;

    public Timer(MediaPlayer player, IController iController) {
        this.player = player;
        this.iController = iController;
        setDaemon(true);
    }

    /* управление */
    public void startCounting() { paused = false; }
    public void pauseCounting()  { paused = true;  }
    public void stopTimer()      { running = false; interrupt(); }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000);

                if (paused) continue;

                Duration now = player.getCurrentTime();
                int totalSec = (int) now.toSeconds();
                int min = totalSec / 60;
                int sec = totalSec % 60;

                String time = String.format("%02d:%02d", min, sec);
                iController.setTime(time);
            } catch (InterruptedException e) {
                if (!running) break;
            }
        }
    }
}