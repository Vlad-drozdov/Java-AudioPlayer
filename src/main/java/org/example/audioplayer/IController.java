package org.example.audioplayer;

import javafx.event.ActionEvent;

public interface IController {
    void setTime(String time);
    void updateSlider(double value);
    void dataHandler(Data data);
}
