# Fonctionnalites CU

Nous avons développés les cas d'utilisations suivants :
    - Visualiser une partie
    - Créer une partie à partir d'une carte
    - Modifier la vitesse de la simulation

## Simulation
### Moteur (voir diag_seq_engine.png) -> Evan & Adrien

- collisions entre agents 
  - élimination d'un agent d'une autre équipe
  - repoussemnt d'agents de la même équipe
- collisions avec les murs
- prise des drapeaux par un agent
- réapparition d'un agent à un point d'apparition de l'équipe

### Map -> Damien

- Création de fichier représentant une carte du jeu
- Création d'une carte à partir d'un fichier
- Création des drapeaux de chaque camp

### Agent -> Grégoire

- implementation Class Agent

## Vue -> Tibère

- implémentation MVC (voir Graphe de scène.png)
- vue affichage simulation
- squelette menu configuration

## Autre -> Grégoire

- systeme de log
