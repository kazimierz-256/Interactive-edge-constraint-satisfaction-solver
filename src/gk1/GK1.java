/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Kazimierz
 */
public class GK1 extends Application {

    public static Model model;
    public static Viewer viewer;
    public static Scene accessScene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("gk1/style.css");
        GK1.accessScene = scene;
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            viewer.setWidth((double) newVal);
            model.draw(viewer);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            viewer.setHeight((double) newVal);
            model.draw(viewer);
        });
        primaryStage.setTitle("picoCAD");
        primaryStage.setScene(scene);
//        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
