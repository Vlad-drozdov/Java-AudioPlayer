package org.example.audioplayer;

public class Init {
    protected static Model init (IController iController){
        Model model = new PlayerService(iController);
        return model;
    }
}
