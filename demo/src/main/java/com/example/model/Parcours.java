package com.example.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Parcours {

    /* Constante générateur aléatoire */
    private static final Random RAND = new Random();

    /* Constantes X_MIN et X_MAX définissant l'écart minimum et maximum en X */
    private static final int X_MIN = 50;
    private static final int X_MAX = 150;

    /* Liste des points de la ligne brisée */
    private ArrayList<Point> points;

    /* Le modèle : la position */
    private Position position;

    public Parcours(Position p) {
        this.position = p;
        points = new ArrayList<>();
        genererParcours();
    }

    /* 
     * Initialise la liste points avec 2 points initiaux.
     * Le reste sera généré dynamiquement par update().
     */
    private void genererParcours() {
        // On commence avant l'horizon (par exemple à -BEFORE)
        int currentX = -Position.BEFORE;
        int currentY = 20; 

        points.add(new Point(currentX, currentY));

        // Deuxième point : Même Y, mais X avancé
        currentX += X_MIN + RAND.nextInt(X_MAX - X_MIN);
        points.add(new Point(currentX, currentY));
        
        // On génère quelques points d'avance pour commencer
        while (currentX <= Position.AFTER + 200) {
             ajouterPoint();
             currentX = points.get(points.size() - 1).x;
        }
    }

    /**
     * Ajoute un nouveau point à la fin de la liste
     */
    private void ajouterPoint() {
        Point lastPoint = points.get(points.size() - 1);
        
        int nextX = lastPoint.x + X_MIN + RAND.nextInt(X_MAX - X_MIN);
        
        int safeMax = Position.HAUTEUR_MAX - Position.HAUTEUR_OVALE;
        int safeMin = Position.HAUTEUR_MIN;
        int nextY = safeMin + RAND.nextInt(safeMax - safeMin);
        
        points.add(new Point(nextX, nextY));
    }

    /**
     * Met à jour la liste des points.
     * 1. Supprime les points sortis à gauche.
     * 2. Ajoute des points à droite si nécessaire.
     */
    public void update() {
        // Suppression des points obsolètes
        // On regarde si le 2ème point est déjà derrière l'écran
        if (points.size() >= 2) {
            Point p2 = points.get(1);
            // Si p2 est sorti de l'écran (x - avancement < -BEFORE)
            // On peut supprimer p1 car le segment p1-p2 n'est plus visible (ou presque)
            if ((p2.x - position.getAvancement()) < -Position.BEFORE) {
                points.remove(0);
            }
        }

        // Ajout de nouveaux points
        Point lastPoint = points.get(points.size() - 1);
        // Si le dernier point est proche de l'horizon (x - avancement < AFTER + MARGE)
        if ((lastPoint.x - position.getAvancement()) < Position.AFTER + 200) {
            ajouterPoint();
        }
    }

    /* 
     * Retourne la liste des points décalés de -Position.avancement.
     * On crée une nouvelle liste pour ne pas modifier l'originale.
     */
    public ArrayList<Point> getPoints() {
        ArrayList<Point> shiftedPoints = new ArrayList<>();
        int shift = position.getAvancement();
        for (Point p : points) {
            shiftedPoints.add(new Point(p.x - shift, p.y));
        }
        return shiftedPoints;
    }

    /* Main pour tester la génération */
    public static void main(String[] args) {
        Position pos = new Position();
        Parcours p = new Parcours(pos);
        System.out.println("Points générés (avancement=0) :");
        for (Point pt : p.getPoints()) {
            System.out.println("Point[x=" + pt.x + ", y=" + pt.y + "]");
        }
        
        pos.avancer();
        pos.avancer();
        // Le décalage devrait être visible
        System.out.println("Points générés (avancement=2) :");
        for (Point pt : p.getPoints()) {
            System.out.println("Point[x=" + pt.x + ", y=" + pt.y + "]");
        }
    }
}
