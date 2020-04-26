package edu.wpi.N.database;
// TODO: Add your imports

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.N.entities.*;
import edu.wpi.N.entities.employees.EmotionalSupporter;
import edu.wpi.N.entities.request.Request;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import org.junit.jupiter.api.*;

/*TODO: implement tests for your serviceType
Follow the golden rule of database tests: WHEN THE CONTROL FLOW EXITS YOUR FUNCTION, THE DATABASE SHOULD BE 110,000%
IDENTICAL TO WHAT IT WAS WHEN CONTROL FLOW ENTERED YOUR FUNCTION
For service requests, if you mutate them, that's probably ok.
DO NOT RELY ON IDS BEING PARTICULAR VALUES
ServiceDB.getEmployee(1) <--- NO
TURN OFF AUTOCOMMIT BEFORE ENTERING YOUR TESTS, CATCH DBEXCEPTION AND ROLLBACK
*/
public class ServicesTest {
  private static Connection con;

  @BeforeAll
  public static void setup()
      throws ClassNotFoundException, SQLException, DBException, FileNotFoundException {
    MapDB.initTestDB();
    con = MapDB.getCon();
  }

  @Test
  public void example() throws DBException {

    Assertions.assertTrue(true);

    try {
      con.setAutoCommit(false);
      // Insertion statements, like addTranslator
      con.commit();
      con.setAutoCommit(true);
      // checking statements
      // deleting statements
    } catch (SQLException e) { // also wanna catch DBException e
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that function returns all Emotional supporters from the database
   *
   * @throws DBException
   */
  @Test
  public void testGetAllEmotionalSupporters() throws DBException {
    try {
      con.setAutoCommit(false);
      // Insertion statements, like addTranslator
      int idEOne = ServiceDB.addEmotionalSupporter("Ivan Eroshenko");

      LinkedList<String> langs = new LinkedList<String>();
      langs.add("English");
      int idTOne = ServiceDB.addTranslator("Matt Damon", langs);

      int idLOne = ServiceDB.addLaundry("Mike Laks");
      int idLTwo = ServiceDB.addLaundry("Nick Wood");

      int idETwo = ServiceDB.addEmotionalSupporter("Annie Fan");
      int idEThree = ServiceDB.addEmotionalSupporter("Chriss Lee");

      con.commit();
      con.setAutoCommit(true);
      // checking statements
      LinkedList<EmotionalSupporter> emotionalEmployees = ServiceDB.getEmotionalSupporters();
      Assertions.assertTrue(emotionalEmployees.contains(ServiceDB.getEmployee(idEOne)));
      Assertions.assertTrue(emotionalEmployees.contains(ServiceDB.getEmployee(idETwo)));
      Assertions.assertTrue(emotionalEmployees.contains(ServiceDB.getEmployee(idEThree)));
      Assertions.assertFalse(emotionalEmployees.contains(ServiceDB.getEmployee(idTOne)));
      Assertions.assertFalse(emotionalEmployees.contains(ServiceDB.getEmployee(idLOne)));
      Assertions.assertFalse(emotionalEmployees.contains(ServiceDB.getEmployee(idLTwo)));

      // deleting statements
      ServiceDB.removeEmployee(idEOne);
      ServiceDB.removeEmployee(idTOne);
      ServiceDB.removeEmployee(idLOne);
      ServiceDB.removeEmployee(idLTwo);
      ServiceDB.removeEmployee(idETwo);
      ServiceDB.removeEmployee(idEThree);

    } catch (SQLException e) { // also wanna catch DBException e
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that function returns a Emotional Support Request if given ID matches Emotional Request
   *
   * @throws DBException
   */
  @Test
  public void testGetRequestEmotionalSupport() throws DBException {
    try {
      con.setAutoCommit(false);
      // Insertion statements, like addTranslator
      DbNode node = MapDB.addNode(5, 5, 1, "TestBuilding", "STAI", "My test", "Short");
      // add Emotional support
      int idE =
          ServiceDB.addEmotSuppReq("Software Engineering class", node.getNodeID(), "Individual");
      // add Laundry
      int idL = ServiceDB.addLaundReq("I shit my pants", node.getNodeID());
      // add Translator
      int idT = ServiceDB.addTransReq("Помогите!", node.getNodeID(), "Russian");
      con.commit();
      con.setAutoCommit(true);
      // checking statements

      Request request = ServiceDB.getRequest(idE);
      Assertions.assertTrue(request.equals(ServiceDB.getRequest(idE)));

      // deleting statements
      ServiceDB.denyRequest(idE, "Nope");
      ServiceDB.denyRequest(idL, "Nope");
      ServiceDB.denyRequest(idT, "Nope");

    } catch (SQLException e) { // also wanna catch DBException e
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that function getRequests returns all available requests including EmotionalSupport
   *
   * @throws DBException
   */
  @Test
  public void testGetAllRequestsIncludingEmotionalSupport() throws DBException {
    try {
      con.setAutoCommit(false);
      // Insertion statements, like addTranslator
      DbNode node = MapDB.addNode(5, 5, 1, "TestBuilding", "STAI", "My test", "Short");
      // add Emotional support
      int idE =
          ServiceDB.addEmotSuppReq("Software Engineering class", node.getNodeID(), "Individual");
      // add Laundry
      int idL = ServiceDB.addLaundReq("I shit my pants", node.getNodeID());
      // add Translator
      int idT = ServiceDB.addTransReq("Помогите!", node.getNodeID(), "Russian");

      // add requests which will be open
      int idEO = ServiceDB.addEmotSuppReq("Gladiator", node.getNodeID(), "Individual");
      // add Laundry
      int idLO = ServiceDB.addLaundReq("Forrest Gump", node.getNodeID());
      // add Translator
      int idTO = ServiceDB.addTransReq("Помогите! Пожалуйста", node.getNodeID(), "Russian");

      ServiceDB.denyRequest(idE, "Nope");
      ServiceDB.denyRequest(idL, "Nope");
      ServiceDB.denyRequest(idT, "Nope");

      con.commit();
      con.setAutoCommit(true);
      // checking statements

      LinkedList<Request> allRequests = ServiceDB.getRequests();

      Assertions.assertTrue(allRequests.size() >= 6);
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idE)));
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idL)));
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idT)));
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idEO)));
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idLO)));
      Assertions.assertTrue(allRequests.contains(ServiceDB.getRequest(idTO)));

      // deleting statements

      ServiceDB.denyRequest(idEO, "Nope");
      ServiceDB.denyRequest(idLO, "Nope");
      ServiceDB.denyRequest(idTO, "Nope");
    } catch (SQLException e) { // also wanna catch DBException e
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that get all open requests returns all Open requests including open emotional requests
   *
   * @throws DBException
   */
  @Test
  public void testGetAllOpenRequestsIncludingEmotionalSupport() throws DBException {
    try {
      con.setAutoCommit(false);
      // Insertion statements, like addTranslator
      DbNode node = MapDB.addNode(5, 5, 1, "TestBuilding", "STAI", "My test", "Short");
      // add Emotional support
      int idE =
          ServiceDB.addEmotSuppReq("Software Engineering class", node.getNodeID(), "Individual");
      // add Laundry
      int idL = ServiceDB.addLaundReq("I shit my pants", node.getNodeID());
      // add Translator
      int idT = ServiceDB.addTransReq("Помогите!", node.getNodeID(), "Russian");
      con.commit();
      con.setAutoCommit(true);
      // checking statements
      LinkedList<Request> openReqs = ServiceDB.getOpenRequests();
      Assertions.assertTrue(openReqs.size() >= 3);
      Assertions.assertTrue(openReqs.contains(ServiceDB.getRequest(idE)));
      // deleting statements
      ServiceDB.denyRequest(idE, "Nope");
      ServiceDB.denyRequest(idL, "Nope");
      ServiceDB.denyRequest(idT, "Nope");
    } catch (SQLException e) { // also wanna catch DBException e
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests adding emotional supporter to the database
   *
   * @throws DBException
   */
  @Test
  public void testAddEmotionalSupporter() throws DBException {
    try {
      con.setAutoCommit(false);
      // Insertion statements, like addTranslator
      int id = ServiceDB.addEmotionalSupporter("Ivan Eroshenko");

      con.commit();
      con.setAutoCommit(true);
      // checking statements

      Assertions.assertTrue("Ivan Eroshenko".equals(ServiceDB.getEmployee(id).getName()));
      Assertions.assertTrue("Emotional Support".equals(ServiceDB.getEmployee(id).getServiceType()));
      // deleting statements
      ServiceDB.removeEmployee(id);
    } catch (SQLException e) { // also wanna catch DBException e
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  /**
   * Tests that request for Emotional Support gets added correctly to the database
   *
   * @throws DBException
   */
  @Test
  public void testAddEmotSuppReq() throws DBException {
    try {
      con.setAutoCommit(false);
      DbNode node = MapDB.addNode(5, 5, 1, "TestBuilding", "STAI", "My test", "Short");
      int id =
          ServiceDB.addEmotSuppReq("Software Engineering class", node.getNodeID(), "Individual");
      con.commit();
      con.setAutoCommit(true);
      // checking statements
      String type = ServiceDB.getRequest(id).getServiceType();
      Assertions.assertTrue("Emotional Support".equals(type));
      Assertions.assertTrue(id != 0);
      // deleting statements
      ServiceDB.denyRequest(id, "Fuck off!");
    } catch (SQLException e) { // also wanna catch DBException e
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException ex) {
        throw new DBException("Oh no");
      }
    }
  }

  @AfterEach
  public void clearDB() throws DBException {
    MapDB.clearNodes();
  }
}