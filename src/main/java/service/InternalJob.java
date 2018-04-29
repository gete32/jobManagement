package service;

import constants.JobStatusEnum;
import constants.PriorityEnum;
import exception.JobStatusException;

public interface InternalJob extends Runnable{

    JobStatusEnum getStatus();

    void setPriority(PriorityEnum priority);

    void increaseStatus(final JobStatusEnum status) throws JobStatusException;

}
