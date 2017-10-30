/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    private ArrayList<Drawable> drawables = new ArrayList<>();
    private double zCounter = 0;
    private Collection<LightSource> lights;

    public double getNextZ() {
        return zCounter++;
    }

    //private Drawable activeDrawable;
    public void registerDrawable(Drawable drawable) {
        drawables.add(drawable);
    }

    public void unregisterDrawable(Drawable drawable) {
        drawables.remove(drawable);
    }

    public void draw(Viewer viewer) {

        // draw each subdrawable
        drawables.forEach((drawable) -> {
            drawable.draw(viewer, this);
        });
    }

    public ArrayList<MenuItem> buildContextMenu(MouseEvent event) {
        // gather all MenuItems from all objects in an organized manner

        ArrayList<MenuItem> mainMenuItems = new ArrayList<>();

        drawables.forEach((drawable) -> {
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
                        getNextZ(),
                        ((CheckBox) GK1.accessScene.lookup("#automaticRelations")).isSelected(),
                        Arrays.asList(
                                new Vertex(x, y, true),
                                new Vertex(x + 200, y + 50),
                                new Vertex(x + 50, y + 200))
                );
                registerDrawable(newPolygon);
                GK1.model.draw(GK1.viewer);
            });
            mainMenuItems.add(menuItem);
        }

        return mainMenuItems;
    }

    public Reaction mouseMoved(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        drawables.forEach((drawable) -> {
            mergedReaction.Merge(drawable.mouseMoved(mouseEvent));
        });
        return mergedReaction;
    }

    public Reaction mousePressed(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        drawables.forEach((drawable) -> {
            mergedReaction.Merge(drawable.mousePressed(mouseEvent));
        });
        return mergedReaction;
    }

    public Reaction mouseReleased(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        drawables.forEach((drawable) -> {
            mergedReaction.Merge(drawable.mouseReleased(mouseEvent));
        });
        return mergedReaction;
    }

    public Reaction toggleAutomaticRelations(Boolean isAutomatic) {
        Reaction mergedReaction = new Reaction();
        drawables.forEach((drawable) -> {
            mergedReaction.Merge(drawable.toggleAutomaticRelations(isAutomatic));
        });
        return mergedReaction;
    }

    public Collection<LightSource> getLights() {
        return lights;
    }

    public void setLights(Collection<LightSource> lights) {
        this.lights = lights;
    }

}
