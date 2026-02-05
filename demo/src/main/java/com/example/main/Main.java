package com.example.main;

import javax.swing.JFrame;

import com.example.control.ReactionClic;
import com.example.model.Avancer;
import com.example.model.DecorManager;
import com.example.model.Descendre;
import com.example.model.Parcours;
import com.example.model.Position;
import com.example.view.Affichage;
import com.example.view.AnimationCollision;
import com.example.view.Redessine;

/**
 * Classe principale du jeu Ketchapp Circle.
 * Initialise tous les composants : modèle, vue, contrôleur et threads.
 */
public class Main {

    /**
     * Point d'entrée du programme.
     * Configure la fenêtre et lance tous les threads du jeu.
     */
    public static void main(String[] args) {
        JFrame maFenetre = new JFrame("Ketchapp Circle");
        maFenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* Modèle */
        Position p = new Position();
        Parcours parcours = new Parcours(p);

        /* Affichage */
        Affichage a = new Affichage(p, parcours);
        maFenetre.add(a);

        /* Décor parallaxe (montagnes, nuages, arbres) */
        DecorManager decor = new DecorManager(a);
        a.setDecorManager(decor);
        decor.start();

        /* Rafraîchissement */
        Redessine r = new Redessine(a);
        r.start();

        /* Descente (gravité) */
        Descendre d = new Descendre(p);
        d.start();
        
        /* Animation collision */
        AnimationCollision animCollision = new AnimationCollision(a);
        animCollision.start();
        
        /* Avancement du monde */
        Avancer av = new Avancer(p, parcours);
        av.setAnimationCollision(animCollision);
        av.start();

        /* Contrôleur (gestion des clics) */
        new ReactionClic(a, p);

        /* Finaliser et afficher */
        maFenetre.setResizable(false);
        maFenetre.pack();
        maFenetre.setVisible(true);
    }
}
