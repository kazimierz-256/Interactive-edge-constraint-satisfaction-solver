/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import gk1.textures.ArgbHelper;
import gk1.textures.CachedImage;
import static java.lang.Math.*;
import java.net.URL;
import java.util.*;
import javafx.animation.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.*;

/**
 * FXML Controller class
 *
 * @author Kazimierz
 */
public class FXMLDocumentController implements Initializable {

    private ContextMenu contextMenu = new ContextMenu();
    private LightSource mouseLight = new LightSource(
            new Vertex(0, 0),
            0xff_ff_ff_ff
    );

    @FXML
    private Canvas drawing;
    @FXML
    private ToggleButton automaticRelations;
    @FXML
    private ColorPicker lightColour;
    @FXML
    private ColorPicker textureColor;
    @FXML
    private TextField textureURL;

    @FXML
    public void textureUrlChange(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.getTexture().setTexture(new CachedImage(
                textureURL.getText()
        ));
    }

    @FXML
    public void textureColorChange(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.getTexture().setTexture(new CachedImage(
                ArgbHelper.fromColor(textureColor.getValue())
        ));
    }

    @FXML
    public void changeColour(Event event) {
        int color = ArgbHelper.fromColor(lightColour.getValue());
        GK1.model.getLightsList().stream().forEach((light) -> {
            light.setLight(color);
        });
    }

    @FXML
    private void toggleAutomaticRelations() {
        if (GK1.model.activePolygon == null) {
            return;
        }
        Reaction reaction = GK1.model.activePolygon.toggleAutomaticRelations(
                automaticRelations.isSelected()
        );

        if (reaction.shouldChangeCursor) {
            drawing.setCursor(reaction.desiredCursor);
        } else {
            drawing.setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    private void mouseMoved(MouseEvent mouseEvent) {
        mouseLight.setPosition(new Vertex(
                mouseEvent.getX(), mouseEvent.getY(), 50
        ));
        Reaction reaction = GK1.model.mouseMoved(mouseEvent);

        if (reaction.shouldChangeCursor) {
            drawing.setCursor(reaction.desiredCursor);
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
//            GK1.viewer.drawModel(GK1.model);

        }
    }

    @FXML
    private void mouseReleased(MouseEvent mouseEvent) {
        Reaction reaction = GK1.model.mouseReleased(mouseEvent);
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
                        new Vertex(650, 50),
                        new Vertex(600, 500),
                        new Vertex(150, 550))
        );

        LightSource light1 = new LightSource(
                new Vertex(400, 300, 100),
                0xff_ff_ff_dd
        );

        LightSource light2 = new LightSource(
                new Vertex(400, 300, 100),
                0xff_ff_aa_33
        );

        GK1.model = new Model();
        GK1.model.registerPolygon(newPolygon);
        GK1.model.registerLight(light1);
        GK1.model.registerLight(light2);
        GK1.model.registerLight(mouseLight);
        GK1.viewer = new Viewer(drawing, 600, 600);

        long startedTime = System.currentTimeMillis();

        Timeline fiveSecondsWonder = new Timeline(
                new KeyFrame(javafx.util.Duration.millis(64), (ActionEvent event) -> {

                    // animate the light source
                    double t = (System.currentTimeMillis() - startedTime) / 10_000d;

                    // light1 animation
                    double radius = 100 + 200 * cos(sin(t) + 10 * sin(2 * t));
                    double phase = -t / 2 + (sin(t) * sqrt(t));
                    double z = 200 + 200 * sin(10 * sin(t));
                    light1.setPosition(new Vertex(300 + radius * cos(phase),
                            200 + radius * sin(phase), z));

                    // light2 animation
                    radius = 200 + 100 * sin(sin(t) + 5 * sin(5 * t));
                    phase = t - 2 * cos(sin(t / 2) * sqrt(t));
                    z = 100 + 100 * sin(30 * sin(t));
                    light2.setPosition(new Vertex(200 + radius * cos(phase),
                            300 + radius * sin(phase), z));

                    // draw the actual model
                    GK1.viewer.drawModel(GK1.model);
                }));

        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);

        fiveSecondsWonder.play();
    }
}
