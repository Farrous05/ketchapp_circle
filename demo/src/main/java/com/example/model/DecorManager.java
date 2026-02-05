package com.example.model;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.example.view.Affichage;

/**
 * Thread gestionnaire des objets décoratifs du décor.
 * Gère les 4 couches de parallaxe :
 * - Étoiles (fond, vitesse 0.05x)
 * - Montagnes (arrière-plan, vitesse 0.3x)
 * - Nuages (milieu, vitesse 0.5x)
 * - Arbres (premier plan, vitesse 0.8x)
 * 
 * Chaque objet se déplace de droite à gauche avec sa propre vitesse.
 */
public class DecorManager extends Thread {

    /** Délai entre chaque mise à jour (ms) */
    public static final int DELAY = 50;
    
    /** Vitesse de base de déplacement */
    public static final double BASE_SPEED = 2.0;

    /** Liste des objets décoratifs */
    private ArrayList<DecorObject> objects;
    
    /** Référence à l'affichage pour obtenir les dimensions */
    private Affichage affichage;
    
    /** Générateur aléatoire pour le spawn */
    private Random random;
    
    /** Compteur pour espacer les spawns */
    private int spawnCounter = 0;

    /**
     * Constructeur du gestionnaire de décor.
     * @param affichage référence à l'affichage
     */
    public DecorManager(Affichage affichage) {
        this.affichage = affichage;
        this.objects = new ArrayList<>();
        this.random = new Random();
        
        // Initialise quelques objets au démarrage
        initializeObjects();
    }

    /** Crée des objets initiaux pour remplir l'écran */
    private void initializeObjects() {
        int screenWidth = Affichage.LARGEUR_INIT;
        int screenHeight = Affichage.HAUTEUR_INIT;
        
        // Quelques montagnes
        for (int i = 0; i < 3; i++) {
            objects.add(new DecorObject(i * 200, DecorObject.Type.MOUNTAIN, screenHeight));
        }
        
        // Des étoiles
        for (int i = 0; i < 5; i++) {
            objects.add(new DecorObject(random.nextInt(screenWidth), DecorObject.Type.STAR, screenHeight));
        }
        
        // Quelques nuages
        for (int i = 0; i < 4; i++) {
            objects.add(new DecorObject(i * 150 + 50, DecorObject.Type.CLOUD, screenHeight));
        }
        
        // Quelques arbres
        for (int i = 0; i < 3; i++) {
            objects.add(new DecorObject(i * 120, DecorObject.Type.TREE, screenHeight));
        }
    }

    /**
     * Boucle principale du thread.
     * Met à jour les positions, supprime les objets hors écran,
     * et génère de nouveaux objets.
     */
    @Override
    public void run() {
        while (true) {
            // Met à jour tous les objets
            synchronized (objects) {
                Iterator<DecorObject> it = objects.iterator();
                while (it.hasNext()) {
                    DecorObject obj = it.next();
                    obj.update(BASE_SPEED);
                    
                    // Supprime si hors écran
                    if (obj.isOffScreen()) {
                        it.remove();
                    }
                }
            }
            
            // Génère de nouveaux objets périodiquement
            spawnCounter++;
            if (spawnCounter >= 20) {  // Toutes les ~1 seconde
                spawnNewObjects();
                spawnCounter = 0;
            }
            
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /** Génère de nouveaux objets à droite de l'écran */
    private void spawnNewObjects() {
        int screenWidth = affichage.getWidth();
        int screenHeight = affichage.getHeight();
        
        if (screenWidth <= 0) screenWidth = Affichage.LARGEUR_INIT;
        if (screenHeight <= 0) screenHeight = Affichage.HAUTEUR_INIT;
        
        synchronized (objects) {
            // Spawn aléatoire d'un type d'objet
            int chance = random.nextInt(100);
            
            if (chance < 20) {  // 20% montagne
                objects.add(new DecorObject(screenWidth + 50, DecorObject.Type.MOUNTAIN, screenHeight));
            } else if (chance < 50) {  // 30% nuage
                objects.add(new DecorObject(screenWidth + 30, DecorObject.Type.CLOUD, screenHeight));
            } else if (chance < 70) {  // 20% arbre
                objects.add(new DecorObject(screenWidth + 20, DecorObject.Type.TREE, screenHeight));
            } else if (chance < 80) {  // 10% étoile
                objects.add(new DecorObject(screenWidth + 20, DecorObject.Type.STAR, screenHeight));
            }
            // 20% pas de spawn
        }
    }

    /**
     * Dessine tous les objets décoratifs.
     * Appelé par Affichage dans sa méthode paint.
     * @param g2d contexte graphique Graphics2D
     */
    public void drawDecor(Graphics2D g2d) {
        synchronized (objects) {
            // Dessiner par couche (arrière vers avant)
            // 0. Étoiles (fond absolu)
            for (DecorObject obj : objects) {
                if (obj.getType() == DecorObject.Type.STAR) {
                    obj.draw(g2d);
                }
            }
            // 1. Montagnes (arrière-plan)
            for (DecorObject obj : objects) {
                if (obj.getType() == DecorObject.Type.MOUNTAIN) {
                    obj.draw(g2d);
                }
            }
            // 2. Nuages (milieu)
            for (DecorObject obj : objects) {
                if (obj.getType() == DecorObject.Type.CLOUD) {
                    obj.draw(g2d);
                }
            }
            // 3. Arbres (premier plan) - dessinés après le terrain
        }
    }
    
    /**
     * Dessine les objets de premier plan (après le terrain).
     * @param g2d contexte graphique Graphics2D
     */
    public void drawForeground(Graphics2D g2d) {
        synchronized (objects) {
            for (DecorObject obj : objects) {
                if (obj.getType() == DecorObject.Type.TREE) {
                    obj.draw(g2d);
                }
            }
        }
    }
}
