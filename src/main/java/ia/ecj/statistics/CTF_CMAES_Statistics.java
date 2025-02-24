package ia.ecj.statistics;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.eda.cmaes.CMAESSpecies;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import org.ejml.simple.SimpleSVD;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class CTF_CMAES_Statistics extends Statistics {

    // Stat listeners related
    public record Stats(Individual bestOfRun, Individual bestOfGen, Individual worstOfGen, double averageGenFit, double sigma, double conditionNumber) { }

    // Class related
    private Individual[] best_of_run = null;
    private static final Set<CTF_CMAES_StatListener> listeners = new LinkedHashSet<>();

    // Logs related
    public static final String P_VERBOSE_FILE = "file.verbose";
    public static final String P_CSV_FILE = "file.csv";
    public static final String P_DO_LOGS = "do-logs";
    public static final String P_DO_MESSAGE = "do-message";
    public int verboseLogs = 0;  // stdout
    public int csvLogs = -1;
    public boolean doLogs;
    public boolean doMessage;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);

        doLogs = state.parameters.getBoolean(base.push(P_DO_LOGS),null,true);
        doMessage = state.parameters.getBoolean(base.push(P_DO_MESSAGE),null,true);

        File verboseFile = state.parameters.getFile(base.push(P_VERBOSE_FILE), null);
        File csvFile = state.parameters.getFile(base.push(P_CSV_FILE), null);

        if (verboseFile != null) {
            try {
                verboseLogs = state.output.addLog(verboseFile, true);
            } catch (IOException e) {
                state.output.fatal("An IOException occurred while trying to create the log " + verboseFile + ":\n" + e);
            }
        } else
            state.output.warning("No verbose file specified, printing to stdout at end.", base.push(P_VERBOSE_FILE));

        if(csvFile != null) {
            try {
                csvLogs = state.output.addLog(csvFile, true);
                state.output.println("Generation;Subpop;Best of run;Best of gen;Worst of gen;Average;Sigma;Condition number", csvLogs);
            } catch (IOException e) {
                state.output.fatal("An IOException occurred while trying to create the log " + csvFile + ":\n" + e);
            }
        } else state.output.warning("No CSV file specified.", base.push(P_VERBOSE_FILE));
    }

    @Override
    public void postInitializationStatistics(final EvolutionState state) {
        super.postInitializationStatistics(state);

        // set up our best_of_run array -- can't do this in setup, because
        // we don't know if the number of subpopulations has been determined yet
        best_of_run = new Individual[state.population.subpops.size()];
    }

    boolean warned = false;
    @Override
    public void postEvaluationStatistics(EvolutionState state) {
        super.postEvaluationStatistics(state);

        // Individual stats for each subpops
        Stats[] stats = new Stats[state.population.subpops.size()];

        if(doLogs) {
            state.output.println("\nGeneration: " + state.generation, verboseLogs);
        }

        for (int subpopIndex = 0; subpopIndex < state.population.subpops.size(); subpopIndex++) {
            if(doLogs) {
                state.output.println("Subpopulation: " + subpopIndex, verboseLogs);
            }
            boolean warnedThisSubpop = false;

            Individual bestOfGen = state.population.subpops.get(subpopIndex).individuals.getFirst();
            Individual worstOfGen = state.population.subpops.get(subpopIndex).individuals.getFirst();
            double averageGenFit = 0;
            double sigma = -1;
            double conditionNumber = -1;

            if (bestOfGen == null) {
                state.output.warnOnce("Null individuals found in subpopulation");
                warned = true; // we do this rather than relying on warnOnce because it is much faster in a tight loop
                warnedThisSubpop = true;
            } else {
                // Done like that to have a smaller line
                averageGenFit += bestOfGen.fitness.fitness();
            }

            // Find best, worst and average fitness of population
            int individualsCount = state.population.subpops.get(subpopIndex).individuals.size();
            for (int i = 1; i < individualsCount; i++) {
                var individual = state.population.subpops.get(subpopIndex).individuals.get(i);
                if (individual == null) {
                    if (!warnedThisSubpop) {
                        state.output.warnOnce("Null individuals found in subpopulation");
                        warned = true; // we do this rather than relying on warnOnce because it is much faster in a tight loop
                        warnedThisSubpop = true;
                    }
                } else {
                    // Everything fine, compute !
                    if (best_of_run[subpopIndex] == null || individual.fitness.betterThan(best_of_run[subpopIndex].fitness)) {
                        best_of_run[subpopIndex] = individual;
                    }
                    if (bestOfGen == null || individual.fitness.betterThan(bestOfGen.fitness)) {
                        bestOfGen = individual;
                    }
                    if (worstOfGen == null || worstOfGen.fitness.betterThan(individual.fitness)) {
                        worstOfGen = individual;
                    }
                    averageGenFit += individual.fitness.fitness();
                }
            }

            if (state.population.subpops.get(subpopIndex).species instanceof CMAESSpecies cmaesSpecies) {
                SimpleSVD svd = cmaesSpecies.c.svd();
                double maxSingular = svd.getW().get(0, 0);
                double minSingular = svd.getW().get(svd.rank() - 1, svd.rank() - 1);

                conditionNumber = maxSingular / minSingular;
                sigma = cmaesSpecies.sigma;
            }

            if(individualsCount > 0) {
                averageGenFit /= individualsCount;
            }

            stats[subpopIndex] = new Stats(best_of_run[subpopIndex], bestOfGen, worstOfGen, averageGenFit, sigma, conditionNumber);

            if(doLogs && bestOfGen != null) {
                if(doMessage && verboseLogs != 0 && verboseLogs != 1) {
                    state.output.message("Subpop " + subpopIndex + " best fitness of generation" +
                            (bestOfGen.evaluated ? " " : " (evaluated flag not set): ") +
                            bestOfGen.fitness.fitness()
                    );
                    state.output.message("Average fitness: " + averageGenFit +
                                    "   Sigma: " + sigma +
                                    "   Condition number: " +
                                    conditionNumber
                    );
                    state.output.message("");
                }

                bestOfGen.printIndividualForHumans(state, verboseLogs);
                state.output.println("Subpop " + subpopIndex + " best fitness of generation" +
                        (bestOfGen.evaluated ? " " : " (evaluated flag not set): ") +
                        bestOfGen.fitness.fitnessToStringForHumans(), verboseLogs);
                state.output.println("Subpop " + subpopIndex + " worst fitness of generation" +
                        (worstOfGen.evaluated ? " " : " (evaluated flag not set): ") +
                        worstOfGen.fitness.fitnessToStringForHumans(), verboseLogs);
                state.output.println("Average fitness: " + averageGenFit +
                        "   Sigma: " + sigma +
                        "   Condition number: " +
                        conditionNumber,
                        verboseLogs);

                //"Generation;Subpop;Best of run;Best of gen;Worst of gen;Average;Sigma;Condition number", csvLogs
                if(csvLogs != -1) state.output.println(
                        state.generation+";"+
                                subpopIndex+";"+
                                best_of_run[subpopIndex].fitness.fitness()+";"+
                                bestOfGen.fitness.fitness()+";"+
                                worstOfGen.fitness.fitness()+";"+
                                averageGenFit+";"+
                                sigma+";"+
                                conditionNumber,
                        csvLogs);
            }
        }

        for(CTF_CMAES_StatListener listener : listeners) {
            listener.postEvaluationStatistics(stats);
        }
    }

    @Override
    public void finalStatistics(EvolutionState state, int result) {
        super.finalStatistics(state, result);

        if(!doLogs) return;

        state.output.println("\nBest Individual of Run:", verboseLogs);

        for (int subpopIndex = 0; subpopIndex < state.population.subpops.size(); subpopIndex++) {
            if(best_of_run[subpopIndex] != null) {
                state.output.println("Subpopulation: " + subpopIndex, verboseLogs);
                best_of_run[subpopIndex].printIndividualForHumans(state, verboseLogs);

                // finally describe the winner if there is a description
                if (state.evaluator.p_problem instanceof SimpleProblemForm)
                    ((SimpleProblemForm)(state.evaluator.p_problem.clone())).describe(state, best_of_run[subpopIndex], subpopIndex, 0, verboseLogs);
            }
            else if(!warned) {
                state.output.println("-- Run terminated prematurely, before evaluation of generation 0 --", verboseLogs);
                break;
            }
        }
    }

    public static void addListener(CTF_CMAES_StatListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(CTF_CMAES_StatListener listener) {
        listeners.remove(listener);
    }
}