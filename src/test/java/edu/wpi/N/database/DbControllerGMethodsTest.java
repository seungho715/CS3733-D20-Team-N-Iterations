package edu.wpi.N.database;

import edu.wpi.N.Main;
import edu.wpi.N.algorithms.Pathfinder;
import edu.wpi.N.entities.Node;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import org.junit.jupiter.api.*;

public class DbControllerGMethodsTest {

  @BeforeAll
  public static void initializeTest() throws Exception {
    DbController.initDB();
    InputStream inputNodes = Main.class.getResourceAsStream("csv/TestNodes.csv");
    InputStream inputEdges = Main.class.getResourceAsStream("csv/TestEdges.csv");
    CSVParser.parseCSV(inputNodes);
    CSVParser.parseCSV(inputEdges);
  }

  /** Tests that getGAdjacent(nodeID) returns the correct list of edges for a given node */
  @Test
  public void getGAdjacentTester() throws DBException {
    LinkedList<Node> hall3Edges = new LinkedList<Node>();
    hall3Edges.add(DbController.getGNode("H200000000"));
    hall3Edges.add(DbController.getGNode("H400000000"));
    Assertions.assertEquals(DbController.getGAdjacent("H300000000"), hall3Edges);
  }

  /**
   * Tests that getGAdjacent(nodeID) will return null if the node is not in the graph and if the
   * node does not have any edges
   */
  @Test
  public void getGAdjacentNullTester() throws DBException {
    // null for a node that is not in the database
    Node testNode = new Node(2.345, 5.5657, "TESTNODE02");
    Assertions.assertEquals(DbController.getGAdjacent("TESTNODE02"), new ArrayList<Node>());
    // null for a node that is in the database but has no edges
    DbController.addNode("TESTNODE03", 23, 345, 4, "Foisie", "sdfk", "fskjd", "sdfk", 'N');
    Assertions.assertEquals(DbController.getGAdjacent("TESTNODE3"), new ArrayList<Node>());
    DbController.deleteNode("TESTNODE03");
  }

  /**
   * Tests that addEdges(nodeID1,nodeID2) will add the given edge to the list of nodes for both
   * given nodes
   */
  @Test
  public void addEdgesTester() throws DBException {
    LinkedList<Node> hall1Edges = new LinkedList<Node>();
    hall1Edges.add(DbController.getGNode("H200000000"));
    hall1Edges.add(DbController.getGNode("H130000000"));
    DbController.addEdge("H100000000", "H130000000");
    Assertions.assertEquals(DbController.getGAdjacent("H100000000"), hall1Edges);

    // checks that H100000000 was added to H130000000 adjacent nodes
    LinkedList<Node> hall13Edges = new LinkedList<Node>();
    hall13Edges.add(DbController.getGNode("EEEEEEEEEE"));
    hall13Edges.add(DbController.getGNode("H100000000"));
    hall13Edges.add(DbController.getGNode("H120000000"));
    Assertions.assertEquals(DbController.getGAdjacent("H130000000"), hall13Edges);
    DbController.removeEdge("H100000000", "H130000000");
  }

  /**
   * Tests that addEdges(nodeID1,nodeID2) will create a list of edges for a node that currently has
   * no edges Can't test because all nodes in the csv have edges
   */
  /*
  @Test
  public void addEdgesEmptyNodeTester() {
    DbController.addNode("TESTNODE05", 23, 345, 4, "Foisie", "sdfkjd", "fskjd", "sdfk", 'N');
    DbController.addEdge("TESTNODE05", "H800000000");
    LinkedList<Node> testNodeEdges = new LinkedList<Node>();
    testNodeEdges.add(DbController.getGNode("H800000000"));
    Assertions.assertEquals(DbController.getGAdjacent("TESTNODE05"), testNodeEdges);
  }
  */

