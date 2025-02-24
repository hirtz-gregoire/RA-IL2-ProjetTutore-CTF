package ia.ecj.statistics;

public interface CTF_CMAES_StatListener {
    void postEvaluationStatistics(CTF_CMAES_Statistics.Stats[] stats);
    void finalStatistics(CTF_CMAES_Statistics.Stats[] stats);
}
