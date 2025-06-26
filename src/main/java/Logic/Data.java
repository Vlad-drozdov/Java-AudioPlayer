package Logic;

import javafx.scene.image.Image;

import java.util.Objects;

public class Data {
    private String songTitle;
    private Image songImage;
    private String songLength;
    private Image playButtonImage;
//    private String time;

    public Data(String songTitle,Image songImage, String songLength, Image playButtonImage){

        this.songTitle = songTitle;

        this.songImage = songImage;
        if (songLength==null){
            this.songLength = "00:00";
        }
        else {
            this.songLength = songLength;
        }
        if (playButtonImage==null){
            this.playButtonImage = new Image(Objects.requireNonNull(getClass().getResource("/icons/play.png"), "Resource not found: " + "/play.png").toExternalForm());
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
