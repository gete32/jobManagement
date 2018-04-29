package service;

import constants.JobStatusEnum;
import constants.PriorityEnum;
import exception.JobStatusException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JobDecorator implements Job, InternalJob {

    private final Job job;
    private final Log log = LogFactory.getLog(this.getClass());

    private JobStatusEnum status;
    private PriorityEnum priority;

    JobDecorator(Job job) {
        this(job, PriorityEnum.MIDDLE);
    }

    JobDecorator(Job job, PriorityEnum priority) {
        this.job = job;
        this.priority = priority;
        this.status = JobStatusEnum.NOT_REGISTERED;
    }

    public void increaseStatus(final JobStatusEnum status) throws JobStatusException {
        this.status = this.status.isNext(status) ? status : JobStatusEnum.FAILED;
        if (!this.status.is(status)) throw new JobStatusException();
    }

    @Override
    public void run() {
        try {
            increaseStatus(JobStatusEnum.RUNNING);
            this.process();
            increaseStatus(JobStatusEnum.SUCCESS);
        } catch (JobStatusException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void process() {
        job.process();
    }

    @Override
    public JobStatusEnum getStatus() {
        return status;
    }

    @Override
    public PriorityEnum getPriority() {
        return priority;
    }
}
