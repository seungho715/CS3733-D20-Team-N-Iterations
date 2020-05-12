package edu.wpi.N.views.chatbot;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.N.App;
import edu.wpi.N.Main;
import edu.wpi.N.chatbot.Dialogflow;
import edu.wpi.N.entities.States.StateSingleton;
import edu.wpi.N.views.Controller;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatbotController implements Controller, Initializable {
  private StateSingleton state;
  private App mainApp;

  @FXML private Pane mainPane;
  @FXML private JFXTextField textField;
  @FXML private VBox chatBox;
  @FXML private AnchorPane chatBotView;
  @FXML private AnchorPane buttonOnlyView;
  @FXML private ScrollPane scrollPane;
  @FXML private JFXButton btnSendMessage;

  // Inject state singleton
  public ChatbotController(StateSingleton state) {
    this.state = state;
  }

  /** Opens up the Chat-bot window */
  @FXML
  private void onBtnAskMeClicked() {

    // If message history is empty
    if (state.chatBotState.getMessageHistory().isEmpty()) {
      try {
        state.chatBotState.initSession();
      } catch (Exception e) {
        e.printStackTrace();
        displayErrorMessage("Error initializing Client Session");
      }
    } else {
      // else, load existing message history
      loadMessageHistory();
    }

    buttonOnlyView.setVisible(false);
    chatBotView.setVisible(true);
  }

  /** When user closes chat-dialog, close the Dialogflow Client Session and clear message history */
  @FXML
  private void onBtnCloseDialogClicked() throws IOException {
    state.chatBotState.closeSession();
    chatBotView.setVisible(false);
    buttonOnlyView.setVisible(true);
    chatBox.getChildren().clear();
  }

  /**
   * Loads previous message history into chatBox (Vbox) Loads in order. Chat-bot messages appear on
   * the left side, User messages on the right side
   */
  private void loadMessageHistory() {
    for (VBox singleMessage : state.chatBotState.getMessageHistory()) {
      chatBox.getChildren().add(singleMessage);
    }
  }

  /**
   * Upon click, sends the user's message to chat-bot Displays user's message and chat-bot reply in
   * Chatbox
   */
  @FXML
  private void onBtnSendMessageClicked() {
    if (!textField.getText().equals(null) && !textField.getText().equals("")) {

      // Display user's message first (right side)
      Label userInput = new Label(textField.getText());
      LinkedList<Node> messageContainer = new LinkedList<Node>();
      messageContainer.add(userInput);
      displayAndSaveMessages(messageContainer, true);

      // Display chat-bots reply (left side)
      try {
        // get chatbot's reply, display it and save in message history
        displayAndSaveBotMessage(userInput.getText());
      } catch (Exception e) {
        e.printStackTrace();
        displayErrorMessage("Error trying to load chat-bot's reply");
      }
    }
  }

  /**
   * Displays chat-bot's reply-message/messages and saves to chat-history
   *
   * @param userMessage: user's Input
   */
  private void displayAndSaveBotMessage(String userMessage) {
    try {
      QueryResult queryResults =
          state.chatBotState.dialogflow.detectIntentTexts(userMessage, "en-US");

      LinkedList<Node> singleMessageObject = new LinkedList<Node>();

      if (queryResults
          .getIntent()
          .getDisplayName()
          .equals("question-billing-how-to-pay-online-option")) {
        // create message with links
        Label messagePartA =
            new Label(
                "Great! It is the most convenient, efficient way to pay your bill. If you have a Partners Patient Gateway account, you can see the current status of all open patient balances; payments are immediately posted to your account.\n"
                    + "\n"
                    + "You can opt to go paperless and receive your bills online. In addition, you can now set up monthly payment plans.\n"
                    + "\n");
        Hyperlink loginLink = new Hyperlink("Log in to Partners Patient Gateway");
        loginLink.setOnMouseClicked(
            new EventHandler<MouseEvent>() {
              @Override
              public void handle(MouseEvent event) {
                try {
                  loadWebView("https://mychart.partners.org/mychart-prd/Authentication/Login?");
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            });

        Hyperlink guestLink = new Hyperlink("Or you can quickly pay as a guest");
        guestLink.setOnMouseClicked(
            new EventHandler<MouseEvent>() {
              @Override
              public void handle(MouseEvent event) {
                try {
                  loadWebView(
                      "https://mychart.partners.org/mychart-prd/billing/guestpay/payasguest");
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            });

        // add elements to a 'Single Message'
        singleMessageObject.add(messagePartA);
        singleMessageObject.add(loginLink);
        singleMessageObject.add(guestLink);
      }
      // If intent matches with get-weather
      else if (queryResults.getIntent().getDisplayName().equals("get-weather")) {
        Label message = new Label(Dialogflow.getCurrentWeatherReply());
        singleMessageObject.add(message);
      } else {
        // else, use Dialogflow text
        Label message = new Label(queryResults.getFulfillmentText());
        singleMessageObject.add(message);
      }

      displayAndSaveMessages(singleMessageObject, false);

    } catch (Exception ex) {
      ex.printStackTrace();
      displayErrorMessage("Ooops... Something went wong when loading chat-bot message");
    }
  }

  /**
   * Displays bot's reply(s) from a LinkedList of objects such as Labels/Hyperlinks
   *
   * @param messages: list of objects to display Output from
   * @param isUserMessage: boolean indicating whether the given message is user's message, true -
   *     yes
   */
  private void displayAndSaveMessages(LinkedList<Node> messages, boolean isUserMessage) {
    // Common settings for both User's and Chatbot's messages
    VBox singleMessage = new VBox(5);
    singleMessage.setPadding(new Insets(5));
    singleMessage.setFillWidth(false);

    for (Node n : messages) {
      singleMessage.getChildren().add(n);
    }

    if (isUserMessage) {
      singleMessage.getStylesheets().add(Main.class.getResource("css/UserMessage.css").toString());
      singleMessage.setAlignment(Pos.CENTER_RIGHT);
      textField.clear();
    } else {
      singleMessage.getStylesheets().add(Main.class.getResource("css/BotReply.css").toString());
    }

    // Update the chatBox (VBOX)
    chatBox.getChildren().add(singleMessage);

    // Save changes in Message history
    this.state.chatBotState.addMessageToHistory(singleMessage);
  }

  /**
   * Creates a new stage and Loads given link in a web-view
   *
   * @param url
   */
  private void loadWebView(String url) throws IOException {
    Stage browserStage = new Stage();
    browserStage.setTitle("Browser Window");

    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("browserWindow.fxml"));
    loader.setControllerFactory(
        type -> {
          try {
            return new BrowserController(url);
          } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
          }
        });

    Pane pane = loader.load();
    Scene scene = new Scene(pane);

    browserStage.setScene(scene);
    browserStage.setFullScreen(true);
    browserStage.show();
  }

  /**
   * Displays allert message
   *
   * @param str: message of the alert
   */
  private void displayErrorMessage(String str) {
    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setHeaderText("Something went wong...");
    errorAlert.setContentText(str);
    errorAlert.showAndWait();
  }

  @Override
  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // on enter pressed, send message automatically
    textField.addEventHandler(
        KeyEvent.KEY_RELEASED,
        keyEvent -> {
          if (keyEvent.getCode() == KeyCode.ENTER) {
            onBtnSendMessageClicked(); // send message
            keyEvent.consume();
          }
        });

    // Do such that scroll pane auto-scrolls down
    scrollPane.vvalueProperty().bind(chatBox.heightProperty());

    // If message history still persists, open the dialog window right away
    if (!state.chatBotState.getMessageHistory().isEmpty()) onBtnAskMeClicked();
  }
}
