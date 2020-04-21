package edu.wpi.N;

import edu.wpi.N.database.*;
import edu.wpi.N.entities.DbNode;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.LinkedList;

public class Main {

  public static void main(String[] args) throws SQLException, DBException, ClassNotFoundException {
    DbController.initDB();
    InputStream nodes = Main.class.getResourceAsStream("csv/TeamNFloor4Nodes.csv");
    InputStream edges = Main.class.getResourceAsStream("csv/TeamNFloor4Edges.csv");
    CSVParser.parseCSV(nodes);
    CSVParser.parseCSV(edges);
    LinkedList<DbNode> offices = new LinkedList();
    offices.add(DbController.getNode("NDEPT01804"));
    offices.add(DbController.getNode("NDEPT00604"));
    DoctorController.addDoctor("Dr.Wilson Wong", "Softeng", offices);
    LinkedList<DbNode> offices1 = new LinkedList();
    offices1.add(DbController.getNode("NDEPT00104"));
    offices1.add(DbController.getNode("NDEPT00604"));
    DoctorController.addDoctor("Dr.Hue Jass", "Ligma", offices);
    LinkedList<DbNode> offices2 = new LinkedList();
    offices2.add(DbController.getNode("NDEPT00704"));
    offices2.add(DbController.getNode("NDEPT01004"));
    DoctorController.addDoctor("Dr.Seymour Butts", "Ligma", offices2);
    LinkedList<String> languagesA = new LinkedList<>();
    languagesA.add("English");
    LinkedList<String> languagesB = new LinkedList<>();
    languagesB.add("English");
    languagesB.add("Mandarin");
    LinkedList<String> languagesC = new LinkedList<>();
    languagesC.add("English");
    languagesC.add("Spanish");
    languagesC.add("Russian");

    EmployeeController.addLaundry("Joe");
    EmployeeController.addLaundry("Randy");
    EmployeeController.addLaundry("Wilson");
    EmployeeController.addTranslator("Bob", languagesA);
    EmployeeController.addTranslator("Andy", languagesB);
    EmployeeController.addTranslator("Camille", languagesC);
    App.launch(App.class, args);
  }
}
