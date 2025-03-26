package display.views.Learning;

import ec.EvolutionState;
import ec.util.Checkpoint;
import ec.util.MersenneTwisterFast;
import ec.util.Output;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
import ec.util.Version;
import java.io.File;
import java.io.PrintWriter;

public class ECJ_Evolve {
    public static final String P_PRINTACCESSEDPARAMETERS = "print-accessed-params";
    public static final String P_PRINTUSEDPARAMETERS = "print-used-params";
    public static final String P_PRINTALLPARAMETERS = "print-all-params";
    public static final String P_PRINTUNUSEDPARAMETERS = "print-unused-params";
    public static final String P_PRINTUNACCESSEDPARAMETERS = "print-unaccessed-params";
    public static final String A_CHECKPOINT = "-checkpoint";
    public static final String A_FILE = "-file";
    public static final String A_FROM = "-from";
    public static final String A_AT = "-at";
    public static final String A_HELP = "-help";
    public static final String P_EVALTHREADS = "evalthreads";
    public static final String P_BREEDTHREADS = "breedthreads";
    public static final String P_SEED = "seed";
    public static final String V_SEED_TIME = "time";
    public static final String P_STATE = "state";
    public static final String V_THREADS_AUTO = "auto";
    public static final String P_SILENT = "silent";
    static final String P_MUZZLE = "muzzle";

    public ECJ_Evolve() {
    }

    public static void checkForHelp(String[] args) {
        for(int x = 0; x < args.length; ++x) {
            if (args[x].equals("-help")) {
                Output.initialMessage(Version.message());
                Output.initialMessage("Format:\n\n    java ec.Evolve -file FILE [-p PARAM=VALUE] [-p PARAM=VALUE] ...\n    java ec.Evolve -from FILE [-p PARAM=VALUE] [-p PARAM=VALUE] ...\n    java ec.Evolve -from FILE -at CLASS [-p PARAM=VALUE] [-p PARAM=VALUE] ...\n    java ec.Evolve -checkpoint CHECKPOINT\n    java ec.Evolve -help\n\n-help                   Shows this message and exits.\n\n-file FILE              Launches ECJ using the provided parameter FILE.\n\n-from FILE              Launches ECJ using the provided parameter FILE\n                        which is defined relative to the directory\n                        holding the classfile ec/Evolve.class  If this\n                        class file is found inside a Jar file, then the\n                        FILE will also be assumed to be in that Jar file,\n                        at the proper relative location.\n\n-from FILE -at CLASS    Launches ECJ using the provided parameter FILE\n                        which is defined relative to the directory\n                        holding the classfile CLASS (for example,\n                        ec/ant/ant.class).  If this class file is found\n                        inside a Jar file, then the FILE will also be\n                        assumed to be in that Jar file, at the proper\n                        relative location.\n\n-p PARAM=VALUE          Overrides the parameter PARAM in the parameter\n                        file, setting it to the value VALUE instead.  You\n                        can override as many parameters as you like on\n                        the command line.\n\n-checkpoint CHECKPOINT  Launches ECJ from the provided CHECKPOINT file.\n");
                System.exit(1);
            }
        }

    }

    public static EvolutionState possiblyRestoreFromCheckpoint(String[] args) {
        for(int x = 0; x < args.length - 1; ++x) {
            if (args[x].equals("-checkpoint")) {
                Output.initialMessage("Restoring from Checkpoint " + args[x + 1]);

                try {
                    return Checkpoint.restoreFromCheckpoint(args[x + 1]);
                } catch (Exception var3) {
                    Exception e = var3;
                    Output.initialError("An exception was generated upon starting up from a checkpoint.\nFor help, try:  java ec.Evolve -help\n\n" + String.valueOf(e));
                }
            }
        }

        return null;
    }

    public static ParameterDatabase loadParameterDatabase(String[] args) {
        ParameterDatabase parameters = null;

        for(int x = 0; x < args.length - 1; ++x) {
            if (args[x].equals("-file")) {
                try {
                    parameters = new ParameterDatabase(new File((new File(args[x + 1])).getAbsolutePath()), args);
                    break;
                } catch (Exception var7) {
                    Exception e = var7;
                    e.printStackTrace();
                    Output.initialError("An exception was generated upon reading the parameter file \"" + args[x + 1] + "\".\nHere it is:\n" + String.valueOf(e));
                }
            }
        }

        Class cls = null;

        Exception e;
        int x;
        for(x = 0; x < args.length - 1; ++x) {
            if (args[x].equals("-at")) {
                try {
                    if (parameters != null) {
                        Output.initialError("Both -from and -at arguments provided.  This is not permitted.\nFor help, try:  java ec.Evolve -help");
                    } else {
                        cls = Class.forName(args[x + 1]);
                    }
                    break;
                } catch (Exception var6) {
                    e = var6;
                    e.printStackTrace();
                    Output.initialError("An exception was generated upon extracting the class to load the parameter file relative to: " + args[x + 1] + "\nFor help, try:  java ec.Evolve -help\n\n" + String.valueOf(e));
                }
            }
        }

        for(x = 0; x < args.length - 1; ++x) {
            if (args[x].equals("-from")) {
                try {
                    if (parameters != null) {
                        Output.initialError("Both -file and -from arguments provided.  This is not permitted.\nFor help, try:  java ec.Evolve -help");
                    } else {
                        if (cls == null) {
                            cls = ec.Evolve.class;
                        }

                        parameters = new ParameterDatabase(args[x + 1], cls, args);
                        Output.initialMessage("Using database resource location " + parameters.getLabel());
                    }
                    break;
                } catch (Exception var5) {
                    e = var5;
                    e.printStackTrace();
                    Output.initialError("The parameter file is missing at the resource location: " + args[x + 1] + " relative to the class: " + String.valueOf(cls) + "\n\nFor help, try:  java ec.Evolve -help");
                }
            }
        }

        if (parameters == null) {
            Output.initialError("No parameter or checkpoint file was specified.\nFor help, try:   java ec.Evolve -help");
        }

        return parameters;
    }

