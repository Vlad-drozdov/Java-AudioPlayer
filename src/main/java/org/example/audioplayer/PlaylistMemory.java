package org.example.audioplayer;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class PlaylistMemory {
    private static String file = ("src/main/resources/org/example/audioplayer/playlists/playlist.txt");

    public void save(List<String> data){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < data.size(); j++) {
                    if (data.get(i).equals(data.get(j))&& i!=j){
                        data.remove(j);
                    }
                }
            }
            for (String song: data){
                writer.write(String.valueOf(song));
                writer.newLine();
            }
            System.out.println("Файл успішно збережено");
        } catch (IOException e) {
            System.err.println("Помилка збереження файлу: " + e.getMessage());
        }
    }

    public List<String> load() {
        List<String> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null){
                data.add(line);
            }
            System.out.println("Файл успішно завантажено");
        } catch (IOException e){
            System.err.println("Помилка завантаження файлу: " + e.getMessage());
        }
        return data;
    }
}
