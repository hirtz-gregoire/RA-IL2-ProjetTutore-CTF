package ia.model.NeuralNetworks;

import ia.perception.Perception;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NNFileLoader {
    /**
     * A USABLE NETWORK FOR THE CTF PROJECT IS DEFINED AS FOLLOW :
     *
     * fileName : name.CTF
     * content (don't forget line breaks) :
     *
     * PerceptionName param1 param2 param3 <- the different perceptions used by the model, params depend on the perceptions
     * PerceptionName param1 param2 param3 param4
     * PerceptionName param1 param2
     * [etc]
     *
     * anyName.anyExtension <- this is the file containing the weights of the network (depend on the type of network, detected via the file extension)
     */

    private static final int STATE_PERCEPTION = 0;
    private static final int STATE_FILE = 1;
    public static ModelNeuralNetwork loadModel(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);

        List<Perception> perceptions = new ArrayList<Perception>();

        int state = STATE_PERCEPTION;
        String line = bufferedReader.readLine();
        while (line != null) {
            String[] tokens = line.split(" ");

            if(state == STATE_PERCEPTION) {

            }
            else {

            }
            line = bufferedReader.readLine();
        }

        return null;
    }

    private static Perception perceptionBuilder(String[] tokens) {
        
    }
}
