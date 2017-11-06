/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package animation;

import java.util.Random;

/**
 *
 * @author kazimierz
 */
public class PoliceHeadlights implements ColorAnimator {

    private final Random random;
    private final double pace;
    private final double offset;

    public PoliceHeadlights(int seed) {
        this.random = new Random(seed);
        pace = 1 + .2d * random.nextDouble();
        offset = 10 * random.nextDouble();
    }

    @Override
    public int animate(double t) {
        t = transform(t);
        if (Math.abs(10 * t % 10) > 5) {
            return 0xff_ff_66_66;
        } else {
            return 0xff_66_66_ff;
        }
    }

    private double transform(double t) {
        t += offset;
        t *= pace;
        return t;
    }

}
