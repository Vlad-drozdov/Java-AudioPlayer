package PlaylistItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class PlaylistMemory {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String folderPath = "src/main/data/playlists/";

    // Сохранение данных
    public static void save(String id, String title, List<String> songs, String number) {
        List<String> uniqueSongs = new ArrayList<>(new LinkedHashSet<>(songs));

        PlaylistData data = new PlaylistData(title, uniqueSongs, number);
        File file = new File(folderPath + id + ".json");

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(data, writer);
            System.out.println("✅ Плейліст '" + title + "' успішно збережено");
        } catch (IOException e) {
            System.err.println("❌ Помилка збереження: " + e.getMessage());
        }
    }

    // Загрузка одного плейлиста
    public static PlaylistData load(String id) {
        File file = new File(folderPath + id + ".json");
        if (!file.exists()) {
            System.out.println("⚠️ Файл не знайдено: " + id);
            return null;
        }

        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, PlaylistData.class);
        } catch (IOException e) {
            System.err.println("❌ Помилка завантаження: " + e.getMessage());
            return null;
        }
    }

    // Загрузка всех id плейлистов
    public static List<String> loadAllPlaylists() {
        File folder = new File(folderPath);
        List<String> playlists = new ArrayList<>();

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("❌ Папка не існує або не є директорією");
            return null;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return null;

        // Сортировка: номер -> имя плейлиста
        Map<Integer, String> sortMap = new TreeMap<>();

        for (File file : files) {
            String name = file.getName().replace(".json", "");
            PlaylistData data = load(name);

            try {
                int number = Integer.parseInt(data.getNumber());
                sortMap.put(number, name);
            } catch (NumberFormatException e) {
                System.err.println("⚠️ Неправильний номер плейлиста: " + data.getNumber());
            }
        }

        playlists.addAll(sortMap.values());
        return playlists;
    }


    public static void del(String id) {
        File file = new File(folderPath + id + ".json");
        if (file.exists()) {
            if (file.delete()) {
//                System.out.println("✅ Файл успішно видалено: " + id);
            } else {
                System.err.println("❌ Не вдалося видалити файл: " + id);
            }
        } else {
            System.err.println("⚠️ Файл не знайдено: " + id);
        }
    }



    static class PlaylistData {
        private String title;
        private String number;
        private List<String> songs;

        public PlaylistData(String title, List<String> songs, String number) {
            this.title = title;
            this.songs = songs;
            this.number = number;
        }

        public String getTitle() { return title; }
        public String getNumber() { return number; }
        public List<String> getSongs() { return songs; }

        public void setTitle(String title) { this.title = title; }
        public void setNumber(String number) { this.number = number; }
        public void setSongs(List<String> songs) { this.songs = songs; }
    }

}
