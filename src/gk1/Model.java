/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import java.util.ArrayList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Kazimierz
 */
public class Model implements Drawable {

    private ArrayList<Drawable> drawables = new ArrayList<>();

    //private Drawable activeDrawable;
    public void registerDrawable(Drawable drawable) {
        drawables.add(drawable);
    }

    public void unregisterDrawable(Drawable drawable) {
        drawables.remove(drawable);
    }

    @Override
    public void draw(Viewer viewer) {
        // clear the canvas
        viewer.clear();

        // draw each subdrawable
        drawables.forEach((drawable) -> {
            drawable.draw(viewer);
        });
    }

    @Override
    public ArrayList<MenuItem> buildMenu(MouseEvent event) {
        // gather all MenuItems from all objects in an organized manner

        ArrayList<MenuItem> mainMenuItems = new ArrayList<>();

        drawables.forEach((d) -> {
            ArrayList<MenuItem> subMenuItems = d.buildMenu(event);
            if (subMenuItems.size() > 0) {
                Menu subMenu = new Menu(d.toString());
                subMenu.getItems().addAll(subMenuItems);
                mainMenuItems.add(subMenu);
            }
        });

        return mainMenuItems;
    }

    @Override
    public Reaction mouseMoved(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        drawables.forEach((drawable) -> {
            mergedReaction.Merge(drawable.mouseMoved(mouseEvent));
        });
        return mergedReaction;
    }

    @Override
    public Reaction mousePressed(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        drawables.forEach((drawable) -> {
            mergedReaction.Merge(drawable.mousePressed(mouseEvent));
        });
        return mergedReaction;
    }

    @Override
    public Reaction mouseReleased(MouseEvent mouseEvent) {
        Reaction mergedReaction = new Reaction();
        drawables.forEach((drawable) -> {
            mergedReaction.Merge(drawable.mouseReleased(mouseEvent));
        });
        return mergedReaction;
    }

}
