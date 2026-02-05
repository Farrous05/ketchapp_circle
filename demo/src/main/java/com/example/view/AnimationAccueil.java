package com.example.view;

import java.util.Random;
import javax.swing.JPanel;

/**
 * Thread responsable de l'animation sur l'écran d'accueil.
 * Anime des pommes flottantes avec un mouvement sinusoïdal
 * pour donner vie à l'écran de bienvenue.
 *
 * Chaque pomme défile de droite à gauche avec une oscillation verticale.
 */
public class AnimationAccueil extends Thread {

    /** Délai entre chaque frame (ms) */
    public static final int DELAY = 40;

    /** Nombre de pommes flottantes */
    public static final int NB_POMMES = 5;

    /** Amplitude de l'oscillation verticale */
    private static final double AMPLITUDE = 20.0;

    /** Référence au panneau à redessiner */
    private JPanel panel;

    /** Flag pour arrêter le thread proprement */
    private volatile boolean running = true;

    /** Compteur de ticks pour le calcul sinusoïdal */
    private int tick = 0;

    /** Positions X des pommes */
    private double[] xPositions;

    /** Positions Y de base des pommes */
    private double[] baseY;

    /** Phases de chaque pomme (décalage sinusoïdal) */
    private double[] phases;

    /** Vitesses horizontales */
    private double[] speeds;

    /** Largeur et hauteur de l'écran */
    private int screenWidth;
    private int screenHeight;

    private static final Random RAND = new Random();

    /**
     * Constructeur.
     * @param panel le panneau à redessiner (EcranAccueil)
     * @param width largeur de l'écran
     * @param height hauteur de l'écran
     */
    public AnimationAccueil(JPanel panel, int width, int height) {
        this.panel = panel;
        this.screenWidth = width;
        this.screenHeight = height;

        xPositions = new double[NB_POMMES];
        baseY = new double[NB_POMMES];
        phases = new double[NB_POMMES];
        speeds = new double[NB_POMMES];

        for (int i = 0; i < NB_POMMES; i++) {
            xPositions[i] = RAND.nextInt(width);
            baseY[i] = 80 + RAND.nextInt(height - 160);
            phases[i] = RAND.nextDouble() * Math.PI * 2;
            speeds[i] = 0.5 + RAND.nextDouble() * 1.0;
        }
    }

    /** Arrête le thread proprement */
    public void arreter() {
        running = false;
    }

    /** @return les positions X courantes */
    public double[] getXPositions() {
        return xPositions;
    }

    /** Calcule les positions Y courantes (baseY + oscillation sinusoïdale) */
    public double[] getYPositions() {
        double[] yPositions = new double[NB_POMMES];
        for (int i = 0; i < NB_POMMES; i++) {
            yPositions[i] = baseY[i] + Math.sin(phases[i] + tick * 0.05) * AMPLITUDE;
        }
        return yPositions;
    }

    @Override
    public void run() {
        while (running) {
            tick++;

            for (int i = 0; i < NB_POMMES; i++) {
                xPositions[i] -= speeds[i];

                // Respawn à droite si sorti à gauche
                if (xPositions[i] < -40) {
                    xPositions[i] = screenWidth + RAND.nextInt(100);
                    baseY[i] = 80 + RAND.nextInt(screenHeight - 160);
                    phases[i] = RAND.nextDouble() * Math.PI * 2;
                    speeds[i] = 0.5 + RAND.nextDouble() * 1.0;
                }
            }

            panel.repaint();

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
