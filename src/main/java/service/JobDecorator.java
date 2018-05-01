package service;

import constants.JobStatus;
import constants.Priority;
import exception.JobStatusException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import service.job.Job;
import service.job.JobResult;
import service.job.Prioritized;

import java.util.concurrent.Future;

public class JobDecorator implements JobResult {

    private final Log LOG = LogFactory.getLog(this.getClass());

    private final Job job;
    private JobStatus status;
    private Priority priority;
    private Future<JobStatus> result;

    JobDecorator(final Job job) {
        this.job = job;
        this.status = JobStatus.QUEUED;
        this.priority = Priority.MIDDLE;
    }

    JobDecorator(final Prioritized job) {
        this.job = job;
        this.priority = job.getPriority();
        this.status = JobStatus.QUEUED;
    }

    private JobStatus increaseStatus(final JobStatus status) throws JobStatusException {
        this.status = this.status.isNext(status) ? status : JobStatus.FAILED;
        if (!this.status.equals(status)) throw new JobStatusException();
        return this.status;
    }

    @Override
    public JobStatus call() {
        try {
            increaseStatus(JobStatus.RUNNING);
            this.process();
            return increaseStatus(JobStatus.SUCCESS);
        } catch (Exception e) {
            LOG.error(e);
        }
        return this.status = JobStatus.FAILED;
    }

    @Override
    public void process() throws Exception {
        job.process();
    }

    @Override
    public synchronized JobStatus getStatus() {
        return this.status;
    }

    @Override
    public Priority getPriority() {
        return this.priority;
    }

    @Override
    public int compareTo(final Prioritized o) {
        return this.priority.compareTo(o.getPriority());
    }

    @Override
    public Future<JobStatus> getResult() {
        return this.result;
    }

    @Override
    public void setResult(Future<JobStatus> result) {
        this.result = result;
    }
}
