module org.example.audioplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jaudiotagger;
    requires javafx.media;
    requires java.sql;


    opens org.example.audioplayer to javafx.fxml;
    exports org.example.audioplayer;
}