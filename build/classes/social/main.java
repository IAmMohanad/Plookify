/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package social;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 *
 * @author mas34
 */
public class main extends Application{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {//throws Exception {
       // Database instance = new Database();
       
       //instance.connect();
        //System.out.println("Connected to database..");
        //instance.createTable();
       //System.out.println("created database..");
       
        launch(args);//Launches GUI
    
    
    }
   
    @Override
    public void start(Stage stage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("SocialManager.fxml"));
        
        Parent root = FXMLLoader.load(getClass().getResource("SocialManager.fxml"));
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }
}//END CLASS