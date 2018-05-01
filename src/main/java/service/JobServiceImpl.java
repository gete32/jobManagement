package service;

import exception.JobException;
import exception.PriorityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import service.job.Job;
import service.job.JobResult;
import service.job.Prioritized;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class JobServiceImpl implements JobService {

    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * Number of threads to preserve for scheduled jobs
     */
    private static final int SCHEDULED_THREADS = 1;

    /**
     * Number of processor to leave in reserve
     */
    private static final int PROCESSOR_RESERVE = 1;

    /**
     * Number to multiply processors by for number of threads to keep ready.
     */
    private static final int PROCESSOR_MULTIPLIER = 5;

    /**
     * Job queue initial capacity
     */
    private static final int QUEUE_CAPACITY = 1;

    /**
     * Job Manager Global Instance
     */
    private static JobServiceImpl INSTANCE;

    /**
     *
     *
     * @return Global Instance of the Job Manager
     */
    public static JobServiceImpl getInstance() {
        return INSTANCE;
    }

    /**
     * Thread pool to perform the processing
     */
    private PausableThreadPoolExecutor pool;

    /**
     * Thread pool for scheduled jobs
     */
    private ScheduledExecutorService scheduledPool;

    static {
        INSTANCE = new JobServiceImpl();
    }

    private JobServiceImpl() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        Long keepAliveTime = 0L;
        int corePoolSize = PROCESSOR_RESERVE >= availableProcessors ?
                PROCESSOR_MULTIPLIER : (availableProcessors - PROCESSOR_RESERVE) * PROCESSOR_MULTIPLIER;
        final BlockingQueue<Runnable> bq = new PriorityBlockingQueue<>(QUEUE_CAPACITY);
        this.pool = new PausableThreadPoolExecutor(corePoolSize, corePoolSize, keepAliveTime, TimeUnit.MILLISECONDS, bq);
        this.scheduledPool = Executors.newScheduledThreadPool(SCHEDULED_THREADS);
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
}
