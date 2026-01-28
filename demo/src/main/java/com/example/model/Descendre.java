package com.example.model;
/** Thread qui fait redescendre l'ovale tout seul */
public class Descendre extends Thread {

    /** Une descente toutes les 100ms */
    public static final int DELAY = 100;
  
    /* Le modèle */
    private Position position;
  
    /** Constructeur */
    public Descendre(Position p) {
      position = p;
    }
  
    /** Diminue la hauteur de 1 toutes les 100ms (sans descendre sous 0) */
    @Override
    public void run() {
      while (true) {
        position.move();   // descend si possible
  
        try {
          Thread.sleep(DELAY);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
  