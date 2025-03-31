package display.controllers;

import javafx.scene.control.Spinner;
import javafx.scene.layout.Region;

//Classe static pour ajouter un vérificateur sur les Spinner
public class SpinnerVerification {
    public static void addNumericValidationToSpinnerInteger(Spinner<Integer> spinner) {
        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinner.getEditor().setText(oldValue);
            } else {
                try {
                    int value = Integer.parseInt(newValue);
                    spinner.getValueFactory().setValue(value);
                } catch (NumberFormatException e) {
                    System.out.println("TODO");
                }
            }
        });
        spinner.setMinWidth(Region.USE_PREF_SIZE);
    }

    public static void addFocusValidationToSpinnerInteger(Spinner<Integer> spinner) {
        // Écouteur sur la propriété de focus de l'éditeur
        spinner.getEditor().focusedProperty().addListener((observable, oldFocused, newFocused) -> {
            if (!newFocused) {
                String text = spinner.getEditor().getText();

                if (text.isEmpty() || !text.matches("\\d*")) {
                    spinner.getEditor().setText("1");
                    spinner.getValueFactory().setValue(1);
                } else {
                    try {
                        int value = Integer.parseInt(text);
                        spinner.getValueFactory().setValue(value);
                    } catch (NumberFormatException e) {
                        spinner.getEditor().setText("1");
                        spinner.getValueFactory().setValue(1);
                    }
                }
            }
        });
    }

    public static void addNumericValidationToSpinnerDouble(Spinner<Double> spinner) {
        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinner.getEditor().setText(oldValue);
            } else {
                try {
                    double value = Double.parseDouble(newValue);
                    spinner.getValueFactory().setValue(value);
                } catch (NumberFormatException e) {
                    System.out.println("TODO");
                }
            }
        });
    }

    public static void addFocusValidationToSpinnerDouble(Spinner<Double> spinner) {
        // Écouteur sur la propriété de focus de l'éditeur
        spinner.getEditor().focusedProperty().addListener((observable, oldFocused, newFocused) -> {
            if (!newFocused) {
                String text = spinner.getEditor().getText();

                if (text.isEmpty() || !text.matches("\\d*")) {
                    spinner.getEditor().setText("1");
                    spinner.getValueFactory().setValue(1.0);
                } else {
                    try {
                        double value = Double.parseDouble(text);
                        spinner.getValueFactory().setValue(value);
                    } catch (NumberFormatException e) {
                        spinner.getEditor().setText("1");
                        spinner.getValueFactory().setValue(1.0);
                    }
                }
            }
        });
    }
}
