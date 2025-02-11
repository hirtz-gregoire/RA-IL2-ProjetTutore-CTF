package engine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugRandom extends Random {
    private final Random delegate;
    private long currentSeed = -1;
    private static AtomicInteger seedCount = new AtomicInteger(0);
    private PrintWriter logWriter;

    public DebugRandom(long seed) {
        this.delegate = new Random(seed);
        updateLogFile(seed);
    }

    public DebugRandom() {
        this.delegate = new Random();
        updateLogFile(currentSeed);
    }

    private void updateLogFile(long seed) {
        this.currentSeed = seed;
        int count = seedCount.incrementAndGet();
        String filename = "ressources/rnd_logs/" + seed + "_" + count + ".txt";

        // Close previous log file if open
        if (logWriter != null) {
            logWriter.close();
        }

        try {
            logWriter = new PrintWriter(new FileWriter(filename, true));
            logWriter.printf("=== New Log File for Seed: %d (Iteration %d) ===%n", seed, count);
            logWriter.flush();
        } catch (IOException e) {
            System.err.println("Failed to open log file: " + e.getMessage());
        }
    }

    private void logCall(String method, Object result, Object... args) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3]; // Get the direct caller
        String logEntry = String.format("Random call: %s(%s) -> %s at %s:%d%n",
                method, (args.length > 0 ? args[0] : ""), result, caller.getClassName(), caller.getLineNumber());

        System.out.print(logEntry); // Print to console (optional)
        if (logWriter != null) {
            logWriter.print(logEntry);
            logWriter.flush();
        }
    }

    @Override
    public synchronized void setSeed(long seed) {
        if(delegate != null) {
            delegate.setSeed(seed);
            updateLogFile(seed);
        }
    }

    @Override
    public int nextInt(int bound) {
        int result = delegate.nextInt(bound);
        logCall("nextInt", result, bound);
        return result;
    }

    @Override
    public double nextDouble() {
        double result = delegate.nextDouble();
        logCall("nextDouble", result);
        return result;
    }

    @Override
    public boolean nextBoolean() {
        boolean result = delegate.nextBoolean();
        logCall("nextBoolean", result);
        return result;
    }

    @Override
    public float nextFloat() {
        float result = delegate.nextFloat();
        logCall("nextFloat", result);
        return result;
    }

    @Override
    public long nextLong() {
        long result = delegate.nextLong();
        logCall("nextLong", result);
        return result;
    }

    @Override
    public synchronized void nextBytes(byte[] bytes) {
        delegate.nextBytes(bytes);
        logCall("nextBytes", bytes);
    }

    public void close() {
        if (logWriter != null) {
            logWriter.close();
        }
    }
}
