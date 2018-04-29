package service;

import constants.JobStatusEnum;
import constants.PriorityEnum;
import exception.JobStatusException;

public interface Job extends Runnable {

    void process();

    JobStatusEnum getStatus();

    void setPriority(PriorityEnum priority);

    void increaseStatus(final JobStatusEnum status) throws JobStatusException;
}
