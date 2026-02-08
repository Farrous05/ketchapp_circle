package com.example.model;

/**
 * Modèle central du joueur.
 * Encapsule la position verticale de l'ovale, sa vitesse, l'avancement
 * horizontal, le nombre de vies et le score.
 */
public class Position {

    /** Constantes de gameplay */
    public static final int IMPULSION = 5;
    public static final int HAUTEUR_OVALE = 100;
    public static final int HAUTEUR_MAX = 400;
    public static final int HAUTEUR_MIN = 0;
    public static final int BEFORE = 50;
    public static final int AFTER = 200;

    private int vitesse = 0;
    
    /** Position verticale initiale du joueur */
    public static final int Y_START = 20;

    /** Position verticale courante de l'ovale */
    private int position_ovale = Y_START;

    /** Avancement horizontal global (scrolling) */
    private int avancement = 0;
    
    /** Nombre de vies restantes */
    private int lives = 3;

    /** Score (nombre de pommes capturées) */
    private int score = 0;
    
    /** Retourne le nombre de vies restantes */
    public int getLives() {
        return lives;
    }
    
    /** Retire une vie (collision) */
    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }
    
    /** Vérifie si le joueur est toujours en vie */
    public boolean isAlive() {
        return lives > 0;
    }

    /** Retourne le score actuel */
    public int getScore() {
        return score;
    }

    /** Incrémente le score de 1 (pomme capturée) */
    public void incrementScore() {
        score++;
    }

    /** Retourne l'avancement horizontal global */
    public int getAvancement() {
        return avancement;
    }

    /** Incrémente l'avancement de 1 (scrolling) */
    public void avancer() {
        avancement++;
    }

    /** Retourne la position verticale de l'ovale */
    public int getPosition() {
        return position_ovale;
    }

    /** Applique une impulsion de saut (vitesse ← IMPULSION) */
    public void jump() {
        vitesse = IMPULSION;
        
    }

    /** Applique la physique : gravité (décélération) et bornes verticales */
    public void move() {
        int sol = HAUTEUR_MAX - HAUTEUR_OVALE;

        if (position_ovale < sol) {
            position_ovale = position_ovale + vitesse;
            vitesse = vitesse - 1;
        }
        // éviter de sortir par le haut
        if (position_ovale < HAUTEUR_MIN) {
            position_ovale = HAUTEUR_MIN;
        }
    }
}
