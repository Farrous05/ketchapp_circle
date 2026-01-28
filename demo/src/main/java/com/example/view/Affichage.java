package com.example.view;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.example.model.Position;

/** 
 * La classe pour dessiner 
 */
public class Affichage extends JPanel {



    /** Dimensions de l’ovale */
    public static final int LARG_OVAL = 50;

    /** Ratios de mise à l'échelle */
    public static final int RATIO_X = 2;
    public static final int RATIO_Y = 1;

    /** Dimensions de la fenêtre */
    public static final int LARGEUR = (Position.BEFORE + Position.AFTER) * RATIO_X;
    public static final int HAUTEUR = (Position.HAUTEUR_MAX - Position.HAUTEUR_MIN) * RATIO_Y;


    /* Le modèle :position courante de l'oval */
    private Position maposition;

    /* Position horizontale calculée */
    private int positionX;

    /** Le constructeur définit la dimension de la fenêtre */
    public Affichage(Position p) {
      maposition = p;
      setPreferredSize(new Dimension(LARGEUR, HAUTEUR));
      
      // Calcul de la position horizontale : BEFORE * RATIO_X - demi-largeur
      positionX = Position.BEFORE * RATIO_X - LARG_OVAL / 2;
    }

    /** Redéfinition de la méthode paint */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        // Calcul de la position verticale : (HAUTEUR_MAX - hauteur - HAUTEUR_OVALE) * RATIO_Y
        int positionY = (Position.HAUTEUR_MAX - maposition.getPosition() - Position.HAUTEUR_OVALE) * RATIO_Y;
        
        g.drawOval(positionX, positionY, LARG_OVAL, Position.HAUTEUR_OVALE);
    }
}