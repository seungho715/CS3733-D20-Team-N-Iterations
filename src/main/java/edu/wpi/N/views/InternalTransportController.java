package edu.wpi.N.views;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTimePicker;
import edu.wpi.N.App;
import edu.wpi.N.algorithms.FuzzySearchAlgorithm;
import edu.wpi.N.database.DBException;
import edu.wpi.N.entities.DbNode;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;

public class InternalTransportController implements Controller {

  private App mainApp;

  // Add FXML Tags Here
  @FXML JFXComboBox<String> cmbo_dest;
  @FXML JFXComboBox<String> cmbo_pickup;
  @FXML JFXTextArea txtf_transNotes;
  @FXML JFXComboBox<String> cmbo_type;
  @FXML JFXTimePicker tp_transporttime;

  private ObservableList<String> fuzzySearchTextList =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList = new LinkedList<>();

  private ObservableList<String> fuzzySearchTextList1 =
      // List that fills TextViews
      FXCollections.observableArrayList();
  LinkedList<DbNode> fuzzySearchNodeList1 = new LinkedList<>();

  private String countVal = "";

  public InternalTransportController() throws DBException {}

  public void setMainApp(App mainApp) {
    this.mainApp = mainApp;
  }

  public void initialize() throws DBException {

    cmbo_dest.getEditor().setOnKeyTyped(this::locationTextChanged);
    cmbo_pickup.getEditor().setOnKeyTyped(this::locationTextChanged1);
    // Available types of support: Individual, Family, Couple, Group
    LinkedList<String> transportTypes = new LinkedList<String>();
    transportTypes.add("Wheelchair");
    transportTypes.add("Stretcher");

    ObservableList<String> transportTypeList = FXCollections.observableList(transportTypes);
    cmbo_type.setItems(transportTypeList);
  }

  @FXML
  public void autofillLocation(String currentText) {
    System.out.println(currentText);
    if (currentText.length() > 2) {
      try {
        fuzzySearchNodeList = FuzzySearchAlgorithm.suggestLocations(currentText);
      } catch (DBException e) {
        e.printStackTrace();
      }
      LinkedList<String> fuzzySearchStringList = new LinkedList<>();
      if (fuzzySearchNodeList != null) {

        for (DbNode node : fuzzySearchNodeList) {
          fuzzySearchStringList.add(node.getLongName());
        }
        fuzzySearchTextList = FXCollections.observableList(fuzzySearchStringList);
      }
      System.out.println(fuzzySearchTextList);
    }
    if (fuzzySearchTextList == null) fuzzySearchTextList.add("  ");
  }

  @FXML
  public void autofillLocation1(String currentText) {
    // System.out.println(currentText);
    if (currentText.length() > 2) {
      try {
        fuzzySearchNodeList1 = FuzzySearchAlgorithm.suggestLocations(currentText);
      } catch (DBException e) {
        e.printStackTrace();
      }
      LinkedList<String> fuzzySearchStringList = new LinkedList<>();
      if (fuzzySearchNodeList1 != null) {

        for (DbNode node : fuzzySearchNodeList1) {
          fuzzySearchStringList.add(node.getLongName());
        }
        fuzzySearchTextList1 = FXCollections.observableList(fuzzySearchStringList);
      }
    }
    if (fuzzySearchTextList1 == null) fuzzySearchTextList1.add("  ");
  }

  // Handler for location combo box
  @FXML
  public void locationTextChanged(KeyEvent event) {
    String curr = cmbo_dest.getEditor().getText();
    autofillLocation(curr);
    cmbo_dest.getItems().setAll(fuzzySearchTextList);
    cmbo_dest.show();
  }

  @FXML
  public void locationTextChanged1(KeyEvent event) {
    String curr = cmbo_pickup.getEditor().getText();
    autofillLocation1(curr);
    cmbo_pickup.getItems().setAll(fuzzySearchTextList1);
    cmbo_pickup.show();
  }

  // Create Transport Request
  @FXML
  public void createNewTransportationRequest() throws DBException {

    String typeSelection = cmbo_type.getSelectionModel().getSelectedItem();
    String nodeID;
    String pickupNodeID;
    int nodeIndex = 0;

    try {
      String curr = cmbo_dest.getEditor().getText();
      for (String name : fuzzySearchTextList) {
        if (name.equals(curr)) {
          nodeIndex++;
          break;
        }
      }
      nodeID = fuzzySearchNodeList.get(nodeIndex).getNodeID();
    } catch (IndexOutOfBoundsException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a destination location for your service request!");
      errorAlert.show();
      return;
    }

    try {
      String curr1 = cmbo_pickup.getEditor().getText();
      for (String name : fuzzySearchTextList1) {
        if (name.equals(curr1)) {
          nodeIndex++;
          break;
        }
      }
      pickupNodeID = fuzzySearchNodeList1.get(nodeIndex).getNodeID();
    } catch (IndexOutOfBoundsException e) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a pickup location for your service request!");
      errorAlert.show();
      return;
    }

    String notes = txtf_transNotes.getText();
    if (typeSelection == null) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setContentText("Please select a support type for your emotional support request!");
      errorAlert.show();
      return;
    }

    String time = tp_transporttime.getValue().toString();

    // int emotSuppReq = ServiceDB.addEmotSuppReq(notes, nodeID, supportSelection);
    //    App.adminDataStorage.addToList(emotSuppReq);

    txtf_transNotes.clear();
    cmbo_dest.getItems().clear();
    cmbo_pickup.getItems().clear();
    cmbo_type.getItems().clear();

    Alert confAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confAlert.setContentText("Request Recieved");
    confAlert.show();
  }
}
