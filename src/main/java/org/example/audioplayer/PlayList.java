package org.example.audioplayer;

import java.util.ArrayList;
import java.util.Random;

public class PlayList {
    private ArrayList<Song> songs = new ArrayList<>();
    private int currentIndex = 0;
    private int count;

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

    public Song previousSong() {
        if (songs.isEmpty()) return null;
        currentIndex = (currentIndex - 1 + songs.size()) % songs.size();
        return getCurrentSong();
    }

    public Song getRandomSong(){
        Random random = new Random();
        currentIndex = random.nextInt(0,songs.size());
        return getCurrentSong();
    }
}
