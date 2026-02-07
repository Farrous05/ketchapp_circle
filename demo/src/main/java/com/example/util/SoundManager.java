package com.example.util;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Gestionnaire de sons pour le jeu.
 * Utilise javax.sound.sampled pour lire des fichiers .wav depuis les ressources.
 * Implémente le pattern Singleton pour un accès facile depuis n'importe où.
 */
public class SoundManager {

    private static SoundManager instance;
    private Clip backgroundMusic;

    // Chemins des fichiers audio (dans src/main/resources)
    private static final String SOUND_JUMP = "/sounds/jump.wav";
    private static final String SOUND_HIT = "/sounds/hit.wav";
    private static final String SOUND_EAT = "/sounds/apple-crunch.wav";
    private static final String SOUND_MENU = "/sounds/Menu Theme.wav";
    private static final String SOUND_BATTLE = "/sounds/Through Mountains.wav";

    private SoundManager() {
        // Constructeur privé
    }

    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Joue un son court (effet sonore).
     * @param resourcePath chemin du fichier audio dans les ressources
     */
    private void playSound(String resourcePath) {
        new Thread(() -> {
            try {
                URL url = getClass().getResource(resourcePath);
                if (url == null) {
                    System.err.println("Son non trouvé: " + resourcePath);
                    return;
                }

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
                
                // Attente active pour éviter fermeture prématurée (simple pour démo)
                // Ou utiliser LineListener pour close() à la fin
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Erreur lecture son " + resourcePath + ": " + e.getMessage());
            }
        }).start();
    }

    public void playJump() {
        playSound(SOUND_JUMP);
    }

    public void playHit() {
        playSound(SOUND_HIT);
    }

    public void playEat() {
        playSound(SOUND_EAT);
    }

    /**
     * Joue une musique de fond en boucle.
     * Arrête toute musique en cours.
     * @param resourcePath chemin de la musique
     */
    private void playMusic(String resourcePath) {
        stopBackgroundMusic(); // Arrête la musique précédente

        new Thread(() -> {
            try {
                URL url = getClass().getResource(resourcePath);
                
                // Gestion espace URL (encodage simple si nécessaire)
                if (url == null && resourcePath.contains(" ")) {
                     url = getClass().getResource(resourcePath.replace(" ", "%20"));
                }
                
                if (url == null) {
                    System.err.println("Musique non trouvée: " + resourcePath);
                    return;
                }

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioIn);
                
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY); // Boucle infinie
                backgroundMusic.start();

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Erreur lecture musique: " + e.getMessage());
            }
        }).start();
    }

    public void playMenuMusic() {
        playMusic(SOUND_MENU);
    }

    public void playBattleMusic() {
        playMusic(SOUND_BATTLE);
    }

    /**
     * Arrête la musique de fond.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }
}
