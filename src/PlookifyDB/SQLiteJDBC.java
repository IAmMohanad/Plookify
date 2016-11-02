package test;

import java.sql.*;

public class SQLiteJDBC
{
  public static void main( String args[] )
  {
    Connection c = null;
     Statement stmt = null;
    try {
      Class.forName("org.sqlite.JDBC");
	// Class.forName("com.mysql.jdbc.Driver");
      c = DriverManager.getConnection("jdbc:sqlite:../SE36/Plookify/src/PlookifyDB/PlookifyDB.db");//change this to the path of the database
      System.out.println("Opened database successfully!");
      //************************************************
      //                Change this to run any queries.
      
      /*
      stmt = c.createStatement();
      String sql = "CREATE TABLE if not exists track " +
                   "(idtrack INTEGER PRIMARY KEY," +
                   " artistID INT NULL, " + 
                   " trackGenre VARCHAR(45) NULL, "+
                  "trackName VARCHAR(45) NULL,"+
                    "trackLength VARCHAR(45) NULL,"+
                    "trackPath VARCHAR(45) NULL,"+
                    "CONSTRAINT idartist,"+
                    "  FOREIGN KEY (artistID)"+
                     " REFERENCES artist(idartist)"+
                    "  ON DELETE NO ACTION"+
                      "ON UPDATE NO ACTION)";
      
      stmt.executeUpdate(sql);
      stmt.close();
      c.close();
      System.out.println("Table created successfully!");
      */
      
      //************************************************
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    
  }
}


//C:Users:mas34:Desktop:SOFTWARE_ENG_PROJECT:SoftwareEngeeringProject.db

//C:\Users\mas34\Desktop\SOFTWARE_ENG_PROJECT\sqlite-jdbc-3.8.11.2.jar