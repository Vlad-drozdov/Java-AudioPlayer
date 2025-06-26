package Listeners;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import MainWindow.MainController;

public class KeyboardListener implements EventHandler<KeyEvent> {

    private final MainController controller;

    public KeyboardListener(MainController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(KeyEvent event) {
        switch (event.getCode()) {
            case SPACE:
                controller.ClickPlay(null);
                event.consume();
                break;
            case RIGHT:
                controller.ClickNext(null);
                event.consume();
                break;
            case LEFT:
                controller.ClickLast(null);
                event.consume();
                break;
        }
    }
}

