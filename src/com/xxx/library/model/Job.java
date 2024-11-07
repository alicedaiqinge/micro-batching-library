package com.xxx.library.model;

import com.xxx.library.model.JobResult;

public class Job {
    private final String jobId;

    public Job(String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }
}