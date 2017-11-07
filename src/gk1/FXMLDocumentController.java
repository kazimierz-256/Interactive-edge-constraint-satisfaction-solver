/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import animation.Helicopter;
import animation.PoliceHeadlights;
import gk1.textures.ArgbHelper;
import gk1.textures.CachedImage;
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
    private ColorPicker lightColor;
    @FXML
    private ColorPicker backgroundColor;
    @FXML
    private TextField backgroundURL;
    @FXML
    private TextField displacementURL;
    @FXML
    private TextField normalURL;
    @FXML
    private TitledPane polygonTile;

    @FXML
    public void lightVectorDefault(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.setArtificialLight(true);
    }

    @FXML
    public void lightVectorAnimated(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.setArtificialLight(false);
    }

    @FXML
    public void normalVectorDefault(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.getTexture().setNormals(new CachedImage(
                0x00_7f_7f_ff
        ));
    }

    @FXML
    public void normalUrlChange(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.getTexture().setNormals(new CachedImage(
                normalURL.getText()
        ));
    }

    @FXML
    public void displacementVectorDefault(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.getTexture().setHeights(new CachedImage(
                0
        ));
    }

    @FXML
    public void displacementUrlChange(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.getTexture().setHeights(new CachedImage(
                displacementURL.getText()
        ));
    }

    @FXML
    public void backgroundUrlChange(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.getTexture().setBackground(new CachedImage(
                backgroundURL.getText()
        ));
    }

    @FXML
    public void backgroundColorChange(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.getTexture().setBackground(new CachedImage(
                ArgbHelper.fromColor(backgroundColor.getValue())
        ));
    }

    @FXML
    public void changeColor(Event event) {
        int color = ArgbHelper.fromColor(lightColor.getValue());
        GK1.model.getLightsList().stream().forEach((light) -> {
            light.setLight(color);
        });
    }

    @FXML
    private void toggleAutomaticRelations(Event event) {
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
                new Vertex(0, 0),
                0,
                32d,
                new Helicopter(123),
                new PoliceHeadlights(1)
        );

        LightSource light2 = new LightSource(
                new Vertex(0, 0),
                0xff_ff_aa_33,
                16d,
                new Helicopter(456)
        );

        LightSource light3 = new LightSource(
                new Vertex(0, 0),
                0,
                8d,
                new Helicopter(1293),
                new PoliceHeadlights(23)
        );

        GK1.model = new Model();
        GK1.model.registerPolygon(newPolygon);
        GK1.model.registerLight(light1);
        GK1.model.registerLight(light2);
        GK1.model.registerLight(light3);
        GK1.model.registerLight(mouseLight);
        GK1.viewer = new Viewer(drawing, 600, 600);

        long startedTime = System.currentTimeMillis();

        Timeline mainAnimationTimeline = new Timeline(
                new KeyFrame(javafx.util.Duration.millis(64), (ActionEvent event) -> {

//                    if (GK1.viewer.isCurrentlyDrawing()) {
//                        return;
//                    }
                    // animate the light source
                    double t = (System.currentTimeMillis() - startedTime) / 1000d;

                    GK1.model.getLightsList().forEach(light -> {
                        light.animate(t);
                    });

                    // draw the actual model
                    GK1.viewer.drawModel(GK1.model);
                }));

        mainAnimationTimeline.setCycleCount(Timeline.INDEFINITE);

        mainAnimationTimeline.play();
    }
}
