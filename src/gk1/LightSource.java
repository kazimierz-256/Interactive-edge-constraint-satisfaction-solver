/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import java.util.ArrayList;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author kazimierz
 */
public class LightSource implements Drawable {

    private Vertex position;
    private int light;

    @Override
    public double getZ() {
        return getPosition().getZ();
    }

    public LightSource(Vertex position, int light) {
        this.position = position;
        this.light = light;
    }

    @Override
    public void draw(Viewer viewer) {
        // just draw a normal single vertex
        viewer.draw(getPosition());
    }

    @Override
    public Reaction mouseMoved(MouseEvent mouseEvent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Reaction mousePressed(MouseEvent mouseEvent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Reaction mouseReleased(MouseEvent mouseEvent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<MenuItem> buildContextMenu(MouseEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Reaction toggleAutomaticRelations(Boolean isAutomatic) {
        return new Reaction();
    }

    public Vertex getPosition() {
        return position;
    }

    public void setPosition(Vertex position) {
        this.position = position;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

}
