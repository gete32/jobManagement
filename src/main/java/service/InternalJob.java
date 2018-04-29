package service;

import constants.JobStatusEnum;
import constants.PriorityEnum;
import exception.JobStatusException;

public interface InternalJob extends Runnable {

    JobStatusEnum getStatus();

    PriorityEnum getPriority();

    void increaseStatus(final JobStatusEnum status) throws JobStatusException;

}
