/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package animation;

import gk1.Vertex;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import java.util.Random;

/**
 *
 * @author kazimierz
 */
public class Helicopter implements PositionAnimator {

    private final Random random;
    private final double primaryRadius;
    private final double secondaryRadius;
    private final double primaryHeight;
    private final double secondaryHeight;
    private final double initialX;
    private final double initialY;
    private final double pace;
    private final double offset;

    public Helicopter(int seed) {
        this.random = new Random(seed);
        primaryRadius = 100 + 100 * random.nextDouble();
        secondaryRadius = 30 + 20 * random.nextDouble();
        primaryHeight = 100 + 90 * random.nextDouble();
        secondaryHeight = 5 + 5 * random.nextDouble();
        initialX = 200 + 10 * random.nextDouble();
        initialY = 200 + 10 * random.nextDouble();
        pace = .3d * random.nextDouble();
        offset = 10 * random.nextDouble();
    }

    @Override
    public Vertex animate(double t) {
        t = transform(t);
        double radius = primaryRadius + secondaryRadius * cos(sin(t) + 10 * sin(2 * t));
        double phase = -t / 2 + (sin(t) * sqrt(t));
        double z = primaryHeight + secondaryHeight * sin(10 * sin(t));
        return new Vertex(
                initialX + radius * cos(phase),
                initialY + radius * sin(phase),
                z
        );
    }

    private double transform(double t) {
        t += offset;
        t *= pace;
        return t;
    }

}
