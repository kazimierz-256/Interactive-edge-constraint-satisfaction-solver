/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import animation.ColorAnimator;
import animation.PositionAnimator;
import java.util.ArrayList;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author kazimierz
 */
public class LightSource implements Drawable {

    private Vertex position;
    private int light = 0xff_ff_ff_ff;
    private double intensity = 16d;
    private PositionAnimator positionAnimator;
    private ColorAnimator colorAnimator;

    LightSource(Vertex vertex, int i, double d, PositionAnimator positionAnimator) {
        this.position = position;
        this.light = light;
        this.intensity = intensity;
        this.positionAnimator = positionAnimator;
    }

    LightSource(Vertex vertex, int i, double d, PositionAnimator positionAnimator, ColorAnimator colorAnimator) {
        this.position = position;
        this.light = light;
        this.intensity = intensity;
        this.positionAnimator = positionAnimator;
        this.colorAnimator = colorAnimator;
    }

    @Override
    public double getZ() {
        return getPosition().getZ();
    }

    public LightSource(Vertex position, int light) {
        this.position = position;
        this.light = light;
    }

    public LightSource(Vertex position, int light, double intensity) {
        this.position = position;
        this.light = light;
        this.intensity = intensity;
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

    public Vertex getPosition() {
        return position;
    }

    public void setPosition(Vertex position) {
        this.position = position;
    }

    public int getLightColor() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    @Override
    public void draw(Viewer viewer, Model context) {
        // just draw a normal single vertex
        viewer.draw(this);
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getIntensity() {
        return intensity;
    }

    void animate(double t) {
        if (getColorAnimator() != null) {
            light = getColorAnimator().animate(t);
        }
        if (getPositionAnimator() != null) {
            position = getPositionAnimator().animate(t);
        }
    }

    public PositionAnimator getPositionAnimator() {
        return positionAnimator;
    }

    public void setPositionAnimator(PositionAnimator positionAnimator) {
        this.positionAnimator = positionAnimator;
    }

    public ColorAnimator getColorAnimator() {
        return colorAnimator;
    }

    public void setColorAnimator(ColorAnimator colorAnimator) {
        this.colorAnimator = colorAnimator;
    }

}
