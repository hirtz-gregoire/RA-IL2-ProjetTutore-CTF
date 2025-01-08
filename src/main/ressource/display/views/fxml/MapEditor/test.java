public void actualiser(ModeleMVC modeleMVC) throws Exception {
    this.getChildren().clear();

    if (modeleMVC.getVue().equals(ViewsEnum.MapParametersChoice)) {
        ControlerVue controlerVue = new ControlerVue(modeleMVC);
        VBox vBoxParametres = new VBox();

        //Choix taille de la carte
        //Nombre de lignes (hauteur)
        Slider heightSlider = new Slider(1, 50, modeleMVC.getHeightCarte());
        heightSlider.setMajorTickUnit(1);         // Espacement entre les ticks principaux
        heightSlider.setMinorTickCount(0);        // Pas de ticks intermédiaires
        heightSlider.setSnapToTicks(true);        // Alignement sur les ticks
        heightSlider.setShowTickMarks(true);      // Afficher les ticks
        heightSlider.setShowTickLabels(true);     // Afficher les labels
        Label heightText = new Label("Nombre de lignes (hauteur) :");
        Label heightValue = new Label(Double.toString(heightSlider.getValue()));
        heightSlider.valueProperty().addListener((
                ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) -> {
            heightValue.setText(String.format("%.2f", new_val));
            modeleMVC.setHeightCarte(new_val.intValue());
            setNbEquipesMax(modeleMVC);
        });
        //Nombre de colonnes (largeur)
        Slider widthSlider = new Slider(1, 50, modeleMVC.getWidthCarte());
        widthSlider.setMajorTickUnit(1);         // Espacement entre les ticks principaux
        widthSlider.setMinorTickCount(0);        // Pas de ticks intermédiaires
        widthSlider.setSnapToTicks(true);        // Alignement sur les ticks
        widthSlider.setShowTickMarks(true);      // Afficher les ticks
        widthSlider.setShowTickLabels(true);     // Afficher les labels
        Label widthText = new Label("Nombre de colonnes (largeur) :");
        Label widthValue = new Label(Double.toString(widthSlider.getValue()));
        widthSlider.valueProperty().addListener((
                ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) -> {
            widthValue.setText(String.format("%.2f", new_val));
            modeleMVC.setWidthCarte(new_val.intValue());
            setNbEquipesMax(modeleMVC);
        });

        //Choix Nombre de joueurs
        nombreEquipesSlider = new Slider(1, 8, modeleMVC.getNbEquipes());
        nombreEquipesSlider.setMajorTickUnit(1);         // Espacement entre les ticks principaux
        nombreEquipesSlider.setMinorTickCount(0);        // Pas de ticks intermédiaires
        nombreEquipesSlider.setSnapToTicks(true);        // Alignement sur les ticks
        nombreEquipesSlider.setShowTickMarks(true);      // Afficher les ticks
        nombreEquipesSlider.setShowTickLabels(true);     // Afficher les labels
        Label nombreEquipesText = new Label("Nombre de joueurs :");
        nombreEquipesValue = new Label(Double.toString(nombreEquipesSlider.getValue()));
        nombreEquipesSlider.valueProperty().addListener((
                ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) -> {
            nombreEquipesValue.setText(String.format("%.2f", new_val));
            modeleMVC.setNbEquipes(new_val.intValue());
        });

        //Boutton pour créer la carte
        Button buttonCreateCarte = new Button("Créer Carte");
        buttonCreateCarte.setOnMouseClicked(controlerVue);

        vBoxParametres.getChildren().addAll(heightText, heightSlider, heightValue,
                widthText, widthSlider, widthValue,
                nombreEquipesText, nombreEquipesSlider, nombreEquipesValue,
                buttonCreateCarte);

        this.getChildren().add(vBoxParametres);

    }
}

//Fonction qui adapte le nombre d'équipes maximal à la taille de la carte
private void setNbEquipesMax(ModeleMVC modele) {
    int height = modele.getHeightCarte();
    int width = modele.getWidthCarte();
    int taille = height * width;
    int nbEquipesMax = Math.min(modele.getNbEquipesMax(), Math.round(taille/2));

    nombreEquipesSlider.setMax(nbEquipesMax);
    if (modele.getNbEquipes() > nbEquipesMax) {
        nombreEquipesSlider.setValue(nbEquipesMax);
        nombreEquipesValue.setText(String.format("%.2f", nbEquipesMax));
    }
}