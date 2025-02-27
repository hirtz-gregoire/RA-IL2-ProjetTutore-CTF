# Fonctionnalites CU

Nous avons développé les cas d'utilisations suivants :

## Engine

### Agent humain -> Evan & Adrien

- Permettre à un agent humain de jouer une partie avec et contre des robots.

## IA

### Model de réseau de neurone -> Adrien & Damien

- Test de DL4J (Deep Learning for Java), modèle de réseau de neurones : </br>
-> DL4J n'est pas adapté à notre cas d'utilisation et est trop lent
- Réglage de problèmes d'accès concurrents lors de l'apprentissage

### Statistiques -> Adrien & Tibère
- Extraction des statistiques de ECJ
- Affichage des statistiques

## Editeur de carte -> Tibère

- Résolution des nombreux bugs (taille de carte, chargement de carte, vérificateur de la validité d'une carte)
- Vérificateur de la validité d'une carte (Implémentation de l'algo A*)

## Correction de bugs & Refactoring & Ajouts mineurs -> Damien & Tibère

- Refactoring des perceptions -> Damien
- Chargement et sauvegarde de partie -> Tibère

## Interface Application -> Tibère & Grégoire

- Ajout de scrollPane pour pouvoir tout afficher -> Tibère
- Ajout bouton accessible de partout pour retourner au menu principal -> Grégoire
- Ajout de choix de paramètres pour l'apprentissage (nombre de générations + nom du modèle + fonction d'activation) -> Tibère
