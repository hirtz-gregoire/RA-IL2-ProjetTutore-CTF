package ia.ecj.statistics;

import ec.Individual;

public class Stats implements CTF_CMAES_StatListener {
    private 

    @Override
    public void postEvaluationStatistics(CTF_CMAES_Statistics.Stats[] stats) {
        System.out.println(stats.length);
    }

    @Override
    public void finalStatistics(Individual[] bestOfRun, Individual[] bestOfLastRun) {
        CTF_CMAES_Statistics.removeListener(this);
    }
}
