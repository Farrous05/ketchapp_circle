package com.example.main;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Test {
    public static void main(String[] args) {
        /* Une fenêtre avec pour titre "Test" */
        JFrame maFenetre = new JFrame("Test");
        /* Une étiquette avec pour texte "Hello World !" */
        JLabel monLabel = new JLabel("Hello World !");
        /* Un bouton "coucou" qui écrit "coucou" dans la console chaque fois qu'on clique dessus */
        JButton monBouton = new JButton("coucou");
        monBouton.addActionListener(e -> System.out.println("coucou"));
        maFenetre.add(monBouton);
        /* On rend la fenêtre visible */
        maFenetre.setVisible(true);
        /* On définit la taille de la fenêtre */
        maFenetre.setSize(200, 200);
        /* On définit l'action à effectuer quand on ferme la fenêtre */
        maFenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
}
