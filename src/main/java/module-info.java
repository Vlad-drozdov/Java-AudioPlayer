module AudioPlayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires jaudiotagger;
    requires javafx.media;
    requires com.github.kwhat.jnativehook;
    requires java.discord.rpc;
    requires com.google.gson;

    exports MainWindow;
    opens MainWindow to javafx.fxml;
    exports CustomComponents;
    opens CustomComponents to javafx.fxml;
    exports PlaylistItem;
    opens PlaylistItem to javafx.fxml, com.google.gson;
}
