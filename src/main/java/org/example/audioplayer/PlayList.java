package org.example.audioplayer;

import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PlayList {
    private ArrayList<Song> songs = new ArrayList<>();
    private int currentIndex = 0;
    private int count;
    private boolean repeatSong = false;
    private PlaylistMemory memory = new PlaylistMemory();

    public void addSong(Song song) {
        songs.add(song);
    }

    public Song getCurrentSong() {
        if (songs.isEmpty()) return null;
        return songs.get(currentIndex);
    }

    public Song nextSong() {
        if (songs.isEmpty()) return null;
        currentIndex = (currentIndex + 1) % songs.size();
        return getCurrentSong();
    }

    public Song nextSongIfItEnd() {
        if (songs.isEmpty()) return null;
        if (!repeatSong){
            currentIndex = (currentIndex + 1) % songs.size();
        }
        return getCurrentSong();
    }

    public Song previousSong() {
        if (songs.isEmpty()) return null;
        currentIndex = (currentIndex - 1 + songs.size()) % songs.size();
        return getCurrentSong();
    }

    public Song getRandomSong() {
        if (songs.isEmpty()) {
            System.out.println("Плейлист порожній, не можна обрати випадкову пісню.");
            return null;
        }
        Random random = new Random();
        currentIndex = random.nextInt(songs.size());
        return getCurrentSong();
    }

    public boolean isRepeatSong() {
        return repeatSong;
    }

    public void changeRepeatSong() {
        this.repeatSong = !repeatSong;
    }

    public boolean isEmpty(){
        return songs.isEmpty();
    }

    public Data dataLoader(Image playButtonImage, String time) {
        Song song = getCurrentSong();
        return new Data(song.getTitle(), song.getCover(), song.getLength(), playButtonImage, time);
    }

    public Data chooseFiles(PlayerService playerService, List<File> files) {
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
                playerService.initPlayer(getRandomSong(), false);
                return dataLoader(null, null);
            }
        }
        return null;
    }

    public void updateMemory(){
        List<String> data = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            data.add(songs.get(i).getFile().toString());
        }
        memory.save(data);
    }
    public boolean loadMemory() {
        List<String> data = memory.load();
        if (!data.isEmpty()) {
            Iterator<String> iterator = data.iterator();
            while (iterator.hasNext()) {
                String path = iterator.next();
                try {
                    addSong(new Song(new File(path)));
                } catch (Exception e) {
                    iterator.remove();
                    System.out.println("Песня удалена из плейлиста, так как нет к ней доступа");
                }
            }
            updateMemory();
            return !songs.isEmpty();
        }
        return false;
    }

}
