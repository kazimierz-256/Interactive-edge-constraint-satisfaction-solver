/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Kazimierz
 */
public class Model {

    private ArrayList<Polygon> polygons = new ArrayList<>();
    private double zCounter = 0;
    private ArrayList<LightSource> lights = new ArrayList<>();
    //private Drawable activeDrawable;

    public double getNextZ() {
        return zCounter++;
    }

    public void draw(Viewer viewer) {

        polygons.forEach((polygon) -> {
            polygon.draw(viewer, this);
        });

        lights.forEach((light) -> {
            light.draw(viewer, this);
        });
    }

    public ArrayList<MenuItem> buildContextMenu(MouseEvent event) {
        // gather all MenuItems from all objects in an organized manner

        ArrayList<MenuItem> mainMenuItems = new ArrayList<>();

        polygons.forEach((drawable) -> {
            ArrayList<MenuItem> subMenuItems = drawable.buildContextMenu(event);
            if (subMenuItems.size() > 0) {
                Menu subMenu = new Menu(drawable.toString());
                subMenu.getItems().addAll(subMenuItems);
                mainMenuItems.add(subMenu);
            }
        });

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
                        ((CheckBox) GK1.accessScene.lookup("#automaticRelations")).isSelected(),
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

    public Reaction mousePressed(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        polygons.forEach((drawable) -> {
            mergedReaction.Merge(drawable.mousePressed(mouseEvent));
        });
        return mergedReaction;
    }

    public Reaction mouseReleased(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        polygons.forEach((drawable) -> {
            mergedReaction.Merge(drawable.mouseReleased(mouseEvent));
        });
        return mergedReaction;
    }

    public Reaction toggleAutomaticRelations(Boolean isAutomatic) {
        Reaction mergedReaction = new Reaction();
        polygons.forEach((drawable) -> {
            mergedReaction.Merge(drawable.toggleAutomaticRelations(isAutomatic));
        });
        return mergedReaction;
    }

    public void registerPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public void unregisterPolygon(Polygon polygon) {
        polygons.remove(polygon);
    }

    public void registerLight(LightSource light) {
        lights.add(light);
    }

    public void unregisterLight(LightSource light) {
        lights.remove(light);
    }

    public ArrayList<LightSource> getLights() {
        return lights;
    }

}
