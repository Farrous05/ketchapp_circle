package com.example.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.example.util.SoundManager;
import com.example.view.AnimationPomme;

/**
 * Modèle représentant le terrain du jeu sous forme de ligne brisée infinie.
 * Gère une liste de points (x, y) qui défilent de droite à gauche.
 * Assure la génération procédurale de nouveaux points pour maintenir
 * le terrain devant le joueur.
 */
public class Parcours {

    /* Constante générateur aléatoire */
    private static final Random RAND = new Random();

    /* Constantes X_MIN et X_MAX définissant l'écart minimum et maximum en X */
    private static final int X_MIN = 50;
    private static final int X_MAX = 150;

    /* Liste des points de la ligne brisée */
    private final ArrayList<Point> points;

    /* Liste des pommes (coordonnées absolues) */
    private final ArrayList<Point> pommes = new ArrayList<>();

    /* Probabilité de spawn d'une pomme par segment */
    private static final double POMME_SPAWN_CHANCE = 0.30;

    /* Offset vertical min/max par rapport à la ligne */
    private static final int POMME_OFFSET_MIN = 20;
    private static final int POMME_OFFSET_MAX = 40;

    /* Le modèle : la position */
    private final Position position;

    /* Référence au thread d'animation pomme */
    private AnimationPomme animationPomme;

    public Parcours(Position p) {
        this.position = p;
        points = new ArrayList<>();
        genererParcours();
    }

    /** Définit le thread d'animation pour les captures de pomme */
    public void setAnimationPomme(AnimationPomme anim) {
        this.animationPomme = anim;
    }

    /* 
     * Initialise la liste points avec 2 points initiaux.
     * Le reste sera généré dynamiquement par update().
     */
    private void genererParcours() {
        // On commence avant l'horizon (par exemple à -BEFORE)
        int currentX = -Position.BEFORE;
        // Commence au milieu de l'ovale pour éviter une collision immédiate
        int currentY = Position.Y_START + Position.HAUTEUR_OVALE / 2;

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
        int safeMin = Position.HAUTEUR_MIN + 100;
        int nextY = safeMin + RAND.nextInt(safeMax - safeMin);
        
        points.add(new Point(nextX, nextY));

        // Génération aléatoire d'une pomme sur ce segment
        if (RAND.nextDouble() < POMME_SPAWN_CHANCE) {
            Point prevPoint = points.get(points.size() - 2);
            int appleX = (prevPoint.x + nextX) / 2;

            // Interpoler le Y de la ligne au milieu du segment
            double t = (double) (appleX - prevPoint.x) / (nextX - prevPoint.x);
            int lineY = (int) (prevPoint.y + t * (nextY - prevPoint.y));

            // Offset aléatoire au-dessus ou en dessous de la ligne
            int offsetMag = POMME_OFFSET_MIN + RAND.nextInt(POMME_OFFSET_MAX - POMME_OFFSET_MIN);
            int offset = RAND.nextBoolean() ? offsetMag : -offsetMag;
            int appleY = Math.max(Position.HAUTEUR_MIN + 10,
                    Math.min(lineY + offset, Position.HAUTEUR_MAX - 10));

            pommes.add(new Point(appleX, appleY));
        }
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

        // Suppression des pommes hors écran
        pommes.removeIf(pomme -> (pomme.x - position.getAvancement()) < -Position.BEFORE - 50);

        // Ajout de nouveaux points
        Point lastPoint = points.get(points.size() - 1);
        // Si le dernier point est proche de l'horizon (x - avancement < AFTER + MARGE)
        // Si le dernier point est proche de l'horizon (x - avancement < AFTER + 200)
        if ((lastPoint.x - position.getAvancement()) < Position.AFTER + 200) {
            ajouterPoint();
        }
    }

    /**
     * Détecte une collision entre l'ovale et la ligne brisée.
     * L'ovale est situé à X=0 (coordonnées décalées).
     * Utilise la formule de la pente: Y_ligne = Y1 + (X - X1) * pente
     * Collision uniquement quand la ligne touche le BORD de l'ovale.
     * 
     * @param ovalY position Y du bas de l'ovale
     * @param ovalHeight hauteur de l'ovale
     * @return true si collision détectée
     */
    public boolean checkCollision(int ovalY, int ovalHeight) {
        int ovalX = 0;  // L'ovale est toujours en X=0 en coordonnées décalées
        int ovalTop = ovalY + ovalHeight;
        
        // Marge de tolérance pour détecter le contact avec le bord
        int TOLERANCE = 5;
        
        ArrayList<Point> shiftedPoints = getPoints();
        
        for (int i = 0; i < shiftedPoints.size() - 1; i++) {
            Point p1 = shiftedPoints.get(i);
            Point p2 = shiftedPoints.get(i + 1);
            
            // Vérifier si l'ovale est dans ce segment
            if (p1.x <= ovalX && ovalX <= p2.x) {
                // Éviter division par zéro (segment vertical)
                if (p2.x == p1.x) {
                    continue;
                }
                
                // Calcul de la pente et du Y de la ligne à X=0
                double pente = (double)(p2.y - p1.y) / (p2.x - p1.x);
                double yLigne = p1.y + (ovalX - p1.x) * pente;
                
                // Collision UNIQUEMENT si la ligne touche le BORD de l'ovale
                // Proche du bas (ovalY) OU proche du haut (ovalTop)
                boolean toucheBasOvale = Math.abs(yLigne - ovalY) <= TOLERANCE;
                boolean toucheHautOvale = Math.abs(yLigne - ovalTop) <= TOLERANCE;
                
                if (toucheBasOvale || toucheHautOvale) {
                    SoundManager.getInstance().playHit();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si l'ovale capture une pomme.
     * Capture = la pomme est à l'intérieur du corps de l'ovale.
     *
     * @param ovalY position Y du bas de l'ovale
     * @param ovalHeight hauteur de l'ovale
     * @return true si une pomme a été capturée
     */
    public boolean checkAppleCapture(int ovalY, int ovalHeight) {
        int ovalX = 0;
        int shift = position.getAvancement();

        // Marge pour que le centre de la pomme soit bien à l'intérieur de l'ovale
        // (correspond au demi-rayon visuel de la pomme en coordonnées modèle)
        int margin = 15;

        Iterator<Point> it = pommes.iterator();
        while (it.hasNext()) {
            Point pomme = it.next();
            int shiftedX = pomme.x - shift;

            // Pomme horizontalement proche de l'ovale
            if (Math.abs(shiftedX - ovalX) <= 25) {
                // Pomme verticalement bien à l'intérieur de l'ovale (pas juste le bord)
                if (pomme.y >= ovalY + margin && pomme.y <= ovalY + ovalHeight - margin) {
                    int animX = shiftedX;
                    int animY = pomme.y;
                    it.remove();

                    if (animationPomme != null) {
                        animationPomme.demarrerAnimation(animX, animY);
                    }
                    SoundManager.getInstance().playEat();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retourne la liste des pommes en coordonnées décalées.
     */
    public ArrayList<Point> getPommes() {
        ArrayList<Point> shiftedPommes = new ArrayList<>();
        int shift = position.getAvancement();
        for (Point p : pommes) {
            shiftedPommes.add(new Point(p.x - shift, p.y));
        }
        return shiftedPommes;
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
