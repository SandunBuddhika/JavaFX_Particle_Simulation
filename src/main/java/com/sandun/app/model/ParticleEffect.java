package com.sandun.app.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleEffect {

    private final List<Particle> PARTICLES = new ArrayList<>();
    private final Random RANDOM = new Random();
    private double width;
    private double height;
    private int numParticles = 200;
    private int particleRadius = 10;
    private int maxDistance = 80;
    private int speed = 20;
    private double lineWidth = 1;
    private boolean running;
    private boolean isConnectionOn;
    private boolean randomRadius;
    private boolean randomColorState;
    private boolean connectionRandomColor;
    private Color particleColor = Color.WHITE;
    private Color connectionColor = Color.WHITE;
    private Color backgroundColor = Color.BLACK;
    private GraphicsContext gc;
    private final BoxBlur blurEffect;
    private final Glow glowEffect;
    private final Color[] RANDOM_COLORS = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.INDIGO, Color.VIOLET, Color.PINK, Color.CYAN, Color.MAGENTA};
    private Thread randomColorThread;

    public ParticleEffect(int width, int height, GraphicsContext gc) {
        this.width = width;
        this.height = height;
        this.gc = gc;
        this.running = true;
        isConnectionOn = true;
        this.blurEffect = new BoxBlur();
        blurEffect.setWidth(10);
        blurEffect.setHeight(10);
        blurEffect.setIterations(1);

        this.glowEffect = new Glow();
        glowEffect.setLevel(1.0);
    }

    public void initializeParticles() {
        if (PARTICLES.size() > numParticles) {
            for (int i = PARTICLES.size() - 1; i >= numParticles - 1; i--) {
                PARTICLES.remove(i);
            }
        } else {
            for (int i = PARTICLES.size() - 1; i < numParticles; i++) {
                double radius = 3 + (RANDOM.nextDouble() * (20 - 3));
                double newRadius = particleRadius;
                if (randomRadius) {
                    newRadius = radius;
                }
                double x = RANDOM.nextDouble() * (width - newRadius);
                double y = RANDOM.nextDouble() * (height - newRadius);
                double vx = RANDOM.nextDouble() * 4 - 2;
                double vy = RANDOM.nextDouble() * 4 - 2;
                PARTICLES.add(new Particle(x, y, vx, vy, radius));
            }
        }
    }

    public void update() {
        for (Particle particle : PARTICLES) {
            particle.setX(particle.getX() + particle.getVx());
            particle.setY(particle.getY() + particle.getVy());

            double radius = particleRadius;
            if (randomRadius) {
                radius = particle.getRadius();
            }
            if (particle.getX() >= width - radius || particle.getX() < 0) {
                particle.setVx((particle.getVx() * -1));
            }
            if (particle.getY() >= height - radius || particle.getY() < 0) {
                particle.setVy((particle.getVy() * -1));
            }
        }
    }

    public void render() {
        gc.clearRect(0, 0, width, height);
        gc.setFill(backgroundColor);
//        gc.fillRect(0, 0, width, height);

        double arcSize = 18;
        gc.beginPath();
        gc.moveTo(arcSize, 0);
        gc.arcTo(0, 0, 0, arcSize, arcSize);
        gc.lineTo(0, height - arcSize);
        gc.arcTo(0, height, arcSize, height, arcSize);
        gc.lineTo(width, height);
//        gc.arcTo(width, height, width, height - arcSize, arcSize);
        gc.lineTo(width, 0);
//        gc.arcTo(width, 0, width - arcSize, 0, arcSize);
        gc.closePath();
        gc.fill();


        if (isConnectionOn) {
            renderConnections();
        }
        gc.setFill(particleColor);
        for (Particle particle : PARTICLES) {
            double radius = particleRadius;
            if (randomRadius) {
                radius = particle.getRadius();
            }
            gc.fillOval(particle.getX(), particle.getY(), radius, radius);
        }
    }

    private void renderConnections() {
        gc.setStroke(connectionColor);
        gc.setLineWidth(lineWidth);
        for (int i = 0; i < PARTICLES.size(); i++) {
            Particle particle = PARTICLES.get(i);
            for (int j = i; j < PARTICLES.size(); j++) {
                Particle particle1 = PARTICLES.get(j);
                double dx = particle1.getX() - particle.getX();
                double dy = particle1.getY() - particle.getY();
                double distance = Math.hypot(dx, dy);
                if (distance < maxDistance) {
                    double opacity = 1 - (distance / maxDistance);
                    gc.setGlobalAlpha(opacity);
                    double radius = particleRadius / 2;
                    double radius2 = radius;
                    if (randomRadius) {
                        radius = particle.getRadius() / 2.0;
                        radius2 = particle1.getRadius() / 2.0;
                    }
                    gc.strokeLine(particle.getX() + radius, particle.getY() + radius, particle1.getX() + radius2, particle1.getY() + radius2);
                }
            }
        }
    }

    public void manageBlueEffectState(boolean state) {
        if (state) {
            gc.setEffect(blurEffect);
        } else {
            gc.setEffect(null);
        }
    }

    public void manageGlowEffectState(boolean state) {
        if (state) {
            gc.setEffect(glowEffect);
        } else {
            gc.setEffect(null);
        }
    }

    public void manageRandomColorState(boolean state) {
        randomColorState = state;
        double increment = (double) speed / 100 / 3;
        ParticleEffect effect = this;
        if (state) {
            Runnable runnable = () -> {
                try {
                    while (randomColorState) {
                        Color currentParticleColor = particleColor;
                        Color newParticleColor = RANDOM_COLORS[RANDOM.nextInt(RANDOM_COLORS.length)];

                        Color currentConnectionColor = null;
                        Color newConnectionColor = null;
                        if (connectionRandomColor) {
                            currentConnectionColor = connectionColor;
                            newConnectionColor = RANDOM_COLORS[RANDOM.nextInt(RANDOM_COLORS.length)];
                        }

                        for (double t = 0.0; t < 1.0; t += increment) {
                            Color interpolatedParticleColor = interpolateColor(currentParticleColor, newParticleColor, t);
                            Color interpolatedConnectionColor;

                            if (connectionRandomColor) {
                                interpolatedConnectionColor = interpolateColor(currentConnectionColor, newConnectionColor, t);
                            } else {
                                interpolatedConnectionColor = interpolatedParticleColor;
                            }

                            synchronized (effect) {
                                particleColor = interpolatedParticleColor;
                                connectionColor = interpolatedConnectionColor;
                            }

                            Thread.sleep(speed);
                        }

                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                }
            };
            randomColorThread = new Thread(runnable);
            randomColorThread.start();
        } else {
            if (randomColorThread != null) {
                randomColorThread.interrupt();
            }
        }
    }

    public void manageRandomConnectionColorState(boolean state) {
        connectionRandomColor = state;
    }

    private Color interpolateColor(Color start, Color end, double t) {
        double red = (1 - t) * start.getRed() + t * end.getRed();
        double green = (1 - t) * start.getGreen() + t * end.getGreen();
        double blue = (1 - t) * start.getBlue() + t * end.getBlue();
        return new Color(red, green, blue, 1);
    }

    public void changeProgramState() {
        running = !running;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getNumParticles() {
        return numParticles;
    }

    public int getParticleRadius() {
        return particleRadius;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    //need to add new particle to array
    public void setNumParticles(int numParticles) {
        this.numParticles = numParticles;
    }

    public void setParticleRadius(int particleRadius) {
        this.particleRadius = particleRadius;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isRandomRadius() {
        return randomRadius;
    }

    public void setRandomRadius(boolean randomRadius) {
        this.randomRadius = randomRadius;
    }

    public Color getParticleColor() {
        return particleColor;
    }

    public void setParticleColor(Color particleColor) {
        this.particleColor = particleColor;
    }

    public Color getConnectionColor() {
        return connectionColor;
    }

    public void setConnectionColor(Color connectionColor) {
        this.connectionColor = connectionColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }

    public boolean isConnectionOn() {
        return isConnectionOn;
    }

    public void setConnectionOn(boolean connectionOn) {
        isConnectionOn = connectionOn;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
