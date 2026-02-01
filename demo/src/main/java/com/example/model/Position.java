package com.example.model;

public class Position {

    /* Constants */
    public static final int IMPULSION = 5;
    public static final int HAUTEUR_OVALE = 100;
    public static final int HAUTEUR_MAX = 400; // Window height equivalent
    public static final int HAUTEUR_MIN = 0;
    public static final int BEFORE = 50;
    public static final int AFTER = 200;

    private int vitesse = 0;
    
    /* Initial Position */
    private static final int Y_START = 20;

    /* la position de l'ovale */
    private int position_ovale = Y_START;

    /* l'avancement de l'ovale */
    private int avancement = 0;

    /* getter sur l'avancement */
    public int getAvancement() {
        return avancement;
    }

    /* fait avancer l'ovale */
    public void avancer() {
        avancement++;
    }

    /* getter sur la position */
    public int getPosition() {
        return position_ovale;
    }

    /* setter sur la position : sauter de IMPULSION pixels */
    public void jump() {
        vitesse = IMPULSION;
        
    }

    /* descendre : diminue la position_ovale de 1 (sans passer sous la fenetre) */
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
