package com.example.main;

import javax.swing.JFrame;

import com.example.control.ReactionClic;
import com.example.model.Descendre;
import com.example.model.Position;
import com.example.view.Affichage;
import com.example.view.Redessine;

/** La classe principale de ce projet */
public class Main {

    /** La méthode de lancement du programme */
    public static void main(String[] args) {
    JFrame maFenetre = new JFrame("Exercice 1");

    /* Modèle */
    Position p = new Position();

    /* Affichage */
    Affichage a = new Affichage(p);
    maFenetre.add(a);

    /* rafraissement */
    Redessine r = new Redessine(a);
    r.start();

    /* descente */
    Descendre d = new Descendre(p);
    d.start();

    /* Controleur */
    new ReactionClic(a, p);

    /* finaliser */
    maFenetre.pack();
    maFenetre.setVisible(true);
    }
}
