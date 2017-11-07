/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import gk1.textures.CachedImage;
import gk1.textures.Texture;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Kazimierz
 */
public class Model {

    private ArrayList<Polygon> polygons = new ArrayList<>();
    private ArrayList<LightSource> lights = new ArrayList<>();
    private double zCounter = 0;
    public Polygon activePolygon;

    public double getNextZ() {
        return zCounter++;
    }

    public void draw(Viewer viewer) {
        polygons.forEach(polygon -> {
            polygon.draw(viewer, this);
        });

        lights.forEach(light -> {
            light.draw(viewer, this);
        });
    }

    public ArrayList<MenuItem> buildContextMenu(MouseEvent event) {
        // gather all MenuItems from all objects in an organized manner

        ArrayList<Polygon> touchedConvexPolygons = new ArrayList<>();
        ArrayList<Polygon> touchedNonConvexPolygons = new ArrayList<>();
        ArrayList<MenuItem> mainMenuItems = new ArrayList<>();

        polygons.forEach((polygon) -> {
            ArrayList<MenuItem> subMenuItems = polygon.buildContextMenu(event);
            if (subMenuItems.size() > 0) {
                Menu subMenu = new Menu(polygon.toString());
                subMenu.getItems().addAll(subMenuItems);
                mainMenuItems.add(subMenu);
            }

            if (polygon.hasTouched(event)) {
                if (polygon.isConvex()) {
                    touchedConvexPolygons.add(polygon);
                } else {
                    touchedNonConvexPolygons.add(polygon);
                }
            }
        });

        if (touchedConvexPolygons.size() >= 2
                || (!touchedConvexPolygons.isEmpty() && !touchedNonConvexPolygons.isEmpty())) {

            // provide all possible options for clipping the polygons
        }

        if (mainMenuItems.isEmpty()) {
            MenuItem menuItem = new MenuItem(String.format("Add a new polygon"));
            menuItem.setOnAction((ActionEvent e) -> {
                double x = event.getX();
                double y = event.getY();

                TextInputDialog dialog = new TextInputDialog();
                dialog.setHeaderText("Please provide a name for the polygon");
                dialog.setTitle("Polygon name");
                dialog.showAndWait();
                String result = dialog.getResult();
                if (result == null || result.isEmpty()) {
                    result = "George";
                }
                Polygon newPolygon = new Polygon(
                        result,
                        false,
                        Arrays.asList(
                                new Vertex(x, y, true),
                                new Vertex(x + 200, y + 50),
                                new Vertex(x + 50, y + 200))
                );
                registerPolygon(newPolygon);
                GK1.model.draw(GK1.viewer);
            });
            mainMenuItems.add(menuItem);
        }

        return mainMenuItems;
    }

    public Reaction mouseMoved(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        polygons.forEach((drawable) -> {
            mergedReaction.Merge(drawable.mouseMoved(mouseEvent));
        });
        return mergedReaction;
    }

    // check which one has interacted and mark as active, then modify the options
    public Reaction mousePressed(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        boolean activatedPolygon = false;
        for (Polygon polygon : polygons) {

            Reaction reaction = polygon.mousePressed(mouseEvent);
            if (!activatedPolygon && reaction.hasTouched) {
                markPolygonAsActive(polygon);
                activatedPolygon = true;
            }
            mergedReaction.Merge(reaction);
        }
        return mergedReaction;
    }

    public Reaction mouseReleased(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        polygons.forEach((polygon) -> {
            mergedReaction.Merge(polygon.mouseReleased(mouseEvent));
        });
        return mergedReaction;
    }

    public void registerPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public void unregisterPolygon(Polygon polygon) {
        if (activePolygon == polygon) {
            ((TitledPane) GK1.accessScene.lookup("#polygonTile")).setDisable(true);
            activePolygon = null;
        }
        polygons.remove(polygon);
    }

    public void registerLight(LightSource light) {
        lights.add(light);
    }

    public void unregisterLight(LightSource light) {
        lights.remove(light);
    }

    public ArrayList<LightSource> getLightsList() {
        return lights;
    }

    public void markPolygonAsActive(Polygon polygon) {
        activePolygon = polygon;
        ((TitledPane) GK1.accessScene.lookup("#polygonTile")).setDisable(false);
        ((ToggleButton) GK1.accessScene.lookup("#automaticRelations")).setSelected(
                polygon.getAutomaticRelations()
        );
        Texture polygonTexture = polygon.getTexture();

        // texture settings
        CachedImage background = polygonTexture.getBackground();
        if (background.getType() == CachedImage.textureType.color) {

            ((RadioButton) GK1.accessScene.lookup("#backgroundConstant")).setSelected(
                    true
            );
            ((ColorPicker) GK1.accessScene.lookup("#backgroundColor")).setValue(background.getColor());
        } else if (background.getType() == CachedImage.textureType.urlStream) {

            ((RadioButton) GK1.accessScene.lookup("#backgroundImage")).setSelected(
                    true
            );
            ((TextField) GK1.accessScene.lookup("#backgroundURL")).setText(background.getUrl());
        }

        // normal settings
        CachedImage normals = polygonTexture.getNormals();
        if (normals.getType() == CachedImage.textureType.color) {

            ((RadioButton) GK1.accessScene.lookup("#normalConstant")).setSelected(
                    true
            );
        } else if (normals.getType() == CachedImage.textureType.urlStream) {

            ((RadioButton) GK1.accessScene.lookup("#normalImage")).setSelected(
                    true
            );
            ((TextField) GK1.accessScene.lookup("#normalURL")).setText(normals.getUrl());
        }

        // displacement settings
        CachedImage displacement = polygonTexture.getHeights();
        if (displacement.getType() == CachedImage.textureType.color) {

            ((RadioButton) GK1.accessScene.lookup("#displacementConstant")).setSelected(
                    true
            );
        } else if (displacement.getType() == CachedImage.textureType.urlStream) {

            ((RadioButton) GK1.accessScene.lookup("#displacementImage")).setSelected(
                    true
            );
            ((TextField) GK1.accessScene.lookup("#displacementURL")).setText(displacement.getUrl());
        }
    }

}
