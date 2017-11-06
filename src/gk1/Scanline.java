/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

/**
 *
 * @author kazimierz
 */
public class Scanline {

    public int from;
    public int to;
    public int localHeight;

    public Scanline(int from, int to, int localHeight) {
        this.from = from;
        this.to = to;
        this.localHeight = localHeight;
    }
}
