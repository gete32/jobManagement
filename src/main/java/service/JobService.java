package service;

import constants.PriorityEnum;
import exception.JobException;
import exception.PriorityException;

import java.util.Collection;
import java.util.List;

public interface JobService {

    void shutdown();

    List<Runnable> shutdownNow();

    InternalJob submit(final Job job) throws JobException;

    InternalJob submit(Job job, PriorityEnum priority) throws JobException, PriorityException;

    List<InternalJob> submitAll(Collection<Job> jobs) throws JobException;

    void pause();

    void resume();
}
