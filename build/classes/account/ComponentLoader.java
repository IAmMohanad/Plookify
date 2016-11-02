/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author anastasiasmirnova
 */
public class ComponentLoader extends Application {

    Stage stage;
    Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ComponentLoader.class.getResource("Welcome.fxml"));
        scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Register or Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
