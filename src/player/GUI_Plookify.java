/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Petra
 */
public class GUI_Plookify extends Application {
    
    @Override
    public void start(Stage stage) throws Exception 
    {
        Parent root = FXMLLoader.load(getClass().getResource("GUITrack.fxml"));
        
        Scene scene = new Scene(root);
        URL url = this.getClass().getResource("style.css");
    if (url == null) {
        System.out.println("Resource not found. Aborting.");
        System.exit(-1);
    }
    String css = url.toExternalForm(); 
    scene.getStylesheets().add(css);;
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        launch(args);
        
    }
    
}
