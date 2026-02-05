package com.example.view;

/**
 * Thread responsable de l'animation lors de la capture d'une pomme.
 * Gère un effet de grossissement + fondu (scale-up + fade-out)
 * pour donner un feedback visuel de collecte.
 *
 * Pattern identique à AnimationCollision :
 * - Animation de 8 frames à 40ms chacune (~320ms total)
 * - Non-bloquant : utilise un thread séparé
 */
public class AnimationPomme extends Thread {

    /** Délai entre chaque frame d'animation (ms) */
    public static final int DELAY_ANIMATION = 40;

    /** Nombre total de frames dans l'animation */
    public static final int NOMBRE_FRAMES = 8;

    /** Référence à l'affichage */
    private Affichage affichage;

    /** Indique si une animation est en cours */
    private boolean enAnimation = false;

    /** Frame actuelle de l'animation */
    private int frameActuelle = 0;

    /**
     * Constructeur du thread d'animation.
     * @param a référence à l'affichage pour notifier les changements de frame
     */
    public AnimationPomme(Affichage a) {
        this.affichage = a;
    }

    /**
     * Démarre une nouvelle animation de capture à la position donnée.
     * @param x coordonnée X modèle (décalée) de la pomme
     * @param y coordonnée Y modèle de la pomme
     */
    public void demarrerAnimation(int x, int y) {
        if (!enAnimation) {
            enAnimation = true;
            frameActuelle = 0;
            affichage.setAppleAnimationPos(x, y);
        }
    }

    /**
     * Boucle principale du thread d'animation.
     * Met à jour les frames et notifie l'affichage.
     */
    @Override
    public void run() {
        while (true) {
            if (enAnimation) {
                affichage.setAppleAnimationFrame(frameActuelle);
                frameActuelle++;

                if (frameActuelle > NOMBRE_FRAMES) {
                    enAnimation = false;
                    affichage.setAppleAnimationFrame(-1);
                }
            }

            try {
                Thread.sleep(DELAY_ANIMATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
