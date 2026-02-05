package com.example.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * Représente un objet décoratif dans le décor de fond.
 * Chaque objet a une position, un type et une vitesse relative.
 * Les objets se déplacent de droite à gauche pour créer l'effet de parallaxe.
 * Utilise des sprites PNG pour le rendu.
 */
public class DecorObject {

    /** Types d'objets décoratifs disponibles */
    public enum Type {
        MOUNTAIN,  // Montagnes en arrière-plan (lent)
        CLOUD,     // Nuages au milieu (moyen)
        TREE,      // Arbres au premier plan (rapide)
        STAR       // Étoiles dans le ciel (statique ou très lent)
    }

    /** Images sprites chargées une seule fois (static) */
    private static BufferedImage cloudSprite;
    private static BufferedImage mountainSprite;
    private static BufferedImage treeSprite;
    private static BufferedImage starSprite;
    private static boolean spritesLoaded = false;

    /** Position X de l'objet */
    private double x;
    
    /** Position Y de l'objet */
    private int y;
    
    /** Type de l'objet (MOUNTAIN, CLOUD, TREE) */
    private Type type;
    
    /** Facteur de vitesse (0.0 à 1.0) */
    private double speedFactor;
    
    /** Échelle de l'image (pour variation de taille) */
    private double scale;
    
    /** Générateur aléatoire pour les variations */
    private static final Random random = new Random();

    /** Charge les sprites depuis les ressources */
    private static void loadSprites() {
        if (spritesLoaded) return;
        
        try {
            cloudSprite = ImageIO.read(DecorObject.class.getResourceAsStream("/sprites/cloud.png"));
            mountainSprite = ImageIO.read(DecorObject.class.getResourceAsStream("/sprites/mountain.png"));
            treeSprite = ImageIO.read(DecorObject.class.getResourceAsStream("/sprites/tree.png"));
            starSprite = ImageIO.read(DecorObject.class.getResourceAsStream("/sprites/star.png"));
            spritesLoaded = true;
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erreur chargement sprites: " + e.getMessage());
            spritesLoaded = false;
        }
    }

    /**
     * Constructeur d'un objet décoratif.
     * @param x position X initiale
     * @param type type d'objet (MOUNTAIN, CLOUD, TREE)
     * @param screenHeight hauteur de l'écran pour calculer Y
     */
    public DecorObject(int x, Type type, int screenHeight) {
        loadSprites();
        
        this.x = x;
        this.type = type;
        
        switch (type) {
            case MOUNTAIN -> {
                this.speedFactor = 0.3;
                this.scale = 0.25 + random.nextDouble() * 0.15;  
                int mHeight = mountainSprite != null ? (int) (mountainSprite.getHeight() * scale) : 100;
                this.y = screenHeight - mHeight;  
            }
            case CLOUD -> {
                this.speedFactor = 0.5;
                this.scale = 0.12 + random.nextDouble() * 0.08;  
                this.y = 15 + random.nextInt(40);
            }
            case TREE -> {
                this.speedFactor = 0.8;
                this.scale = 0.18 + random.nextDouble() * 0.1;  
                int tHeight = treeSprite != null ? (int) (treeSprite.getHeight() * scale) : 60;
                this.y = screenHeight - tHeight;  
            }
            case STAR -> {
                this.speedFactor = 0.05;  
                this.scale = 0.15 + random.nextDouble() * 0.15;  
                this.y = random.nextInt(screenHeight / 3);  
            }
        }
    }



    /**
     * Met à jour la position de l'objet.
     * @param baseSpeed vitesse de base du jeu
     */
    public void update(double baseSpeed) {
        x -= baseSpeed * speedFactor;
    }

    /**
     * Vérifie si l'objet est sorti de l'écran (à gauche).
     * @return true si l'objet doit être supprimé
     */
    public boolean isOffScreen() {
        return x < -200;
    }

    /**
     * Dessine l'objet (sprite image ou fallback géométrique).
     * @param g2d contexte graphique Graphics2D
     */
    public void draw(Graphics2D g2d) {
        int drawX = (int) x;
        BufferedImage sprite = null;
        
        switch (type) {
            case MOUNTAIN -> sprite = mountainSprite;
            case CLOUD -> sprite = cloudSprite;
            case TREE -> sprite = treeSprite;
            case STAR -> sprite = starSprite;
        }
        
        if (sprite != null) {
            int width = (int)(sprite.getWidth() * scale);
            int height = (int)(sprite.getHeight() * scale);
            g2d.drawImage(sprite, drawX, y, width, height, null);
        }
    }

    /** @return position X */
    public double getX() { return x; }
    
    /** @return type d'objet */
    public Type getType() { return type; }
}