    public static int determineThreads(Output output, ParameterDatabase parameters, Parameter threadParameter) {
        int thread = 1;
        String tmp_s = parameters.getString(threadParameter, (Parameter)null);
        if (tmp_s == null) {
            output.fatal("Threads number must exist.", threadParameter, (Parameter)null);
        } else if ("auto".equalsIgnoreCase(tmp_s)) {
            Runtime runtime = Runtime.getRuntime();

            try {
                return (Integer)runtime.getClass().getMethod("availableProcessors", (Class[])null).invoke(runtime, (Object[])null);
            } catch (Exception var8) {
                output.fatal("Whoa! This Java version is too old to have the Runtime.availableProcessors() method available.\nThis means you can't use 'auto' as a threads option.", threadParameter, (Parameter)null);
            }
        } else {
            try {
                thread = parameters.getInt(threadParameter, (Parameter)null);
                if (thread <= 0) {
                    output.fatal("Threads value must be > 0", threadParameter, (Parameter)null);
                }
            } catch (NumberFormatException var7) {
                output.fatal("Invalid, non-integer threads value (" + thread + ")", threadParameter, (Parameter)null);
            }
        }

        return thread;
    }

    public static MersenneTwisterFast primeGenerator(MersenneTwisterFast generator) {
        for(int i = 0; i < 1249; ++i) {
            generator.nextInt();
        }

        return generator;
    }

    public static int determineSeed(Output output, ParameterDatabase parameters, Parameter seedParameter, long currentTime, int offset, boolean auto) {
        int seed = 1;
        String tmp_s = parameters.getString(seedParameter, (Parameter)null);
        if (tmp_s == null && !auto) {
            output.fatal("Seed must exist.", seedParameter, (Parameter)null);
        } else if ("time".equalsIgnoreCase(tmp_s) || tmp_s == null && auto) {
            if (tmp_s == null && auto) {
                output.warnOnce("Using automatic determination number of threads, but not all seeds are defined.\nThe rest will be defined using the wall clock time.");
            }

            seed = (int)currentTime;
            if (seed == 0) {
                output.fatal("Whoa! This Java version is returning 0 for System.currentTimeMillis(), which ain't right.  This means you can't use 'time' as a seed ", seedParameter, (Parameter)null);
            }
        } else {
            try {
                seed = parameters.getInt(seedParameter, (Parameter)null);
            } catch (NumberFormatException var10) {
                output.fatal("Invalid, non-integer seed value (" + seed + ")", seedParameter, (Parameter)null);
            }
        }

        return seed + offset;
    }

    public static Output buildOutput() {
        Output output = new Output(true);
        output.addLog(0, false);
        output.addLog(1, true);
        return output;
    }

    public static EvolutionState initialize(ParameterDatabase parameters, int randomSeedOffset) {
        return initialize(parameters, randomSeedOffset, buildOutput());
    }

