package org.example.audioplayer;

import javafx.scene.Node;
import javafx.scene.image.Image;

public interface Model {
    Data clickLast();
    Data clickPlay();
    Data clickNext();
    Data dataLoader(Image playButtonImage);
    void setIController(MainController mainController);
}
