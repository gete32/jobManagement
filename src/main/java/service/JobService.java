package service;

import exception.JobException;
import exception.PriorityException;
import service.job.Job;
import service.job.JobResult;
import service.job.Prioritized;

import java.util.List;

public interface JobService {

    void shutdown();

    List<Runnable> shutdownNow();

    JobResult submit(final Job job) throws JobException;

    JobResult submit(Prioritized job) throws JobException, PriorityException;

    List<JobResult> submitAll(List<Prioritized> jobs) throws JobException, PriorityException;

    void pause();

    void resume();
}
