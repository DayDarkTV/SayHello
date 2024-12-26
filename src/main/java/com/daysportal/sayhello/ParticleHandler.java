package com.daysportal.sayhello;

import net.minecraft.client.particle.Particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleHandler {

    public Map<String, List<Particle>> particleMap = new HashMap<String, List<Particle>>();

    public List<Particle> getParticles(String channel) {
        return particleMap.computeIfAbsent(channel, k -> new ArrayList<Particle>());
    }
    public void addParticle(String channel, Particle particle) {
        List<Particle> particles = getParticles(channel);
        particles.add(particle);
        particleMap.put(channel, particles);
    }
}
