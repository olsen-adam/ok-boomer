package com.program.main;

import java.awt.*;
import java.util.LinkedList;

public class PtManager {
    LinkedList<Particle> particles = new LinkedList<Particle>();

    public void tick() {
        for (int i = 0; i < particles.size(); i++) {
            Particle ii = particles.get(i);
            ii.tick();
        }
    }

    public void render(Graphics g) {
        for (int i = 0; i < particles.size(); i++) {
            Particle ii = particles.get(i);
            ii.render(g);
        }
    }

    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    public void removeParticle(Particle particle) {
        particles.remove(particle);
    }
}
