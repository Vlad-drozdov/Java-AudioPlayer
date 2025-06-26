package MainWindow;

public class InitMainWindow {
    public static PlayerModel init(PlayerController iController){
        return new PlayerService(iController);
    }
}
