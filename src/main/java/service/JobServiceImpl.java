package service;

import constants.JobStatusEnum;
import exception.JobStatusException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.*;

public class JobServiceImpl implements JobService{

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
    private ExecutorService pool;

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
        this.pool = new ThreadPoolExecutor(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>(
                        QUEUE_CAPACITY,
                        (o1, o2) -> o1 instanceof AbstractJob && o2 instanceof AbstractJob ?
                                Integer.compare(((AbstractJob) o1).priority.getPriority(), ((AbstractJob) o2).priority.getPriority()) : -1
                )
        );
        this.scheduledPool = Executors.newScheduledThreadPool(SCHEDULED_THREADS);
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
    public Future<?> submit(final Job job) {
        if (job == null) return null;
        try {
            job.increaseStatus(JobStatusEnum.QUEUED);
            return this.pool.submit(job);
        } catch (JobStatusException e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
