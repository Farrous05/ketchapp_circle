# Rapport de Projet Technique : Ketchapp Circle (Clone)

Ce document présente l'analyse, la conception et la réalisation d'un mini-jeu Java/Swing inspiré du jeu mobile "Ketchapp Circle" (type Runner Infini).

## 1. Introduction (0.5 page)
**Objectif :** Développer un jeu 2D où un avatar (ovale) évite des obstacles en sautant, dans un monde généré procéduralement qui défile.
Le projet met l'accent sur :
*   La maîtrise de **Java Swing** pour le graphisme.
*   L'architecture **MVC (Modèle-Vue-Contrôleur)**.
*   La gestion de la **concurrence (Threads)** pour la boucle de jeu.
*   Les algorithmes de **transformation géométrique** pour un affichage adaptatif.

---

## 2. Analyse Globale (1-2 pages)
### Fonctionnalités Principales & Complexité

1.  **Moteur Physique (Gravité & Saut)**
    *   *Difficulté : Moyenne.* Indispensable pour le gameplay.
    *   L'ovale doit subir une attraction constante vers le bas et pouvoir s'impulser vers le haut.

2.  **Architecture MVC**
    *   *Difficulté : Moyenne.* Prioritaire pour la qualité du code.
    *   Séparation stricte : le `Modèle` gère les données (position, score), la `Vue` dessine, le `Contrôleur` gère les clics.

3.  **Rendu Dynamique (Redimensionnement)**
    *   *Difficulté : Élevée.*
    *   Le jeu doit s'adapter à n'importe quelle taille de fenêtre. Un monde de 100m de haut doit remplir la fenêtre, qu'elle fasse 400px ou 800px de haut. Cela implique des calculs de ratios dynamiques.