    public static EvolutionState initialize(ParameterDatabase parameters, int randomSeedOffset, Output output) {
        EvolutionState state = null;
        if (parameters.exists(new Parameter("muzzle"), (Parameter)null)) {
            String var10001 = String.valueOf(new Parameter("muzzle"));
            output.warning(var10001 + " has been deprecated.  We suggest you use " + String.valueOf(new Parameter("silent")) + " or similar newer options.");
        }

        if (parameters.getBoolean(new Parameter("silent"), (Parameter)null, false) || parameters.getBoolean(new Parameter("muzzle"), (Parameter)null, false)) {
            output.getLog(0).silent = true;
            output.getLog(1).silent = true;
        }

        output.systemMessage(Version.message());
        int breedthreads = determineThreads(output, parameters, new Parameter("breedthreads"));
        int evalthreads = determineThreads(output, parameters, new Parameter("evalthreads"));
        boolean auto = "auto".equalsIgnoreCase(parameters.getString(new Parameter("breedthreads"), (Parameter)null)) || "auto".equalsIgnoreCase(parameters.getString(new Parameter("evalthreads"), (Parameter)null));
        MersenneTwisterFast[] random = new MersenneTwisterFast[breedthreads > evalthreads ? breedthreads : evalthreads];
        int[] seeds = new int[random.length];
        String seedMessage = "Seed: ";
        int time = (int)System.currentTimeMillis();

        for(int x = 0; x < random.length; ++x) {
            seeds[x] = determineSeed(output, parameters, (new Parameter("seed")).push("" + x), (long)(time + x), random.length * randomSeedOffset, auto);

            for(int y = 0; y < x; ++y) {
                if (seeds[x] == seeds[y]) {
                    output.fatal("seed." + x + " (" + seeds[x] + ") and seed." + y + " (" + seeds[y] + ") ought not be the same seed.", (Parameter)null, (Parameter)null);
                }
            }

            random[x] = primeGenerator(new MersenneTwisterFast((long)seeds[x]));
            seedMessage = seedMessage + seeds[x] + " ";
        }

        state = (EvolutionState)parameters.getInstanceForParameter(new Parameter("state"), (Parameter)null, EvolutionState.class);
        state.parameters = parameters;
        state.random = random;
        state.output = output;
        state.evalthreads = evalthreads;
        state.breedthreads = breedthreads;
        state.randomSeedOffset = randomSeedOffset;
        output.systemMessage("Threads:  breed/" + breedthreads + " eval/" + evalthreads);
        output.systemMessage(seedMessage);
        return state;
    }

    public static void cleanup(EvolutionState state) {
        state.output.flush();
        PrintWriter pw = new PrintWriter(System.err);
        state.parameters.getBoolean(new Parameter("print-used-params"), (Parameter)null, false);
        state.parameters.getBoolean(new Parameter("print-accessed-params"), (Parameter)null, false);
        state.parameters.getBoolean(new Parameter("print-unused-params"), (Parameter)null, false);
        state.parameters.getBoolean(new Parameter("print-unaccessed-params"), (Parameter)null, false);
        state.parameters.getBoolean(new Parameter("print-all-params"), (Parameter)null, false);
        if (state.parameters.getBoolean(new Parameter("print-used-params"), (Parameter)null, false)) {
            pw.println("\n\nUsed Parameters\n===============\n");
            state.parameters.listGotten(pw);
        }

        if (state.parameters.getBoolean(new Parameter("print-accessed-params"), (Parameter)null, false)) {
            pw.println("\n\nAccessed Parameters\n===================\n");
            state.parameters.listAccessed(pw);
        }

        if (state.parameters.getBoolean(new Parameter("print-unused-params"), (Parameter)null, false)) {
            pw.println("\n\nUnused Parameters\n================= (Ignore parent.x references) \n");
            state.parameters.listNotGotten(pw);
        }

        if (state.parameters.getBoolean(new Parameter("print-unaccessed-params"), (Parameter)null, false)) {
            pw.println("\n\nUnaccessed Parameters\n===================== (Ignore parent.x references) \n");
            state.parameters.listNotAccessed(pw);
        }

        if (state.parameters.getBoolean(new Parameter("print-all-params"), (Parameter)null, false)) {
            pw.println("\n\nAll Parameters\n==============\n");
            state.parameters.list(pw, false);
        }

        pw.flush();
        System.err.flush();
        System.out.flush();
        state.output.close();
    }

    public static void main(String[] args) {
        checkForHelp(args);
        EvolutionState state = possiblyRestoreFromCheckpoint(args);
        int currentJob = 0;
        if (state != null) {
            try {
                if (state.runtimeArguments == null) {
                    Output.initialError("Checkpoint completed from job started by foreign program (probably GUI).  Exiting...");
                }

                args = state.runtimeArguments;
                currentJob = (Integer)state.job[0] + 1;
            } catch (Exception var7) {
                Output.initialError("EvolutionState's jobs variable is not set up properly.  Exiting...");
            }

            state.run(1);
            cleanup(state);
        }

        ParameterDatabase parameters = loadParameterDatabase(args);
        if (currentJob == 0) {
            currentJob = parameters.getIntWithDefault(new Parameter("current-job"), (Parameter)null, 0);
        }

        if (currentJob < 0) {
            Output.initialError("The 'current-job' parameter must be >= 0 (or not exist, which defaults to 0)");
        }

        int numJobs = parameters.getIntWithDefault(new Parameter("jobs"), (Parameter)null, 1);
        if (numJobs < 1) {
            Output.initialError("The 'jobs' parameter must be >= 1 (or not exist, which defaults to 1)");
        }

        for(int job = currentJob; job < numJobs; ++job) {
            if (parameters == null) {
                parameters = loadParameterDatabase(args);
            }

            state = initialize(parameters, job);
            state.output.systemMessage("Job: " + job);
            state.job = new Object[1];
            state.job[0] = job;
            state.runtimeArguments = args;
            if (numJobs > 1) {
                String jobFilePrefix = "job." + job + ".";
                state.output.setFilePrefix(jobFilePrefix);
                state.checkpointPrefix = jobFilePrefix + state.checkpointPrefix;
            }

            state.run(0);
            cleanup(state);
            parameters = null;
        }
    }
}
