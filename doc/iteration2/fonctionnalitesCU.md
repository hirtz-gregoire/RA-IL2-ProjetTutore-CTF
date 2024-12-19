# Fonctionnalites CU

Nous avons développé les cas d'utilisations suivants :
## Simulation

### Moteur -> Adrien & Evan & Damien

- Gestion de la zone interdite autour d'un drapeau
- Gestion des spawn des objets

### Map -> Tibère/Grégoire

- Refonte du menu de sélecteur de carte

### Modele et agent -> Adrien & Damien & Evan

- Modèle d'arbre de décision
- Modèle Raycast
- Perceptions :
  - Arbre de décision
  - Raycast

## Sauvegarde des parties -> Tibère/Grégoire
  - mise en place d'un bouton pour sauvegarder les paramètres et la seed
  - stockage en fichier txt

### Structure des fichiers -> Tibère/Grégoire
  - utilisation des fichiers txt pour
    - stocker la seed
    - le nombre d'equipe
    - les modèles des agents
    - le nb d'agents
    - etc

### Système de seed -> Grégoire

- modifications des processus aléatoire :
  - shuffle des agents pour appliquer les actions
  - décision aléatoire des agents
- cela permet d'avoir un programme déterministe selon la seed malgré les méthodes "aléatoire"

## Sauver/Charger d'une partie -> Grégoire

- Sauvegarder une partie et ses informations (seed, carte, nombre d'agents, paramètres)
- Charger une partie à partir d'un fichier

## Correction de bugs -> Evan & Damien

- L'interface inférieure poussée par les agents lorsqu'ils sont trop proche du bas de la carte
- Sortie (puis réapparition) des agents hors de la carte
- Les agents sont poussés en permanence par le drapeau de leur équipe dès leur apparition
