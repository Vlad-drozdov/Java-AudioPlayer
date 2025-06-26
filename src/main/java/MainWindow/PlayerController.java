package MainWindow;

import Logic.Data;

public interface PlayerController {
    void updateTime(String time);
    void updateSlider(double value);
    void dataHandler(Data data);
    void playRotation();
    void stopRotation();
    void resetRotation();
    void loadImage();
    void centerVisible(boolean b);
}