  /**
   * Tests that addEdges(nodeID1,nodeID2) will not add a node that does not exist in the database
   */
  @Test
  public void addInvalidEdgesTester() {
    Assertions.assertThrows(
        DBException.class, () -> DbController.addEdge("CCCCCCCCCC", "NOTANODE01"));
  }

  /**
   * Tests that a duplicate edge will not be added if you run addEdge(nodeID1,nodeID2) on an edge
   * that already exists
   */
  @Test
  public void addEdgeAlreadyThereTester() throws DBException {
    Assertions.assertFalse(DbController.addEdge("H500000000", "H600000000"));
  }

  /** Tests that heuristic(currNode, endNode) returns the correct calculated value */
  @Test
  public void heuristicTester() throws DBException {
    Assertions.assertEquals(
        Pathfinder.heuristic(
            DbController.getGNode("AAAAAAAAAA"), DbController.getGNode("BBBBBBBBBB")),
        455,
        0.01);
  }

  /**
   * Tests that getGNode(nodeID) returns the correct node when given a nodeID that is in the
   * database
   */
  @Test
  public void getNodeTester() throws DBException {
    Node testNode3 = new Node(447, 672, "BBBBBBBBBB");
    Assertions.assertEquals(DbController.getGNode("BBBBBBBBBB"), testNode3);
  }

  /** Second test for getGNode(nodeID) */
  @Test
  public void getNodeTester2() throws DBException {
    Node testNode4 = new Node(517, 904, "H700000000");
    Assertions.assertEquals(DbController.getGNode("H700000000"), testNode4);
  }

  /** Tests that getGNode(nodeID) returns null when given an nodeID that isn't in the database */
  @Test
  public void getNodeNullTester() throws DBException {
    // Change in future to reflect getting an exception/error
    Assertions.assertThrows(DBException.class, () -> DbController.getGNode("test1"));
  }

  /**
   * Tests that addNode(nodeID, x, y, floor, building, nodeType, longName, shortName, teamAssigned)
   * takes the given information and makes it into a node in the database
   */
  @Test
  public void addNodeTester() throws DBException {
    Node testNode5 = new Node(25, 30, "testNodeT5");
    DbController.addNode("testNodeT5", 25, 30, 4, "Buil", "OFFI", "TESTNODE5", "T5", 'Z');
    Assertions.assertEquals(DbController.getGNode("testNodeT5"), testNode5);
    DbController.deleteNode("testNodeT5");
  }

  /**
   * Second test for addNode(nodeID, x, y, floor, building, nodeType, longName, shortName,
   * teamAssigned)
   */
  @Test
  public void addNodeTester2() throws DBException {
    Node testNode6 = new Node(108, 55, "testNodeT6");
    DbController.addNode("testNodeT6", 108, 55, 4, "Buil", "OFFI", "TESTNODE6", "T6", 'Z');
    Assertions.assertEquals(DbController.getGNode("testNodeT6"), testNode6);
    DbController.deleteNode("testNodeT6");
  }

  //
  //  /**
  //   * Tests that addNode(node) doesn't add the given node to the database if it has the same ID
  //   * as another node in the database
  //   * (future test to implement once that functionality is added)
  //   */
  //

  /**
   * Tests that cost(currNode, nextNode) returns the correct cost value for nodes in the database
   */
  @Test
  public void costTester() throws DBException {
    Assertions.assertEquals(
        Pathfinder.cost(DbController.getGNode("AAAAAAAAAA"), DbController.getGNode("EEEEEEEEEE")),
        1196.75,
        0.05);
  }

  /**
   * Tests that cost(currNode, nextNode) returns the correct value for nodes even if they aren't in
   * the database
   */
  @Test
  public void costNotInGraphTester() {
    Node testNode7 = new Node(0, 0, "node7");
    Node testNode8 = new Node(3, 4, "node8");
    Assertions.assertEquals(Pathfinder.cost(testNode7, testNode8), 5, 0.0001);
  }

  @AfterAll
  public static void clearDB() throws DBException {
    DbController.clearNodes();
  }
}
