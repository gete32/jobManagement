package service;

import constants.JobStatusEnum;
import constants.PriorityEnum;
import exception.JobStatusException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractJob implements Job {

    private final Log log = LogFactory.getLog(this.getClass());

    private JobStatusEnum status = JobStatusEnum.NOT_REGISTERED;

    protected PriorityEnum priority = PriorityEnum.MIDDLE;

    public void increaseStatus(final JobStatusEnum status) throws JobStatusException {
        this.status = this.status.isNext(status) ? status : JobStatusEnum.FAILED;
        if (!this.status.is(status)) throw new JobStatusException();
    }

    /**
     * Method to implement to perform the actions necessary for the Job.
     */
    public abstract void process();

    @Override
    public void run() {
        try {
            increaseStatus(JobStatusEnum.RUNNING);
            this.process();
            increaseStatus(JobStatusEnum.RUNNING);
        } catch (JobStatusException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public JobStatusEnum getStatus() {
        return status;
    }

    @Override
    public void setPriority(final PriorityEnum priority){
        if (priority == null) return;
        this.priority = priority;
    }
}
