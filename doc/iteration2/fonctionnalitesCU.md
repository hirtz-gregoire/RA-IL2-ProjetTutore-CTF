# Fonctionnalites CU

Nous avons développé les cas d'utilisations suivants :
## Simulation

### Moteur -> Adrien & Evan & Damien

- Gestion de la zone interdite autour d'un drapeau
- Gestion des spawn des objets

### Création de partie -> Tibère/Grégoire

- Création du menu de sélecteur de carte
- Création d'un menu pour choisir les paramètres de la partie, la seed et les modèles des agents

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
    - stockage les parties (seed, carte, modèles des agents, paramètres de la partie)
    - stockage les cartes ()
    - stockage des modèles des agents (type de modèle, paramètres du modèle)

### Système de seed -> Grégoire

- modifications des processus aléatoire :
  - shuffle des agents pour appliquer les actions
  - décision aléatoire des agents
- cela permet d'avoir un programme déterministe selon la seed malgré les méthodes "aléatoire"

## Sauvegarder/Charger une partie -> Tibère/Grégoire

- Sauvegarder une partie et ses informations (seed, carte, nombre d'agents, paramètres)
- Charger une partie à partir d'un fichier

## Correction de bugs -> Evan & Damien

- L'interface inférieure poussée par les agents lorsqu'ils sont trop proche du bas de la carte
- Sortie (puis réapparition) des agents hors de la carte
- Les agents sont poussés en permanence par le drapeau de leur équipe dès leur apparition
