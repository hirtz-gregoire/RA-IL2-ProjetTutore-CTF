# Fonctionnalites CU

Nous avons développé les cas d'utilisations suivants :

## Simulation

### Moteur -> Evan

- Limite de durée de simulation

## IA

### Model de réseau de neurone -> Adrien & Tibère

- Chargement d'un modèle de RN -> Adrien & Tibère
- Ajout MLP + insertion et récupération des poids -> Tibère

### Création de partie et sauvegarde -> Tibère & Grégoire

- Ajout du choix d'un RN pour créer une nouvelle partie -> Tibère
- Restauration de la sauvgarde d'une partie -> Grégoire

## Apprentissage -> Damien & Adrien & Tibère

- Menu de création d'un apprentissage (choix des paramètres, perceptions, forme du RN) -> Tibère
- Mise en place de ECJ (problème d'évolution, boucle) -> Damien
- Fonction d'évaluation d'une partie -> Adrien

## Editeur de carte -> Tibère

- Création d'une nouvelle carte avec choix de la taille et du nombre d'équipes
- Modification d'une partie existante
- Vérificateur de la validité d'une carte

## Correction de bugs & Refactoring & Ajouts mineurs -> Evan & Adrien & Grégoire

- Crash de la partie, accès concurrentiel de plusieurs threads -> Adrien
- Pouvoir mettre seed négative quand on crééer une partie -> Evan
- TPS Overflow (INTEGER MAX VALUE) -> Evan
- Changer la seed ne change pas la seed -> Evan
- Mise à jour de l'interface graphique -> Grégoire
- Problème random, quand restart la partie n'est pas la même -> Adrien & Evan
- Fin de partie -> Evan
- Les flags peuvent aller "out of bounds" -> Adrien

## Tests -> Adrien & Damien

- Test ONXX Runtime
- Test ECJ (lecture de la doc et tutoriels)
