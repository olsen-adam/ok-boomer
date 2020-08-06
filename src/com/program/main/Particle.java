package com.program.main;

import java.awt.*;
import java.util.Random;

public class Particle {
    private int x, y;
    private float  rad, decayMult, decaySubt, hsp, vsp;
    private PtManager ptManager;
    private Random r = new Random();

    public Particle(int x, int y, float hsp, PtManager ptManager, float rad, float vsp) {
        this.x = x;
        this.y = y;
        this.hsp = hsp;
        this.ptManager = ptManager;

        this.rad = rad;
        this.vsp = vsp;
        decayMult = 0.95f;
        decaySubt = 0.2f;
    }

    public void tick() {
        x += hsp;
        y += vsp;
        hsp = (hsp*decayMult-decaySubt*Math.signum(hsp));
        vsp = (vsp*(decayMult-.1f)+decaySubt*0.5f);
        rad = (rad*(decayMult)-decaySubt);
        if (Math.abs(hsp) < 0.1) hsp = 0;
        if (Math.abs(vsp) < 0.1) vsp = 0;
        if (Math.abs(rad) < 1) {
            ptManager.removeParticle(this);
        }
    }

    public void render(Graphics g) {
        int irad = (int) rad;

        g.setColor(Color.white);
        g.fillOval(x-irad/2,y-irad/2,irad,irad);
    }
}