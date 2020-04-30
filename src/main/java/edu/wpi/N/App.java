package edu.wpi.N;

import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import edu.wpi.N.views.mapDisplay.MapBaseController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {
  private Stage masterStage;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    // Configure the primary Stage
    this.masterStage = primaryStage;
    this.masterStage.setTitle("Brigham and Women's Hospital Kiosk Application");
    StateSingleton newSingleton = StateSingleton.getInstance();
    // TODO: update to home or what not
    switchScene("views/newHomePage.fxml", newSingleton);
    masterStage.setMaximized(true);
  }

  public Stage getStage() {
    return this.masterStage;
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }

  public void switchScene(String path, StateSingleton singleton) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource(path));

    // Inject Singleton object into specified "fx included" controller classes
    loader.setControllerFactory(
        param -> {
          if (param.equals(MapBaseController.class)) {
            return new MapBaseController(singleton);
          } else {
            try {
              return param.getConstructor().newInstance();
            } catch (Exception e) {
              e.printStackTrace();
              return null;
            }
          }
        });

    Pane pane = loader.load();

    Scene scene = new Scene(pane);
    masterStage.setScene(scene);
    // masterStage.setMaximized(true);
    masterStage.setFullScreenExitHint("");
    masterStage.show();
    Controller controller = loader.getController();
    controller.setMainApp(this);
    controller.setSingleton(singleton);
  }
}
