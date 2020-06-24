package scenes;

import database.Database;
import events.Subscriber;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Switcher implements Subscriber {
    
    private final Stage stage;
    private final Scene loginScene;
    private final Scene mainScene;
    
    public Switcher(Stage stage, Scene loginScene, Scene mainScene) {
        this.stage = stage;
        this.loginScene = loginScene;
        this.mainScene = mainScene;
        changeScene(loginScene);
    }
    
    private void changeScene(Scene scene) {
        stage.setScene(scene);
        stage.centerOnScreen();
    }
    
    private void resizeStage() {
        stage.sizeToScene();
        stage.centerOnScreen();
    }
    
    @Override
    public void loginSuccessful(Database database) {
        changeScene(mainScene);
    }
    
    @Override
    public void logout() {
        changeScene(loginScene);
    }
    
    @Override
    public void exitClicked() {
        Platform.exit();
    }
    
    @Override
    public void backClicked() {
        resizeStage();
    }
    
    @Override
    public void calendarClicked() {
        resizeStage();
    }
    
    @Override
    public void customersClicked() {
        resizeStage();
    }
    
    @Override
    public void reportsClicked() {
        resizeStage();
    }
}
