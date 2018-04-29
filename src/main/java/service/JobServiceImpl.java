package service;

import constants.JobStatusEnum;
import constants.PriorityEnum;
import exception.JobException;
import exception.JobStatusException;
import exception.PriorityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.*;

public class JobServiceImpl implements JobService {

    private class ComparePriority implements Comparator<Runnable> {

        @Override
        public int compare(Runnable o1, Runnable o2) {
            return o1 instanceof InternalJob && o2 instanceof InternalJob ?
                    ((InternalJob) o1).getPriority().compareTo(((InternalJob) o2).getPriority()) : -1;
        }
    }

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
    private static final int QUEUE_CAPACITY = 100;

    /**
     * Job Manager Global Instance
     */
    private static JobServiceImpl INSTANCE = null;

    /**
     * @return Global Instance of the Job Manager
     */
    public static JobServiceImpl getInstance() {
        return INSTANCE == null ? INSTANCE = new JobServiceImpl() : INSTANCE;
    }

    /**
     * Thread pool to perform the processing
     */
    private PausableThreadPoolExecutor pool;

    /**
     * Thread pool for scheduled jobs
     */
    private ScheduledExecutorService scheduledPool;

    /**
     * Constructor protected to limit instantiation.
     */
    private JobServiceImpl() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int corePoolSize = PROCESSOR_RESERVE >= availableProcessors ?
                PROCESSOR_MULTIPLIER : (availableProcessors - PROCESSOR_RESERVE) * PROCESSOR_MULTIPLIER;
        final BlockingQueue<Runnable> bq = new PriorityBlockingQueue<>(QUEUE_CAPACITY, new ComparePriority());
        this.pool = new PausableThreadPoolExecutor(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, bq);
        this.scheduledPool = Executors.newScheduledThreadPool(SCHEDULED_THREADS);
    }

    private InternalJob submitInternalJob(final InternalJob internalJob){
        try {
            internalJob.increaseStatus(JobStatusEnum.QUEUED);
            this.pool.submit(internalJob, internalJob.getPriority());
        } catch (JobStatusException e) {
            log.error(e.getMessage());
        }
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
    public InternalJob submit(final Job job) throws JobException {
        if (job == null) throw new JobException();
        return submitInternalJob(new JobDecorator(job));
    }

    @Override
    public InternalJob submit(final Job job, final PriorityEnum priority) throws JobException, PriorityException {
        if (job == null) throw new JobException();
        if (priority == null) throw new PriorityException();

        return submitInternalJob(new JobDecorator(job, priority));
    }

    @Override
    public List<InternalJob> submitAll(final Collection<Job> jobs) throws JobException {
        if (jobs == null) return Collections.emptyList();
        final List<InternalJob> result = new ArrayList<>();
        this.pool.pause();
        for (Job job : jobs)
            result.add(submit(job));
        this.pool.resume();
        return result;
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
