package ia.ecj.statistics;

import ec.Individual;

public interface CTF_CMAES_StatListener {
    void postEvaluationStatistics(CTF_CMAES_Statistics.Stats[] stats);
    void finalStatistics(Individual[] bestOfRun, Individual[] bestOfLastRun);
}
