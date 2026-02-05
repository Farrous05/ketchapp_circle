package com.example.model;

import com.example.view.AnimationCollision;

/**
 * Thread responsable de la progression temporelle du jeu (boucle de jeu principale pour le terrain).
 * 
 * Effectue les tâches suivantes à intervalles réguliers (DELAY) :
 * 1. Avance la position X globale (scrolling)
 * 2. Met à jour la génération du parcours (ajout/suppression de points)
 * 3. Détecte les collisions entre le joueur et le sol
 */
public class Avancer extends Thread {

  /** Délai entre chaque avancement (ms) */
  public static final int DELAY = 50;

    /** Référence au modèle de position */
    private Position position;
    
    /** Référence au parcours (terrain) */
    private Parcours parcours;
    
    /** Référence au thread d'animation de collision */
    private AnimationCollision animationCollision;

    /** Action à exécuter en fin de partie (retour accueil) */
    private Runnable gameOverAction;

    /** Définit l'action de fin de partie (retour à l'accueil) */
    public void setGameOverAction(Runnable action) {
        this.gameOverAction = action;
    }

    /**
     * Constructeur du thread d'avancement.
     * @param p la position du joueur
     * @param parcours le terrain généré procéduralement
     */
    public Avancer(Position p, Parcours parcours) {
        this.position = p;
        this.parcours = parcours;
    }
  
  /** Définit le thread d'animation pour les collisions */
  public void setAnimationCollision(AnimationCollision anim) {
    this.animationCollision = anim;
  }

    /**
     * Boucle principale du thread.
     * Exécute en continu :
     * 1. Avancement de la position
     * 2. Mise à jour du terrain
     * 3. Détection de collision et déclenchement d'animation
     */
    @Override
    public void run() {
        int cooldown = 0; // Cooldown anti-spam collision
        
        while (position.isAlive()) {
            // Gestion du cooldown
            if (cooldown > 0) cooldown--;
            // Avance de 1 pixel (virtuel)
            position.avancer();
            
            // Met à jour la génération infinie du terrain
            parcours.update();
            
            // Vérification de collision avec la ligne brisée
            if (animationCollision != null && cooldown == 0 &&
                parcours.checkCollision(position.getPosition(), Position.HAUTEUR_OVALE)) {
                
                animationCollision.demarrerAnimation();
                position.loseLife();
                cooldown = 20; // Invulnérabilité pendant 20 frames (~1 sec)
            }

            // Vérification de capture de pomme
            if (parcours.checkAppleCapture(position.getPosition(), Position.HAUTEUR_OVALE)) {
                position.incrementScore();
            }

            try {
                sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Sauvegarder le meilleur score en fin de partie
        ScoreManager.saveBestScore(position.getScore());

        // Retour à l'écran d'accueil
        if (gameOverAction != null) {
            javax.swing.SwingUtilities.invokeLater(gameOverAction);
        }
    }
}
