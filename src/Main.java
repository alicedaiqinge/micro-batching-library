import com.xxx.library.BatchProcessor;
import com.xxx.library.Job;
import com.xxx.library.model.JobResult;
import com.xxx.library.MicroBatchingLibrary;

import java.util.ArrayList;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        BatchProcessor batchProcessor = jobs -> {
            List<JobResult> results = new ArrayList<>();
            for (Job job : jobs) {
                System.out.println("Processing job: " + job.getJobId());
                results.add(new JobResult(job.getJobId(), true));
            }
            return results;
        };

        MicroBatchingLibrary library = new MicroBatchingLibrary(batchProcessor, 5, 2000);

        // Submit 10 jobs
        for (int i = 0; i < 10; i++) {
            Job job = new Job("Job-" + i);
            library.submitJob(job).thenAccept(result -> {
                System.out.println("Job " + result.getJobId() + " completed with success: " + result.isSuccess());
            });
        }

        // Shutdown the library after some delay
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        library.shutdown();
    }
}