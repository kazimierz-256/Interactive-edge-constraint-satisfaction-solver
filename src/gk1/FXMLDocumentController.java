/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import static java.lang.Math.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Kazimierz
 */
public class FXMLDocumentController implements Initializable {

    ContextMenu contextMenu = new ContextMenu();

    @FXML
    private Canvas drawing;
    public CheckBox automaticRelations;

    @FXML
    private void automaticToggle(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        Reaction reaction = GK1.model.toggleAutomaticRelations(newValue);
        if (reaction.isShouldRender()) {
            GK1.viewer.drawModel(GK1.model);
        }

        if (reaction.isShouldChangeCursor()) {
            drawing.setCursor(reaction.getDesiredCursor());
        } else {
            drawing.setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    private void mouseMoved(MouseEvent mouseEvent) {
        Reaction reaction = GK1.model.mouseMoved(mouseEvent);
        if (reaction.isShouldRender()) {
            GK1.viewer.drawModel(GK1.model);
        }

        if (reaction.isShouldChangeCursor()) {
            drawing.setCursor(reaction.getDesiredCursor());
        } else {
            drawing.setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    private void mousePressed(MouseEvent mouseEvent) {
        contextMenu.hide();
        if (MouseButton.SECONDARY == mouseEvent.getButton()) {
            // display the contextmenu!
            ArrayList<MenuItem> menuItems = GK1.model.buildContextMenu(mouseEvent);
            if (menuItems.isEmpty()) {
                return;
            }
            contextMenu.getItems().clear();
            contextMenu.getItems().addAll(menuItems);
            contextMenu.show(drawing, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        } else {
            // just clicked!
            Reaction reaction = GK1.model.mousePressed(mouseEvent);
            GK1.viewer.drawModel(GK1.model);

        }
    }

    @FXML
    private void mouseReleased(MouseEvent mouseEvent) {
        Reaction reaction = GK1.model.mouseReleased(mouseEvent);
        if (reaction.isShouldRender()) {
            GK1.viewer.drawModel(GK1.model);
        }
    }

    @FXML
    private void mouseDrag(MouseEvent mouseEvent) {
        mouseMoved(mouseEvent);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Polygon newPolygon = new Polygon(
                "Default",
                false,
                Arrays.asList(
                        new Vertex(100, 100, true),
                        new Vertex(400, 100),
                        new Vertex(200, 400),
                        new Vertex(100, 500))
        );

        LightSource light = new LightSource(
                new Vertex(400, 300, 100),
                0xff_ff_ff_ff
        );

        GK1.model = new Model();
        GK1.model.registerPolygon(newPolygon);
        GK1.model.registerLight(light);
        GK1.viewer = new Viewer(drawing, 600, 600);

        long startedTime = System.currentTimeMillis();

        Timeline fiveSecondsWonder = new Timeline(
                new KeyFrame(javafx.util.Duration.millis(50), (ActionEvent event) -> {

                    // animate the light source
                    double t = (System.currentTimeMillis() - startedTime) / 10_000d;
                    double radius = 100 + 200 * sin(t * log(t));
                    double phase = t + sin(t * log(t)) * sqrt(t);
                    double z = 110 + 100 * sin(10 * sin(t));
                    light.setPosition(new Vertex(400 + radius * cos(phase),
                            300 + radius * sin(phase), z));

                    // draw the model
                    GK1.viewer.drawModel(GK1.model);
                }));

        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);

        fiveSecondsWonder.play();
    }
}
