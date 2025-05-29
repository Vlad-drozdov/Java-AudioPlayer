package org.example.audioplayer;

import javafx.scene.image.Image;

import java.util.Objects;

public class Data {
    private String songTitle;
    private Image songImage;
    private String songLength;
    private Image playButtonImage;
    private String time;

    public Data(String songTitle,Image songImage, String songLength, Image playButtonImage, String time){
        if (songTitle.isEmpty()){
            this.songTitle = "Невідома назва";
        }
        else {
            this.songTitle = songTitle;
        } if (songImage == null){
            this.songImage = new Image("/org/example/audioplayer/icons/icon.png");
        }
        else {
            this.songImage = songImage;
        }
        this.songLength = songLength;
        if (playButtonImage==null){
            this.playButtonImage = new Image(Objects.requireNonNull(getClass().getResource("/org/example/audioplayer/icons/play.png"), "Resource not found: " + "/org/example/audioplayer/play.png").toExternalForm());
        }else {
            this.playButtonImage = playButtonImage;
        }
        if (time==null){
            this.time = "00:00";
        }
        else{
            this.time = time;
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

    public String getTime() {
        return time;
    }
}
