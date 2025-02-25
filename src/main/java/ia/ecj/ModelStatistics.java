package ia.ecj;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleStatistics;

public class ModelStatistics extends SimpleStatistics {
    public static final String P_STATISTICS_FILE = "file";
    public static final String P_COMPRESS = "gzip";
    public static final String P_DO_FINAL = "do-final";
    public static final String P_DO_GENERATION = "do-generation";
    public static final String P_DO_MESSAGE = "do-message";
    public static final String P_DO_DESCRIPTION = "do-description";
    public static final String P_DO_PER_GENERATION_DESCRIPTION = "do-per-generation-description";
    public int statisticslog = 0;
    public Individual[] best_of_run = null;
    public boolean compress;
    public boolean doFinal;
    public boolean doGeneration;
    public boolean doMessage;
    public boolean doDescription;
    public boolean doPerGenerationDescription;
    boolean warned = false;

    @Override
    public void postEvaluationStatistics(EvolutionState state) {
        super.postEvaluationStatistics(state);
        Individual[] best_i = new Individual[state.population.subpops.size()];

        int x;
        for(x = 0; x < state.population.subpops.size(); ++x) {
            best_i[x] = (Individual)((Subpopulation)state.population.subpops.get(x)).individuals.get(0);

            for(int y = 1; y < ((Subpopulation)state.population.subpops.get(x)).individuals.size(); ++y) {
                if (((Subpopulation)state.population.subpops.get(x)).individuals.get(y) == null) {
                    if (!this.warned) {
                        state.output.warnOnce("Null individuals found in subpopulation");
                        this.warned = true;
                    }
                } else if (best_i[x] == null || ((Individual)((Subpopulation)state.population.subpops.get(x)).individuals.get(y)).fitness.betterThan(best_i[x].fitness)) {
                    best_i[x] = (Individual)((Subpopulation)state.population.subpops.get(x)).individuals.get(y);
                }

                if (best_i[x] == null && !this.warned) {
                    state.output.warnOnce("Null individuals found in subpopulation");
                    this.warned = true;
                }
            }

            if (this.best_of_run[x] == null || best_i[x].fitness.betterThan(this.best_of_run[x].fitness)) {
                this.best_of_run[x] = (Individual)best_i[x].clone();
            }
        }

        if (this.doGeneration) {
            state.output.println("\nGeneration: " + state.generation, this.statisticslog);
        }

        if (this.doGeneration) {
            state.output.println("Best Individual:", this.statisticslog);
        }

        for(x = 0; x < state.population.subpops.size(); ++x) {
            if (this.doGeneration) {
                state.output.println("Subpopulation " + x + ":", this.statisticslog);
            }

            if (this.doGeneration) {
                best_i[x].printIndividualForHumans(state, this.statisticslog);
            }

            if (this.doMessage && !this.silentPrint) {
                state.output.message("Subpop " + x + " best fitness of generation" + (best_i[x].evaluated ? " " : " (evaluated flag not set): ") + best_i[x].fitness.fitnessToStringForHumans());
            }

            if (this.doGeneration && this.doPerGenerationDescription && state.evaluator.p_problem instanceof SimpleProblemForm) {
                ((SimpleProblemForm)state.evaluator.p_problem.clone()).describe(state, best_i[x], x, 0, this.statisticslog);
            }
        }
    }
}
