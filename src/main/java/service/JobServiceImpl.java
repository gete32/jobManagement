package service;

import exception.JobException;
import exception.PriorityException;
import service.job.Job;
import service.job.JobResult;
import service.job.Prioritized;
import utils.ServiceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JobServiceImpl implements JobService {

    /**
     * Number of threads to preserve for scheduled jobs
     */
    private static final int SCHEDULED_THREADS = 1;

    /**
     * Job queue initial capacity
     */
    private static final int QUEUE_CAPACITY = 1;

    /**
     * Job Manager Global Instance
     */
    private static JobServiceImpl INSTANCE;

    /**
     * @return Global Instance of the Job Manager
     */
    public static JobServiceImpl getInstance() {
        return INSTANCE;
    }

    /**
     * Thread pool to perform the processing
     */
    private PausableThreadPoolExecutor pool;

    static {
        INSTANCE = new JobServiceImpl();
    }

    private JobServiceImpl() {
        Long keepAliveTime = 0L;
        int corePoolSize = ServiceUtils.getCorePoolSize();
        final BlockingQueue<Runnable> bq = new PriorityBlockingQueue<>(QUEUE_CAPACITY);
        this.pool = new PausableThreadPoolExecutor(corePoolSize, corePoolSize, keepAliveTime, TimeUnit.MILLISECONDS, bq);
    }

    /**
     * Method submit the internal job to the executor
     * @param internalJob Job that has status, result and priority
     * @return JobResult that has the future result of the execution.
     */
    private JobResult submitInternalJob(final JobResult internalJob){
        internalJob.setResult(this.pool.submit(internalJob, internalJob.getPriority()));
        return internalJob;
    }

    /**
     * Stops all execution once all jobs complete.
     */
    @Override
    public void shutdown() {
        this.pool.shutdown();
    }

    /**
     * Attempts to stop all actively executing tasks, halts the processing of
     * waiting tasks, and returns a list of the tasks that were awaiting
     * execution. This method does not wait for actively executing tasks to
     * terminate. Use awaitTermination to do that.
     * <p>
     * There are no guarantees beyond best-effort attempts to stop processing
     * actively executing tasks. For example, typical implementations will
     * cancel via Thread.interrupt(), so any task that fails to respond to
     * interrupts may never terminate.
     *
     * @return list of tasks that never commenced execution
     * @see java.util.concurrent.ExecutorService#shutdownNow()
     */
    @Override
    public List<Runnable> shutdownNow() {
        return this.pool.shutdownNow();
    }

    /**
     * Submits a job for execution
     *
     * @param job Job to process
     * @return Future representing the completion of the job.
     */
    @Override
    public JobResult submit(final Job job) throws JobException {
        if (job == null) throw new JobException();
        return submitInternalJob(new JobDecorator(job));
    }

    @Override
    public JobResult submit(final Prioritized job) throws JobException, PriorityException {
        if (job == null) throw new JobException();
        if (job.getPriority() == null) throw new PriorityException();

        return submitInternalJob(new JobDecorator(job));
    }

    @Override
    public List<JobResult> submitAll(final List<Prioritized> jobs) throws JobException, PriorityException {
        List<Prioritized> prioritizedList = jobs.stream().sorted().collect(Collectors.toList());
        List<JobResult> innerJobs = new ArrayList<>();
        for (Prioritized prioritized : prioritizedList) {
            innerJobs.add(submit(prioritized));
        }
        return innerJobs;
    }

    @Override
    public void pause(){
        this.pool.pause();
    }

    @Override
    public void resume(){
        this.pool.resume();
    }

    @Override
    public int getActiveCount(){
        return this.pool.getActiveCount();
    }
}
