package com.example.view;

/** Le thread responsable de l'affichage */
public class Redessine extends Thread {

  /** Fréquence de rafraissement = 1/20e (50ms) */
  public static final int DELAY = 50;

  /* l'affichage */
  private Affichage monAffichage;

  /** Constructeur */
  public Redessine(Affichage a) {
    monAffichage = a;
  }

  /** Redéfinition de la méthode run : mettre à jour toutes les DELAY ms */
  @Override
  public void run() {
    while (true) {
      monAffichage.revalidate();
      monAffichage.repaint();
      try {
        Thread.sleep(DELAY);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
