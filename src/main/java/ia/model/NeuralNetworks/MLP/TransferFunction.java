package ia.model.NeuralNetworks.MLP;

import java.io.Serializable;

public interface TransferFunction extends Serializable {
    /**
     * Function de transfert
     *
     * @param value entrée
     * @return sortie de la fonction sur l'entrée
     */
    double evaluate(double value);

    /**
     * Dérivée de la fonction de tranfert
     *
     * @param value entrée
     * @return sortie de la fonction dérivée sur l'entrée
     */
    double evaluateDer(double value);
}