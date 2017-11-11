/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import animation.ColorAnimator;
import animation.PositionAnimator;

/**
 *
 * @author kazimierz
 */
public class LightSource {

    private Vertex position;
    private int light;
    private double intensity;
    private PositionAnimator positionAnimator;
    private ColorAnimator colorAnimator;

    LightSource(Vertex position, int light, double intensity, PositionAnimator positionAnimator) {
        this.position = position;
        this.light = light;
        this.intensity = intensity;
        this.positionAnimator = positionAnimator;
    }

    LightSource(Vertex position, int light, double intensity, PositionAnimator positionAnimator, ColorAnimator colorAnimator) {
        this.position = position;
        this.light = light;
        this.intensity = intensity;
        this.positionAnimator = positionAnimator;
        this.colorAnimator = colorAnimator;
    }

    public void setZ(double z) {
        position.setZ(z);
    }

    public double getZ() {
        return position.getZ();
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
        if (colorAnimator != null) {
            light = colorAnimator.animate(t);
        }
        if (positionAnimator != null) {
            position = positionAnimator.animate(t);
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
