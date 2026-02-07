package com.example.main;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.example.control.ReactionClic;
import com.example.model.Avancer;
import com.example.model.DecorManager;
import com.example.model.Descendre;
import com.example.model.Parcours;
import com.example.model.Position;
import com.example.util.SoundManager;
import com.example.view.Affichage;
import com.example.view.AnimationAccueil;
import com.example.view.AnimationCollision;
import com.example.view.AnimationPomme;
import com.example.view.EcranAccueil;
import com.example.view.Redessine;

/**
 * Classe principale du jeu Ketchapp Circle.
 * Gère le CardLayout pour basculer entre l'écran d'accueil et le jeu.
 */
public class Main {

    /**
     * Point d'entrée du programme.
     * Affiche l'écran d'accueil puis lance le jeu au clic sur "Jouer".
     */
    public static void main(String[] args) {
        JFrame maFenetre = new JFrame("Ketchapp Circle");
        maFenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* CardLayout pour basculer entre accueil et jeu */
        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);

        /* Écran d'accueil */
        EcranAccueil accueil = new EcranAccueil();
        container.add(accueil, "accueil");

        /* Animation de l'écran d'accueil */
        AnimationAccueil animAccueil = new AnimationAccueil(
                accueil, EcranAccueil.LARGEUR, EcranAccueil.HAUTEUR);
        accueil.setAnimationAccueil(animAccueil);
        animAccueil.start();

        // Musique du menu
        SoundManager.getInstance().playMenuMusic();

        /* Action du bouton "Jouer" : créer et lancer le jeu */
        accueil.setStartAction(() -> {
            // Créer le modèle
            Position p = new Position();
            Parcours parcours = new Parcours(p);

            // Créer l'affichage du jeu
            Affichage a = new Affichage(p, parcours);
            container.add(a, "jeu");

            // Décor parallaxe
            DecorManager decor = new DecorManager(a);
            a.setDecorManager(decor);
            decor.start();

            // Musique de combat
            SoundManager.getInstance().playBattleMusic();

            // Rafraîchissement
            Redessine r = new Redessine(a);
            r.start();

            // Descente (gravité)
            Descendre d = new Descendre(p);
            d.start();

            // Animation collision
            AnimationCollision animCollision = new AnimationCollision(a);
            animCollision.start();

            // Animation capture pomme
            AnimationPomme animPomme = new AnimationPomme(a);
            animPomme.start();
            parcours.setAnimationPomme(animPomme);

            // Avancement du monde
            Avancer av = new Avancer(p, parcours);
            av.setAnimationCollision(animCollision);
            av.setGameOverAction(() -> {
                // Retour à l'écran d'accueil
                container.remove(a);
                accueil.repaint();
                cardLayout.show(container, "accueil");
                SoundManager.getInstance().playMenuMusic();
            });
            av.start();

            // Contrôleur (gestion des clics)
            new ReactionClic(a, p);

            // Basculer vers le jeu
            cardLayout.show(container, "jeu");
            a.requestFocusInWindow();
        });

        /* Finaliser et afficher */
        maFenetre.add(container);
        maFenetre.setResizable(false);
        maFenetre.pack();
        maFenetre.setVisible(true);
    }
}
