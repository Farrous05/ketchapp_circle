package com.example.control;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.example.model.Position;
import com.example.view.Affichage;

/** Le contrôleur sur les clics dans l'affichage */
public class ReactionClic implements MouseListener {
    private Position position;

    /** Constructeur */
    public ReactionClic(Affichage a, Position p) {
        a.addMouseListener(this);
        position = p;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        position.jump();
       // monAffichage.repaint(); en réalité, on ne veut pas redessiner l'interface à l'intèrieur d'un controleur
    }

    @Override
    public void mouseEntered(MouseEvent arg0) { }

    @Override
    public void mouseExited(MouseEvent arg0) { }

    @Override
    public void mousePressed(MouseEvent arg0) { }

    @Override
    public void mouseReleased(MouseEvent arg0) { }
}