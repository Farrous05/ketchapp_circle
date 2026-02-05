package com.example.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.example.model.DecorManager;
import com.example.model.Parcours;
import com.example.model.Position;

/**
 * Composant graphique principal responsable du rendu de l'application.
 * 
 * Cette classe hérite de JPanel et assure :
 * - Le dessin du décor d'arrière-plan (parallaxe)
 * - Le rendu du terrain (ligne brisée)
 * - La représentation du joueur (ovale)
 * - La conversion des coordonnées physiques (modèle) vers l'écran (vue)
 */
public class Affichage extends JPanel {

    /** Dimensions de l'ovale */
    public static final int LARG_OVAL = 50;

    /** Dimensions de la fenêtre (taille fixe) */
    public static final int LARGEUR_INIT = 800;
    public static final int HAUTEUR_INIT = 400;

    /** Épaisseur de la ligne du terrain */
    private static final float STROKE_TERRAIN = 3.0f;

    /** Image de fond (ciel pixel art) */
    private BufferedImage backgroundImage;
    
    /** Image du coeur (vies) */
    private BufferedImage heartSprite;

    /** Image de la pomme */
    private BufferedImage appleSprite;

    /** Couleur du terrain (blanc) */
    private static final Color TERRAIN_COLOR = Color.WHITE;

    /** Le modèle : position courante de l'ovale */
    private Position maposition;
    
    /** Le parcours : la ligne brisée */
    private Parcours monParcours;
    
    /** Gestionnaire du décor (parallaxe) */
    private DecorManager decorManager;
    
    /** Frame d'animation actuelle (-1 = pas d'animation) */
    private int frameAnimation = -1;

    /** Frame d'animation de capture de pomme (-1 = pas d'animation) */
    private int appleAnimFrame = -1;

    /** Position de l'animation pomme en cours (coordonnées modèle décalées) */
    private int appleAnimX = 0;
    private int appleAnimY = 0;

    /**
     * Constructeur - définit la dimension de la fenêtre.
     * @param p la position du joueur
     * @param parcours le terrain généré
     */
    public Affichage(Position p, Parcours parcours) {
        maposition = p;
        monParcours = parcours;
        setPreferredSize(new Dimension(LARGEUR_INIT, HAUTEUR_INIT));
        
        // Chargement de l'image de fond
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/sprites/background_sky.png"));
            heartSprite = ImageIO.read(getClass().getResourceAsStream("/sprites/heart.png"));
            appleSprite = ImageIO.read(getClass().getResourceAsStream("/sprites/apple.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erreur chargement sprites: " + e.getMessage());
        }
    }
    
    /**
     * Configure le gestionnaire de décor.
     * @param manager le gestionnaire de décor parallaxe
     */
    public void setDecorManager(DecorManager manager) {
        this.decorManager = manager;
    }
    
    /** Définit la frame d'animation actuelle */
    public void setAnimationFrame(int frame) {
        this.frameAnimation = frame;
    }

    /** Définit la frame d'animation de capture de pomme */
    public void setAppleAnimationFrame(int frame) {
        this.appleAnimFrame = frame;
    }

    /** Définit la position de l'animation pomme */
    public void setAppleAnimationPos(int x, int y) {
        this.appleAnimX = x;
        this.appleAnimY = y;
    }

    /** Calcule le ratio X dynamiquement */
    private double getRatioX() {
        return getWidth() / (double) (Position.BEFORE + Position.AFTER);
    }
    
    /** Calcule le ratio Y dynamiquement */
    private double getRatioY() {
        return getHeight() / (double) (Position.HAUTEUR_MAX - Position.HAUTEUR_MIN);
    }

    /** Transforme une coordonnée X du modèle en coordonnée X de la vue */
    public int transformX(int xModel) {
        return (int) ((xModel + Position.BEFORE) * getRatioX());
    }

    /** Transforme une coordonnée Y du modèle en coordonnée Y de la vue */
    public int transformY(int yModel) {
        return (int) ((Position.HAUTEUR_MAX - yModel) * getRatioY());
    }

