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
public class OscillatoryColourLights implements ColorAnimator {

    private final Random random;
    private final double pace;
    private final double offset;

    public OscillatoryColourLights(int seed) {
        this.random = new Random(seed);
        pace = 1 + .7d * random.nextDouble();
        offset = 10 * random.nextDouble();
    }

    @Override
    public int animate(double t) {
        t = transform(t);
        t %= 2;
        t = Math.abs(t);

        if (t >= 1.9 || (t >= .9 && t < 1)) {
            return 0;
        }

        if ((4 * t) % 2 >= 1) {
            return t > 1 ? 0xff_aa_22_22 : 0xff_22_22_aa;
        }

        return t > 1 ? 0xff_ff_66_66 : 0xff_66_66_ff;
    }

    private double transform(double t) {
        t += offset;
        t *= pace;
        return t;
    }

}
