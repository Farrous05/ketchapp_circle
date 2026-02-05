package com.example.view;

/**
 * Thread responsable de l'animation lors d'une collision.
 * Gère les frames d'animation et notifie l'affichage pour créer
 * un effet de secousse (shake) lorsque l'ovale touche la ligne.
 * 
 * Caractéristiques :
 * - Animation de 6 frames à 50ms chacune (~300ms total)
 * - Cooldown de 1 seconde entre deux animations
 * - Non-bloquant : utilise un thread séparé
 */
public class AnimationCollision extends Thread {

    /** Délai entre chaque frame d'animation (ms) - plus lent */
    public static final int DELAY_ANIMATION = 50;
    
    /** Nombre total de frames dans l'animation */
    public static final int NOMBRE_FRAMES = 6;
    
    /** Cooldown après animation (ms) - empêche animations consécutives */
    public static final int COOLDOWN = 1000;

    /** Référence à l'affichage */
    private Affichage affichage;
    
    /** Indique si une animation est en cours */
    private boolean enAnimation = false;
    
    /** Frame actuelle de l'animation */
    private int frameActuelle = 0;
    
    /** Timestamp de la dernière animation pour cooldown */
    private long dernierHit = 0;

    /**
     * Constructeur du thread d'animation.
     * @param a référence à l'affichage pour notifier les changements de frame
     */
    public AnimationCollision(Affichage a) {
        this.affichage = a;
    }

    /**
     * Démarre une nouvelle animation de collision.
     * Vérifie le cooldown pour éviter les animations consécutives,
     * préparant le terrain pour un futur système de vies.
     */
    public void demarrerAnimation() {
        long maintenant = System.currentTimeMillis();
        
        // Ne pas redémarrer si déjà en animation OU si cooldown actif
        if (!enAnimation && (maintenant - dernierHit) > COOLDOWN) {
            enAnimation = true;
            frameActuelle = 0;
            dernierHit = maintenant;
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
                affichage.setAnimationFrame(frameActuelle);
                frameActuelle++;
                
                if (frameActuelle > NOMBRE_FRAMES) {
                    enAnimation = false;
                    affichage.setAnimationFrame(-1);  // Fin de l'animation
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
