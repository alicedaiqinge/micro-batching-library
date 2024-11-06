package com.xxx.library;
import com.xxx.library.model.JobResult;

import java.util.List;

public interface BatchProcessor {
    List<JobResult> process(List<Job> jobs);
}
