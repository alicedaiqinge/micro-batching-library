package com.xxx.library.model;
public class JobResult {
    private final String jobId;
    private final boolean success;

    public JobResult(String jobId, boolean success) {
        this.jobId = jobId;
        this.success = success;
    }

    // Getter for jobId
    public String getJobId() {
        return jobId;
    }

    // Getter for success
    public boolean isSuccess() {
        return success;
    }
}
