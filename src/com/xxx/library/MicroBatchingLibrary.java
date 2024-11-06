package com.xxx.library;

import com.xxx.library.model.JobResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MicroBatchingLibrary {
    private final BatchProcessor batchProcessor;
    private final int batchSize;
    private final long batchFrequencyMillis;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService jobExecutor;
    private final List<Job> jobQueue;
    private final List<CompletableFuture<JobResult>> resultFutures;

    private final Object lock = new Object();
    private volatile boolean isShutdown = false;

    /**
     * Constructor to initialize micro-batching library with batch size, frequency, and processor.
     */
    public MicroBatchingLibrary(BatchProcessor batchProcessor, int batchSize, long batchFrequencyMillis) {
        this.batchProcessor = batchProcessor;
        this.batchSize = batchSize;
        this.batchFrequencyMillis = batchFrequencyMillis;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.jobExecutor = Executors.newCachedThreadPool();
        this.jobQueue = new ArrayList<>();
        this.resultFutures = new ArrayList<>();

        startBatchProcessor();
    }

    /**
     * Submit a single job to the library.
     */
    public CompletableFuture<JobResult> submitJob(Job job) {
        if (isShutdown) throw new IllegalStateException("Library is shutdown, no more jobs can be submitted");

        CompletableFuture<JobResult> futureResult = new CompletableFuture<>();
        synchronized (lock) {
            jobQueue.add(job);
            resultFutures.add(futureResult);
            if (jobQueue.size() >= batchSize) {
                processBatch();
            }
        }
        return futureResult;
    }

    /**
     * Start the batch processor to process jobs at fixed frequency.
     */
    private void startBatchProcessor() {
        scheduler.scheduleAtFixedRate(this::processBatch, batchFrequencyMillis, batchFrequencyMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Process a batch of jobs if there are jobs in the queue.
     */
    private void processBatch() {
        List<Job> jobsToProcess;
        List<CompletableFuture<JobResult>> futuresToComplete;

        synchronized (lock) {
            if (jobQueue.isEmpty()) return;

            jobsToProcess = new ArrayList<>(jobQueue);
            futuresToComplete = new ArrayList<>(resultFutures);
            jobQueue.clear();
            resultFutures.clear();
        }

        jobExecutor.submit(() -> {
            List<JobResult> results = batchProcessor.process(jobsToProcess);
            for (int i = 0; i < results.size(); i++) {
                futuresToComplete.get(i).complete(results.get(i));
            }
        });
    }

    /**
     * Shutdown the micro-batching library, processing all pending jobs before closing.
     */
    public void shutdown() {
        isShutdown = true;
        scheduler.shutdown();
        processBatch();
        jobExecutor.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) scheduler.shutdownNow();
            if (!jobExecutor.awaitTermination(5, TimeUnit.SECONDS)) jobExecutor.shutdownNow();
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            jobExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}