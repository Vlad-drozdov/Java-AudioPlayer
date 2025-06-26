package Listeners;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.application.Platform;
import MainWindow.MainController;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MediaKeyListener implements NativeKeyListener {

    private MainController controller;
    private int muteVolume = -1;

    public MediaKeyListener(MainController controller) {
        this.controller = controller;

        try {
            // Отключение логов
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);

            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook();
            }

            GlobalScreen.addNativeKeyListener(this);

        } catch (NativeHookException ex) {
            System.err.println("Ошибка подключения JNativeHook:");
            ex.printStackTrace();
        }
    }


    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int code = e.getKeyCode();

        Platform.runLater(() -> {
            int volume = controller.getVolume();

            switch (code) {
                case NativeKeyEvent.VC_MEDIA_PLAY:
                    controller.ClickPlay(null);
                    break;
                case NativeKeyEvent.VC_MEDIA_NEXT:
                    controller.ClickNext(null);
                    break;
                case NativeKeyEvent.VC_MEDIA_PREVIOUS:
                    controller.ClickLast(null);
                    break;
                case NativeKeyEvent.VC_VOLUME_UP:
                    if (volume < 100) controller.setVolume(volume + 10);
                    break;
                case NativeKeyEvent.VC_VOLUME_DOWN:
                    if (volume > 0) controller.setVolume(volume - 10);
                    break;
                case NativeKeyEvent.VC_VOLUME_MUTE:
                    if (volume != 0) {
                        muteVolume = volume;
                        controller.setVolume(0);
                    } else if (muteVolume != -1) {
                        controller.setVolume(muteVolume);
                        muteVolume = -1;
                    }
                    break;
                default:
//                    System.out.println("Другая клавиша: " + NativeKeyEvent.getKeyText(code) + " (" + code + ")");
            }
        });
    }


    @Override public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}
}

