/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import animation.PeriodicOscillator;
import animation.OscillatoryColourLights;
import gk1.textures.ArgbHelper;
import gk1.textures.CachedImage;
import gk1.textures.Texture;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * FXML Controller class
 *
 * @author Kazimierz
 */
public class FXMLDocumentController implements Initializable {

    private ContextMenu contextMenu = new ContextMenu();
    private LightSource mouseLight = new LightSource(
            new Vertex(0, 0, 50),
            0xff_ff_ff_ff,
            16d,
            null,
            null
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
    public void displacementScaleChange(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        double scale = (double) new_val;
        scale *= scale;
        scale *= scale;
        GK1.model.activePolygon.getTexture().setHeightScale(scale);
    }

    @FXML
    public void mouseScroll(ScrollEvent event) {
        mouseLight.setZ(mouseLight.getZ()
                + Math.sqrt(Math.abs(mouseLight.getZ())) * (event.getDeltaY() / 16)
        );
    }

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
    public void normalMouseBumpChange(Event event) {
        if (GK1.model.activePolygon == null) {
            return;
        }
        GK1.model.activePolygon.getTexture().setNormals(new CachedImage(
                new Vertex(100, 100)
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
                mouseEvent.getX(), mouseEvent.getY(), mouseLight.getZ()
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

        Polygon newPolygon1 = new Polygon(
                "Default quadrilateral",
                false,
                Arrays.asList(
                        new Vertex(300, 100, true),
                        new Vertex(650, 50),
                        new Vertex(600, 500),
                        new Vertex(350, 550)
                ),
                Texture.getDefault()
        );

        Polygon newPolygon2 = new Polygon(
                "Default triangle",
                false,
                Arrays.asList(
                        new Vertex(200, 300),
                        new Vertex(450, 150),
                        new Vertex(300, 300)
                ),
                Texture.getDefault()
        );

        LightSource light1 = new LightSource(
                new Vertex(0, 0),
                0,
                16d,
                new PeriodicOscillator(123),
                new OscillatoryColourLights(1)
        );

        LightSource light2 = new LightSource(
                new Vertex(0, 0),
                0xff_ff_aa_33,
                8d,
                new PeriodicOscillator(456)
        );

        GK1.model = new Model();
        GK1.model.registerLight(light1);
        GK1.model.registerLight(light2);
        GK1.model.registerLight(mouseLight);
        GK1.model.registerPolygon(newPolygon1);
        GK1.model.registerPolygon(newPolygon2);
        GK1.viewer = new Viewer(drawing, 600, 600);

        long startedTime = System.currentTimeMillis();

        Timeline mainAnimationTimeline = new Timeline(
                new KeyFrame(javafx.util.Duration.millis(50), (ActionEvent event) -> {

//                    if (GK1.viewer.isCurrentlyDrawing()) {
//                        return;
//                    }
                    // animate the light source
                    // draw the actual model
                    GK1.viewer.drawModel(GK1.model);

                    double t = (System.currentTimeMillis() - startedTime) / 1000d;

                    GK1.model.getLightsList().forEach(light -> {
                        light.animate(t);
                    });

                }));

        mainAnimationTimeline.setCycleCount(Timeline.INDEFINITE);

        mainAnimationTimeline.play();
    }
}
