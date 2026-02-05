package com.example.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.example.model.ScoreManager;

/**
 * Écran d'accueil du jeu.
 * Affiche le titre, le meilleur score, des pommes flottantes animées,
 * et des boutons pour lancer le jeu ou quitter.
 */
public class EcranAccueil extends JPanel {

    /** Dimensions (identiques à Affichage) */
    public static final int LARGEUR = 800;
    public static final int HAUTEUR = 400;

    /** Image de fond */
    private BufferedImage backgroundImage;

    /** Sprite de la pomme (pour l'animation flottante) */
    private BufferedImage appleSprite;

    /** Boutons */
    private JButton btnJouer;
    private JButton btnQuitter;

    /** Référence au thread d'animation */
    private AnimationAccueil animationAccueil;

    public EcranAccueil() {
        setPreferredSize(new Dimension(LARGEUR, HAUTEUR));
        setLayout(null); // Positionnement absolu pour les boutons

        // Chargement des sprites
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/sprites/background_sky.png"));
            appleSprite = ImageIO.read(getClass().getResourceAsStream("/sprites/apple.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Erreur chargement sprites accueil: " + e.getMessage());
        }

        // Bouton Jouer
        btnJouer = new JButton("Jouer");
        btnJouer.setFont(new Font("Arial", Font.BOLD, 20));
        btnJouer.setBounds(LARGEUR / 2 - 75, 240, 150, 45);
        btnJouer.setFocusPainted(false);
        add(btnJouer);

        // Bouton Quitter
        btnQuitter = new JButton("Quitter");
        btnQuitter.setFont(new Font("Arial", Font.PLAIN, 16));
        btnQuitter.setBounds(LARGEUR / 2 - 60, 300, 120, 35);
        btnQuitter.setFocusPainted(false);
        btnQuitter.addActionListener(e -> System.exit(0));
        add(btnQuitter);
    }

    /**
     * Définit l'action exécutée quand le joueur clique "Jouer".
     * @param action le Runnable à exécuter
     */
    public void setStartAction(Runnable action) {
        btnJouer.addActionListener(e -> action.run());
    }

    /**
     * Définit la référence au thread d'animation.
     * @param anim le thread d'animation de l'accueil
     */
    public void setAnimationAccueil(AnimationAccueil anim) {
        this.animationAccueil = anim;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Fond
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            GradientPaint gradient = new GradientPaint(0, 0, Color.BLUE, 0, getHeight(), Color.MAGENTA);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Pommes flottantes (animation)
        if (animationAccueil != null && appleSprite != null) {
            double[] xPos = animationAccueil.getXPositions();
            double[] yPos = animationAccueil.getYPositions();
            int size = 35;
            for (int i = 0; i < xPos.length; i++) {
                g2d.drawImage(appleSprite, (int) xPos[i], (int) yPos[i], size, size, null);
            }
        }

        // 3. Titre
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String titre = "Ketchapp Circle";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(titre)) / 2;
        g2d.drawString(titre, titleX, 120);

        // 4. Meilleur score
        int bestScore = ScoreManager.loadBestScore();
        g2d.setFont(new Font("Arial", Font.PLAIN, 22));
        String scoreText = "Meilleur Score : " + bestScore;
        fm = g2d.getFontMetrics();
        int scoreX = (getWidth() - fm.stringWidth(scoreText)) / 2;
        g2d.drawString(scoreText, scoreX, 180);

        // Petite icône pomme à côté du score
        if (appleSprite != null) {
            g2d.drawImage(appleSprite, scoreX - 30, 160, 25, 25, null);
        }
    }
}
