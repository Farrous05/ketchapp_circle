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

    // Ratios are now dynamic
    // private static final int RATIO_X_INIT = 2; // Kept for initial size only
    // private static final int RATIO_Y_INIT = 1;

    /** Dimensions de la fenêtre initiale */
    public static final int LARGEUR_INIT = (Position.BEFORE + Position.AFTER) * 2;
    public static final int HAUTEUR_INIT = Position.HAUTEUR_MAX - Position.HAUTEUR_MIN ;


    /* Le modèle :position courante de l'oval */
    private Position maposition;

    /** Le constructeur définit la dimension de la fenêtre */
    public Affichage(Position p) {
      maposition = p;
      setPreferredSize(new Dimension(LARGEUR_INIT, HAUTEUR_INIT));
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

    /** Redéfinition de la méthode paint */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        // Calcul des dimensions dynamiques
        double ratioY = getRatioY();

        // Position X : Le centre est à (xModel=0), donc pixel = transformX(0)
        // Mais on veut le coin supérieur gauche, donc on retire demi-largeur
        int x = transformX(0) - LARG_OVAL / 2;
        
        // Position Y : Le bas de l'ovale est à yModel. 
        // transformY donne le pixel correspondant à l'altitude yModel.
        // Mais drawOval dessine depuis le coin haut-gauche. 
        // Donc on veut le pixel correspondant à (yModel + HAUTEUR_OVALE) ?
        // transformY(yModel + HAUTEUR_OVALE) -> (MAX - (y + H)) * RY -> (MAX - y - H) * RY. C'est correct.
        int y = transformY(maposition.getPosition() + Position.HAUTEUR_OVALE);
        
        int h = (int) (Position.HAUTEUR_OVALE * ratioY);
        
        g.drawOval(x, y, LARG_OVAL, h);
    }
}