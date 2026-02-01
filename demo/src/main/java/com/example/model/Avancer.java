package com.example.model;

public class Avancer extends Thread {

  /** Délai entre chaque avancement (ms) */
  public static final int DELAY = 50;

  private Position position;
  private Parcours parcours;

  public Avancer(Position p, Parcours parcours) {
    this.position = p;
    this.parcours = parcours;
  }

  @Override
  public void run() {
    while (true) {
        // Avance de 1 pixel (virtuel)
        position.avancer();
        
        // Met à jour la génération infini
        parcours.update();
        
        try {
            sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
  }
}
