import com.xxx.library.BatchProcessor;
import com.xxx.library.model.Job;
import com.xxx.library.MicroBatchingLibrary;
import com.xxx.library.model.JobResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MicroBatchingLibraryTest {
    private MicroBatchingLibrary library;
    private MockBatchProcessor batchProcessor;

    @BeforeEach
    void setUp() {
        batchProcessor = new MockBatchProcessor();
        library = new MicroBatchingLibrary(batchProcessor, 3, 1000);
    }

    @AfterEach
    void tearDown() {
        library.shutdown();
    }

    @Test
    void testSubmitSingleJob() throws ExecutionException, InterruptedException, TimeoutException {
        Job job = new Job("Job-1");
        CompletableFuture<JobResult> resultFuture = library.submitJob(job);
        JobResult result = resultFuture.get(2, TimeUnit.SECONDS);
        assertEquals("Job-1", result.getJobId());
        assertTrue(result.isSuccess());
    }

    @Test
    void testBatchProcessingWithSizeLimit() throws InterruptedException {
        Job job1 = new Job("Job-1");
        Job job2 = new Job("Job-2");
        Job job3 = new Job("Job-3");
        library.submitJob(job1);
        library.submitJob(job2);
        library.submitJob(job3);
        TimeUnit.SECONDS.sleep(2);
        assertEquals(1, batchProcessor.getProcessedBatchCount());
    }

    @Test
    void testGracefulShutdown() {
        Job job = new Job("Job-1");
        library.submitJob(job);
        library.shutdown();
        assertEquals(1, batchProcessor.getProcessedBatchCount());
    }
}

class MockBatchProcessor implements BatchProcessor {
    private final List<List<Job>> processedBatches = new ArrayList<>();

    @Override
    public List<JobResult> process(List<Job> jobs) {
        processedBatches.add(jobs);
        List<JobResult> results = new ArrayList<>();
        for (Job job : jobs) {
            results.add(new JobResult(job.getJobId(), true));
        }
        return results;
    }

    public int getProcessedBatchCount() {
        return processedBatches.size();
    }
}