    /**
     * Dessine le fond dégradé du ciel.
     * @param g2d le contexte Graphics2D
     */
    private void drawBackground(Graphics2D g2d) {
        // 1. Dessiner le fond (Image pixel art ou fallback dégradé)
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Fallback : dégradé simple
            GradientPaint skyGradient = new GradientPaint(
                0, 0, Color.BLUE,
                0, getHeight(), Color.MAGENTA
            );
            g2d.setPaint(skyGradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Dessine la ligne brisée du terrain avec Graphics2D.
     * Utilise un trait épais et anti-aliasé.
     * @param g2d le contexte Graphics2D
     */
    private void drawParcours(Graphics2D g2d) {
        g2d.setColor(TERRAIN_COLOR);
        g2d.setStroke(new BasicStroke(STROKE_TERRAIN, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        ArrayList<Point> points = monParcours.getPoints();
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            
            int x1 = transformX(p1.x);
            int y1 = transformY(p1.y);
            int x2 = transformX(p2.x);
            int y2 = transformY(p2.y);
            
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * Dessine l'ovale du joueur (contour uniquement).
     * @param g2d le contexte Graphics2D
     * @param x position X
     * @param y position Y
     * @param h hauteur de l'ovale
     */
    private void drawOval(Graphics2D g2d, int x, int y, int h) {
        // Contour de l'ovale uniquement (pas de remplissage)
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawOval(x, y, LARG_OVAL, h);
    }
    
    /**
     * Dessine les coeurs de vie en haut à droite.
     * @param g2d le contexte Graphics2D
     */
    private void drawHearts(Graphics2D g2d) {
        int lives = maposition.getLives();
        int heartSize = 60;  // Encore plus grand
        int spacing = -20;   // Chevauchement pour être très proches
        // Positionnement en haut à droite
        int startX = getWidth() - (heartSize + spacing) * 3 - 20;
        int y = 20;
        
        for (int i = 0; i < lives; i++) {
            int x = startX + i * (heartSize + spacing);
            
            if (heartSprite != null) {
                g2d.drawImage(heartSprite, x, y, heartSize, heartSize, null);
            }
        }
    }

    /**
     * Dessine les pommes collectibles sur le terrain.
     * @param g2d le contexte Graphics2D
     */
    private void drawPommes(Graphics2D g2d) {
        if (appleSprite == null) return;

        int appleSize = 30;
        ArrayList<Point> pommes = monParcours.getPommes();

        for (Point p : pommes) {
            int px = transformX(p.x) - appleSize / 2;
            int py = transformY(p.y) - appleSize / 2;
            g2d.drawImage(appleSprite, px, py, appleSize, appleSize, null);
        }

        // Animation de capture : grossissement + fondu
        if (appleAnimFrame >= 0 && appleAnimFrame <= AnimationPomme.NOMBRE_FRAMES) {
            float progress = appleAnimFrame / (float) AnimationPomme.NOMBRE_FRAMES;
            float scale = 1.0f + progress;
            float alpha = Math.max(1.0f - progress, 0.01f);

            int animSize = (int) (appleSize * scale);
            int ax = transformX(appleAnimX) - animSize / 2;
            int ay = transformY(appleAnimY) - animSize / 2;

            Composite oldComp = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(appleSprite, ax, ay, animSize, animSize, null);
            g2d.setComposite(oldComp);
        }
    }

    /**
     * Dessine le score (nombre de pommes) en haut à gauche.
     * @param g2d le contexte Graphics2D
     */
    private void drawScore(Graphics2D g2d) {
        int score = maposition.getScore();
        int iconSize = 30;
        int x = 20;
        int y = 25;

        if (appleSprite != null) {
            g2d.drawImage(appleSprite, x, y, iconSize, iconSize, null);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.drawString("x " + score, x + iconSize + 5, y + iconSize - 8);
    }

    /**
     * Méthode de rendu principale utilisant Graphics2D.
     * Dessine dans l'ordre : fond, décor arrière, terrain, ovale, décor avant.
     * @param g le contexte graphique
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Cast vers Graphics2D pour fonctionnalités avancées
        Graphics2D g2d = (Graphics2D) g;
        
        // Active l'anti-aliasing pour un rendu lisse
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // 1. Dessine le fond dégradé
        drawBackground(g2d);
        
        // 2. Dessine le décor arrière-plan (montagnes, nuages)
        if (decorManager != null) {
            decorManager.drawDecor(g2d);
        }
        
        // 3. Dessine le terrain
        drawParcours(g2d);

        // 3.5 Dessine les pommes (après terrain, avant ovale)
        drawPommes(g2d);

        // 4. Calcul de la position de l'ovale
        double ratioY = getRatioY();
        int x = transformX(0) - LARG_OVAL / 2;
        
        // Animation de collision : effet de secousse
        if (frameAnimation >= 0) {
            int shakeOffset = (frameAnimation % 2 == 0) ? 2 : -2;
            x += shakeOffset;
        }
        
        int y = transformY(maposition.getPosition() + Position.HAUTEUR_OVALE);
        int h = (int) (Position.HAUTEUR_OVALE * ratioY);
        
        // 5. Dessine l'ovale du joueur
        drawOval(g2d, x, y, h);
        
        // 6. Dessine le décor premier plan (arbres)
        if (decorManager != null) {
            decorManager.drawForeground(g2d);
        }
        
        // 7. Dessine les coeurs (HUD)
        drawHearts(g2d);

        // 8. Dessine le score (HUD)
        drawScore(g2d);
    }
}