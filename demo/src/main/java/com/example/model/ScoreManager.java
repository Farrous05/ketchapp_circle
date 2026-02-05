package com.example.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Gère la persistance du meilleur score.
 * Lit et écrit le meilleur score dans un fichier texte (bestscore.txt).
 */
public class ScoreManager {

    private static final String FILE = "bestscore.txt";

    /**
     * Charge le meilleur score depuis le fichier.
     * @return le meilleur score, ou 0 si le fichier n'existe pas
     */
    public static int loadBestScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            return Integer.parseInt(reader.readLine().trim());
        } catch (IOException | NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    /**
     * Sauvegarde le score s'il dépasse le meilleur score actuel.
     * @param score le score de la partie terminée
     */
    public static void saveBestScore(int score) {
        int best = loadBestScore();
        if (score > best) {
            try (FileWriter writer = new FileWriter(FILE)) {
                writer.write(String.valueOf(score));
            } catch (IOException e) {
                System.err.println("Erreur sauvegarde score: " + e.getMessage());
            }
        }
    }
}
