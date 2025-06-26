package Logic;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.images.Artwork;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.Duration;

public class Song {

    private final File file;
    private final Media media;
    private final String title;
    private final String length;
    private final String artist;
    private final Image cover;
    private long totalSec;

    public Song(File mp3) throws Exception {

        this.file = mp3;
        MP3File file = (MP3File) AudioFileIO.read(mp3);
        this.media = new Media(mp3.toURI().toString());
        var tag     = file.getTagOrCreateDefault();

        String editTitle = this.file.getName();
        if (tag.getFirst(FieldKey.TITLE).isEmpty() && editTitle.endsWith(".mp3")) this.title = editTitle.substring(0,editTitle.length()-4);
        else this.title  = tag.getFirst(FieldKey.TITLE);

        this.artist = tag.getFirst(FieldKey.ARTIST);

        int seconds = file.getAudioHeader().getTrackLength();
        totalSec = Duration.ofSeconds(seconds).getSeconds();
        long min = totalSec / 60;
        long sec = totalSec % 60;
        String time = String.format("%02d:%02d", min, sec);
        this.length = time;

        Image img = null;
        Artwork art = tag.getFirstArtwork();
        if (art != null && art.getBinaryData() != null) {
            img = new Image(new ByteArrayInputStream(art.getBinaryData()));
        }else {
            img = new Image(getClass().getResourceAsStream("/icons/plate.png"));
        }
        this.cover = img;
    }

    public String getTitle() { return title==null||title.isBlank() ? file.getName()  : title; }
    public String getArtist() { return artist==null||artist.isBlank()? "Unknown artist" : artist; }
    public String getLength() { return length; }
    public Image getCover() { return cover; }
    public Media getMedia() {return media;}
    public long getTotalSec() {return totalSec;}
    public File getFile() {return file;}
}
