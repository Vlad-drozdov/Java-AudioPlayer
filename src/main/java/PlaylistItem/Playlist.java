package PlaylistItem;

import Logic.Data;
import Logic.Player;
import Logic.Song;
import javafx.scene.image.Image;

import java.io.File;
import java.util.*;

public class Playlist {

    private String title;
    private String number;
    private String id;
    private PlaylistController controller;

    private ArrayList<Song> songs = new ArrayList<>();
    private List<Integer> randomList = new ArrayList<>();
    private int currentIndex = 0;
    private boolean repeatSong = false;
    private boolean randomSong = false;

    public Playlist(String title, PlaylistController controller) {
        this.title = title;
        this.id = UUID.randomUUID().toString();
        this.controller = controller;
    }

    public Playlist(String title, String id, PlaylistController controller){
        this.title = title;
        this.id = id;
        this.controller = controller;
    }

    public Playlist(String title, PlaylistController controller, String number) {
        this.title = title;
        this.id = UUID.randomUUID().toString();
        this.number = number;
        this.controller = controller;
    }



    public void addSong(Song song) {
        songs.add(song);
        if (!randomList.isEmpty()){
            randomList.add(songs.indexOf(song));
        }
        controller.updateInfo();
    }

    public Song getCurrentSong() {
        if (isPlaylistEmpty()) return null;
        return songs.get(currentIndex);
    }

    public Song nextSong() {
        if (isPlaylistEmpty()) return null;
        if (randomSong){ randomAlgorithm();
        } else { currentIndex = (currentIndex + 1) % songs.size(); }
        return getCurrentSong();
    }

    public Song nextSongIfItEnd() {
        if (isPlaylistEmpty()) return null;
        if (!repeatSong){
            if (randomSong){
                randomAlgorithm();
            }
            else {
                currentIndex = (currentIndex + 1) % songs.size();
            }
        }
        return getCurrentSong();
    }



    public Song previousSong() {
        if (isPlaylistEmpty()) return null;
        if (randomSong){ randomAlgorithm();
        } else { currentIndex = (currentIndex - 1 + songs.size()) % songs.size(); }
        return getCurrentSong();
    }

    public Song getRandomSong() {
        if (isPlaylistEmpty()) return null;
        Random random = new Random();
        int randomizeIndex;

        do {
            randomizeIndex = random.nextInt(songs.size());
        }while (randomizeIndex == currentIndex);

        currentIndex = randomizeIndex;
        return getCurrentSong();
    }

    private void fillRandomList() {
        randomList.clear();
        for (int i = 0; i < songs.size(); i++) {
            if (i != currentIndex)
                randomList.add(i);
        }
        Collections.shuffle(randomList);
    }

    private void randomAlgorithm() {
        if (randomList.isEmpty()) {
            fillRandomList();
        }
        currentIndex = randomList.remove(0);
    }


    public boolean isRepeatSong() {
        return repeatSong;
    }

    public void changeRepeatSong() {
        this.repeatSong = !repeatSong;
    }

    public boolean isRandomSong() {
        return randomSong;
    }

    public void changeRandomSong() {
        this.randomSong = !randomSong;
        if (randomSong) {
            randomList.clear();
            for (int i = 0; i < songs.size(); i++) {
                randomList.add(i);
            }
        } else {
            randomList.clear();
        }
    }

    public boolean isEmpty(){
         return songs.isEmpty();
    }

    private boolean isPlaylistEmpty() {
        if (songs.isEmpty()) {
            System.out.println("Плейліст порожній");
            return true;
        }
        return false;
    }

    public Data dataLoader(Image playButtonImage) {
        Song song = getCurrentSong();
        return new Data(song.getTitle(), song.getCover(), song.getLength(), playButtonImage);
    }

    public Data chooseFiles(List<File> files) {
        if (files != null) {
            for (File f : files) {
                try {
                    addSong(new Song(f));
                } catch (Exception e) {
                    System.out.println("Ви обрали не файл пісні");
                    throw new RuntimeException(e);
                }
            }
            updateMemory();
            if (!songs.isEmpty()) {
                Player.initPlayer(getRandomSong(), false);
                return dataLoader(null);
            }
        }
        return null;
    }

    public void updateMemory(){
        List<String> data = new ArrayList<>();
        for (Song song : songs) {
            data.add(song.getFile().toString());
        }
        PlaylistMemory.save(id, title, data, number);
    }

    public boolean loadMemory() {
        PlaylistMemory.PlaylistData data = PlaylistMemory.load(id);
        if (data == null) return false;

        this.title = data.getTitle();
        this.number = data.getNumber();
        List<String> paths = data.getSongs();
        if (paths == null || paths.isEmpty()) return false;

        Iterator<String> iterator = paths.iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            try {
//                System.out.println("Попытка загрузить песню: " + path);
                addSong(new Song(new File(path)));
//                System.out.println("Песня загружена: " + path);
            } catch (Exception e) {
                iterator.remove();
                System.out.println("❌ Пісня недоступна (ошибка): " + path);
                e.printStackTrace();
            }
        }
        return !songs.isEmpty();
    }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCount(){ return songs.size(); }
}
