package MainWindow;

import javafx.scene.image.Image;
import javafx.stage.Window;
import Logic.Data;

import java.io.File;
import java.util.List;

public interface PlayerModel {
    Data clickLast();
    Data clickPlay();
    Data clickNext();
    Data clickRandomBox();
    Image clickRepeat();
    Image clickRandom();
    void sliderEdit(double value);
    void sliderEdited(double value);
    void volumeEdit(double volume);
    Data chooseFiles(Window window);
    Data addFiles(List<File> files);
}
