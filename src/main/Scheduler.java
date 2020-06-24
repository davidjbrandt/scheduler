package main;

import javafx.application.Application;
import javafx.stage.Stage;

public class Scheduler extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        new AppBuilder().setupApp(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
