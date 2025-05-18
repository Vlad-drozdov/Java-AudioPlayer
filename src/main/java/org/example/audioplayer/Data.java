package org.example.audioplayer;

import javafx.scene.image.Image;

import java.util.Objects;

public class Data {
    private String songTitle;
    private Image songImage;
    private String songLength;
    private Image playButtonImage;

    public Data(String songTitle,Image songImage, String songLength, Image playButtonImage){
        if (songTitle.isEmpty()){
            this.songTitle = "Невідома назва";
        }
        else {
            this.songTitle = songTitle;
        } if (songImage == null){
            this.songImage = new Image("/org/example/audioplayer/icon.png");
        }
        else {
            this.songImage = songImage;
        }
        this.songLength = songLength;
        if (playButtonImage==null){
            this.playButtonImage = new Image(Objects.requireNonNull(getClass().getResource("/org/example/audioplayer/play.png"), "Resource not found: " + "/org/example/audioplayer/play.png").toExternalForm());
        }else {
            this.playButtonImage = playButtonImage;
        }

    }

    public String getSongTitle() {
        return songTitle;
    }

    public Image getSongImage() {
        return songImage;
    }

    public String getSongLength() {
        return songLength;
    }

    public Image getPlayButtonImage() {
        return playButtonImage;
    }
}
