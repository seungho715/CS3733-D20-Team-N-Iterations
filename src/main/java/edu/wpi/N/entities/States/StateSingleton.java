package edu.wpi.N.entities.States;

import edu.wpi.N.App;
import edu.wpi.N.algorithms.Algorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.memento.CareTaker;
import edu.wpi.N.entities.memento.Originator;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

public class StateSingleton {

  private static StateSingleton _instance = null;
  public Algorithm savedAlgo;
  public MapImageLoader mapImageLoader;
  public ChatMessagesState chatBotState;
  public Originator originator;
  public CareTaker careTaker;
  public int timeoutTime;
  public Timer timer;
  public TimerTask timerTask;
  private App mainApp = null;

  private StateSingleton() throws DBException {
    savedAlgo = new Algorithm();
    mapImageLoader = new MapImageLoader();
    chatBotState = new ChatMessagesState();
    originator = new Originator();
    careTaker = new CareTaker();

    // Initialize timeout time to 15000 milliseconds (15 seconds)
    timeoutTime = 15000;

    // Initialize timer
    timer = new Timer();

    // Initialize timer task that will reset application
    timerTask =
        new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(
                () -> {
                  System.out.println("Timer Ended!");
                  System.out.println("Reset Kiosk!");
                  try {
                    originator.getStateFromMemento(careTaker.get(0));
                    String path = originator.getState();
                    switchTheScene(path);
                  } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Why u no work? - Singleton constructor");
                  }
                });
          }
        };

    // Schedule the task with large delay on start up
    timer.schedule(timerTask, 1000000);
  }

  /**
   * provides reference to the main application class
   *
   * @param mainApp the main class of the application
   */
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void switchTheScene(String path) throws IOException {
    timer.cancel();
    timer.purge();
    mainApp.switchScene(path, this);
  }

  public static StateSingleton getInstance() throws DBException {
    if (_instance == null) {
      _instance = new StateSingleton();
    }
    return _instance;
  }

  public void update() throws DBException {
    StateSingleton singleton = StateSingleton.getInstance();
    singleton.timer.purge();
    singleton.timer.cancel();
    singleton.timer = new Timer();
    TimerTask timerTask =
        new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(
                () -> {
                  System.out.println("Timer Ended!");
                  System.out.println("Reset Kiosk!");
                  try {
                    singleton.originator.getStateFromMemento(singleton.careTaker.get(0));
                    String path = singleton.originator.getState();
                    switchTheScene(path);
                  } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Why u no work? - update()");
                  }
                });
          }
        };

    singleton.timer.schedule(timerTask, singleton.timeoutTime);
  }
}
