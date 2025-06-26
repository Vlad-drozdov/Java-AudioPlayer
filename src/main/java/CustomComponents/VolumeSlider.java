package CustomComponents;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class VolumeSlider extends HBox {

    private static final int SEGMENTS = 32;
    private static final double PERCENT_PER_SEGMENT = 100.0 / SEGMENTS;
    private int currentIndex = 10; // начальный уровень
    private static final double WIDTH = 6;
    private static final double HEIGHT_STEP = 2.0;
    private static final double START_HEIGHT = 5;

    private Rectangle[] bars = new Rectangle[SEGMENTS];

    public VolumeSlider() {
        super(4); // spacing
        setPadding(new Insets(10));
        setAlignment(Pos.BOTTOM_LEFT);
        setCursor(javafx.scene.Cursor.HAND);

        for (int i = 0; i < SEGMENTS; i++) {
            double height = START_HEIGHT + HEIGHT_STEP * (i + 1);
            Rectangle rect = new Rectangle(WIDTH, height);
            rect.setArcWidth(6);
            rect.setArcHeight(6);
            rect.setFill(i <= currentIndex ? Color.web("#3C3C3C"): Color.BLACK);

            StackPane wrapper = new StackPane();
            wrapper.setPrefHeight(START_HEIGHT + HEIGHT_STEP * SEGMENTS + 10);
            wrapper.setMinWidth(WIDTH);
            wrapper.getChildren().add(rect);
            StackPane.setAlignment(rect, Pos.BOTTOM_CENTER);

            bars[i] = rect;
            getChildren().add(wrapper);
        }

        setOnMousePressed(this::handleClick);
        setOnMouseDragged(this::handleClick);
        setVolume(100);
    }

    private void handleClick(MouseEvent event) {
        double x = event.getX();
        double totalWidth = getWidth();
        int volume = (int) ((x / totalWidth) * 100);
        setVolume(volume);
    }

    public void setVolume(int volume) {
        volume = Math.max(0, Math.min(volume, 100));
        currentIndex = Math.min((int) Math.floor(volume / PERCENT_PER_SEGMENT), SEGMENTS - 1);
        updateBars();
    }

    public int getVolume() {
        return (int) Math.round(currentIndex * PERCENT_PER_SEGMENT);
    }

    private void updateBars() {
        for (int i = 0; i < SEGMENTS; i++) {
            bars[i].setFill(i <= currentIndex ?  Color.web("#3C3C3C"): Color.BLACK);
        }
    }
}
