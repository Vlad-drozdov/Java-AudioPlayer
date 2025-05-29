package org.example.audioplayer;

import javafx.scene.image.Image;
import javafx.stage.Window;

import java.io.File;
import java.util.List;

public interface Model {
    Data clickLast();
    Data clickPlay();
    Data clickNext();
    Data clickRandom();
    Data dataLoader(Image playButtonImage, String time);
    void sliderEdit(double value);
    Image clickRepeat();
    Data chooseFiles(Window window);
    boolean isAudioFile(File file);
    Data addFiles(List<File> files);
}
