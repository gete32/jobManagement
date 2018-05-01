package service.job;

import constants.JobStatus;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Statusable extends Callable<JobStatus> {

    void setResult(Future<JobStatus> result);

    Future<JobStatus> getResult();

    JobStatus getStatus();
}
