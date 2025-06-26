package Logic;

import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class DiscordRpcManager {
    private static final String CLIENT_ID = "1386466007064772709";
    private static final DiscordRPC lib = DiscordRPC.INSTANCE;
    private static final DiscordRichPresence presence = new DiscordRichPresence();
    private static Thread callbackThread;

    public static void start() {
        lib.Discord_Initialize(CLIENT_ID, null, true, null);

        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.largeImageKey = "/icons/plate.png";
        lib.Discord_UpdatePresence(presence);

        callbackThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "Discord-RPC-Callback-Thread");

        callbackThread.setDaemon(true);
        callbackThread.start();
    }

    public static void update(String title) {

        presence.details = "🎵 " + title;
//        presence.state = artist;
        presence.largeImageKey = "/icons/plate.png"; // logo
        presence.largeImageText = "AudioPlayer";
//        presence.smallImageKey = isPlaying ? "play_icon" : "pause_icon";
//        presence.smallImageText = isPlaying ? "Воспроизведение" : "Пауза";
        lib.Discord_UpdatePresence(presence);

    }

    public static void stop() {
        if (callbackThread != null) {
            callbackThread.interrupt();
        }
        lib.Discord_Shutdown();
    }
}
