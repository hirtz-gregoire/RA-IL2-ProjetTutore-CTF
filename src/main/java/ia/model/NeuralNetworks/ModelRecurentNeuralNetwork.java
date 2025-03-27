package ia.model.NeuralNetworks;

import engine.Engine;
import engine.agent.Action;
import engine.agent.Agent;
import engine.map.GameMap;
import engine.object.GameObject;
import ia.perception.Perception;

import java.util.List;

public class ModelRecurentNeuralNetwork extends ModelNeuralNetwork{
    private final double[] memory;

    private final int additionnalMemorySize;


    public ModelRecurentNeuralNetwork(NeuralNetwork neuralNetwork, List<Perception> perceptions, int memorySize) {
        super(neuralNetwork, perceptions);
        additionnalMemorySize = memorySize;
        this.memory = new double[memorySize+2];
        this.numberOfInputs = getNumberOfInputsMLP();
    }

    @Override
    public double[] getAllPerceptionsValuesNormalise() {
        double[] allPerceptions = super.getAllPerceptionsValuesNormalise();
        int size = allPerceptions.length;
        int index = 0;
        for (int i = size - memory.length; i < size; i++) {
            allPerceptions[i] = memory[index];
            index++;
        }
        return allPerceptions;
    }

    @Override
    public Action getAction(Engine engine, GameMap map, List<Agent> agents, List<GameObject> objects) {
        Action action = super.getAction(engine, map, agents, objects);
        System.arraycopy(outputs, 0, memory, 0, outputs.length);
        return action;
    }

    @Override
    public int getNumberOfInputsMLP() {
        if(memory == null) {
            return super.getNumberOfInputsMLP();
        }
        return super.getNumberOfInputsMLP()+additionnalMemorySize;
    }

    public int getMemorySize() {
        return memory.length;
    }
}