4.  **Monde Défilant (Infinite Scrolling)**
    *   *Difficulté : Élevée.* Cœur du gameplay de type "Runner".
    *   Le joueur est statique horizontalement (au tiers de l'écran). C'est le terrain qui se déplace vers la gauche pour simuler avancement.

5.  **Génération Procédurale de Terrain**
    *   *Difficulté : Moyenne.*
    *   Le sol est une ligne brisée générée aléatoirement au fur et à mesure de l'avancée, garantissant une rejouabilité infinie.

---

## 3. Plan de Développement (2-3 pages)
### Diagramme de Gantt (Estimation des charges)

Les temps incluent l'analyse, le développement et les tests unitaires/visuels.

| Phase | Tâche | Temps Estimé |
| :--- | :--- | :--- |
| **P1** | **Initialisation** : Création du projet Maven, Fenêtre JFrame vide. | 1h 00 |
| **P2** | **Affichage de Base** : Classe `Affichage`, surcharge de `paint()`, dessin d'Ovale simple. | 0h 50 |
| **P3** | **Physique** : Implémentation de `Position` (Y, v), Thread `Descendre` (gravité), `ReactionClic` (saut). | 1h 50 |
| **P4** | **Refactoring MVC** : Structuration en packages (`model`, `view`, `control`). Nettoyage du code. | 1h 30 |
| **P5** | **Rendu Adaptatif** : Implémentation des méthodes `transformX`/`transformY` et des ratios. | 3h 00 |
| **P6** | **Génération Terrain** : Algorithme de génération de la ligne brisée (`Parcours`). | 1h 25 |
| **P7** | **Défilement** : Implémentation de l'attribut `avancement` et du Thread `Avancer`. | 2h 00 |
| **TOTAL** | | **~ 11h 30** |

---

## 4. Conception Générale (1 page)
### Architecture Modulaire

Le projet est divisé en quatre blocs fonctionnels principaux qui communiquent entre eux :

1.  **Le Cerveau (Packages `model` & `main`)**
    *   **Main** : Le chef d'orchestre. Il instancie tous les objets et démarre les moteurs (threads).
    *   **Position** : Stocke l'état instantané du joueur (Hauteur Y, Vitesse V, Distance Parcourue X).
    *   **Parcours** : Stocke la géométrie du monde (Liste des points de la ligne brisée).

2.  **Les Moteurs (Threads)**
    *   **Descendre** : Le moteur de gravité. Toutes les 150ms, il dit à `Position` de descendre.
    *   **Avancer** : Le moteur de scrolling. Toutes les 40ms, il dit à `Position` d'augmenter la distance parcourue.
    *   **Redessine** : Le moteur de rendu. Toutes les 50ms, il demande à la Vue de se rafraîchir.

3.  **Les Yeux (Package `view`)**
    *   **Affichage** : Un panneau Swing (`JPanel`). À chaque demande de rafraîchissement, il interroge le Modèle (`Position` et `Parcours`), convertit les coordonnées "Monde" en "Pixels", et dessine l'image.

4.  **Les Mains (Package `control`)**
    *   **ReactionClic** : Un écouteur de souris. Quand le joueur clique, il ordonne immédiatement à `Position` d'effectuer un saut (`jump()`).

---

## 5. Conception Détaillée
Cette section détaille les algorithmes clés développés.

### A. Algorithme de Génération de Terrain (Infinite Scrolling)
Situé dans la classe `Parcours`, il génère une suite de points et nettoie les anciens.

**Entrées :** `X_MIN`, `X_MAX` (Pas horizontal), `Y_MIN`, `Y_MAX` (Bornes verticales), `AVANCEMENT` (Distance parcourue par le joueur).
**Sortie :** Modification de la Liste des Points.

```text
// 1. Suppression des points obsolètes (Optimisation Mémoire)
SI LISTE contient au moins 2 points :
    // On vérifie la position du 2ème point par rapport à l'écran
    POINT_2 = LISTE[1]
    POSITION_ECRAN_P2 = POINT_2.X - AVANCEMENT
    
    // Si le 2ème point est sorti de l'écran par la gauche (marge de sécurité)
    SI POSITION_ECRAN_P2 < -MARGE_GAUCHE
        SUPPRIMER le 1er point de LISTE
    FIN SI
FIN SI

// 2. Génération de nouveaux points (Infinité)
POINT_DERNIER = LISTE[DERNIER]
POSITION_ECRAN_DERNIER = POINT_DERNIER.X - AVANCEMENT

// Si le dernier point arrive bientôt dans l'écran visible à droite
SI POSITION_ECRAN_DERNIER < FIN_ECRAN_DROITE + MARGE_DROITE
    NOUVEAU_X = POINT_DERNIER.X + RANDOM(X_MIN, X_MAX)
    NOUVEAU_Y = RANDOM(Y_MIN, Y_MAX_SAFE)
    
    AJOUTER (NOUVEAU_X, NOUVEAU_Y) à LISTE
FIN SI
```

### B. Algorithme de Transformation (Monde -> Écran)
Situé dans la classe `Affichage`, il permet le redimensionnement fluide.

**Entrées :** `valeur_modele` (mètres), `taille_fenetre` (pixels), `taille_monde` (mètres).
**Sortie :** `valeur_pixel` (pixels).

```text
// Calcul du facteur de zoom (Ratio)
RATIO = taille_fenetre / taille_monde

FONCTION Transform(valeur_modele):
   RETOURNER valeur_modele * RATIO
```
*Note : Pour l'axe Y, une inversion est nécessaire car l'écran (0,0) est en haut à gauche, alors que le monde (0) est en bas.*

### C. Algorithme de Défilement (Scrolling)
L'illusion de mouvement est créée en soustrayant la distance parcourue à la position des obstacles.

```text
POUR CHAQUE Point P du Parcours:
   X_VIRTUEL = P.x - POSITION.AVANCEMENT
   DESSINER au pixel TransformX(X_VIRTUEL)
FIN POUR
```

---

## 6. Résultat (1 page)
Le résultat final est une application fluide et réactive.
*   **Visuel** : Un ovale qui chute de manière réaliste et rebondit lors des sauts. Une ligne brisée complexe qui défile de la droite vers la gauche.
*   **Technique** : Pas de scintillement (grâce au Double Buffering de Swing). La vitesse du jeu est constante et ne dépend pas de la puissance de la machine (grâce aux `Thread.sleep()`).

---

## 7. Documentation Utilisateur (1 page)
### Manuel du Jeu
1.  **Lancement** : Exécuter le fichier `Main.java` (ou le .jar compilé).
2.  **But du Jeu** : Faire survivre l'ovale le plus longtemps possible en évitant de tomber trop bas ou de toucher le plafond (actuellement implémenté comme limites physiques).
3.  **Contrôles** :
    *   **Clic Gauche** (n'importe où) : **Sauter**.
    *   Le personnage avance tout seul, vous ne gérez que la hauteur.

---

## 8. Documentation Développeur (1-2 pages)
### Guide de Maintenance

**Où est le code ?**
*   `com.example.main.Main` : Point d'entrée. C'est ici qu'on assemble les briques.
*   `com.example.model.Position` : C'est ici qu'on règle la physique.

**Comment modifier les paramètres du jeu ?**
Toutes les constantes sont centralisées et faciles à modifier :

1.  **Changer la vitesse du jeu (Scrolling)** :
    *   Fichier : `Avancer.java`
    *   Variable : `DELAY` (Par défaut 40ms). Réduire pour accélérer.

2.  **Changer la gravité (Vitesse de chute)** :
    *   Fichier : `Descendre.java`
    *   Variable : `DELAY` (Par défaut 150ms). Augmenter pour une gravité "Lunaire", réduire pour une gravité "Jupiter".

3.  **Changer la puissance du saut** :
    *   Fichier : `Position.java`
    *   Variable : `IMPULSION`. Augmenter pour sauter plus haut.

**Améliorations Possibles (To-Do List)** :
1.  **Détection de Collisions** : Implémenter la méthode mathématique pour vérifier si le cercle intersecte un segment de ligne.
2.  **Score** : Afficher `position.getAvancement()` à l'écran comme score.
3.  **Génération Infinie** : Nettoyer la liste des points du parcours (supprimer ceux qui sont sortis à gauche) pour libérer la mémoire lors des très longues parties.

---

## 9. Conclusion et Perspectives (0.5 page)
Ce projet a permis de transformer des concepts théoriques (MVC, Threads, POO) en un résultat visuel concret et ludique.
**Difficultés surmontées :**
*   Le passage des coordonnées "Mathématiques" (Y vers le haut) aux coordonnées "Informatiques" (Y vers le bas) a nécessité une gymnastique mentale rigoureuse.
*   La gestion de plusieurs threads modifiant les mêmes données a imposé une conception propre pour éviter les conflits.

**Bilan :** Le jeu est fonctionnel, le code est propre et extensible. C'est une base solide pour créer un jeu complet type "Flappy Bird" ou "Google Dino".
