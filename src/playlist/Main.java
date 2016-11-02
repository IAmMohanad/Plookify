/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playlist;
//update

import GUI_Template.DBConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Work
 */
public class Main extends Application {

    private ResultSet rs = null;
    private final static Connection c = DBConnection.getConnection();
    static PreparedStatement stmt = null;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("playlist.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //Launching the gui 
        launch(args);
    }

    public void connectDB(String sql) {
        try {
            stmt = c.prepareStatement(sql);
            stmt.executeUpdate();
            System.out.println("Created");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": connectDB " + e.getMessage());
            System.exit(0);
        }
    }

    //seperate method to return resultset
    public ResultSet connectDBrs(String sql) throws SQLException {

        try {            
         
            // create the java statement
            stmt = c.prepareStatement(sql);
            // execute the query, and get a java resultset
            rs = stmt.executeQuery();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": connectDBrs " + e.getMessage());
            System.exit(0);
        }
        return rs;
    }
}
