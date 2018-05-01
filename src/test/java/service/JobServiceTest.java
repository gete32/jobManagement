package service;

import constants.JobStatus;
import constants.Priority;
import exception.JobException;
import exception.PriorityException;
import job.JobTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import service.job.Job;
import service.job.JobResult;
import service.job.Prioritized;
import service.job.Statusable;
import utils.ServiceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static constants.Priority.*;

public class JobServiceTest {

    private Log log = LogFactory.getLog(this.getClass());

    private List<Prioritized> generateJobs(int count){
        return IntStream.range(0, count)
                .mapToObj(e -> new JobTest(Priority.of(e % 3)))
                .collect(Collectors.toList());
    }

    private List<Statusable> submitAll(List<Prioritized> jobs) {
        JobServiceImpl service = JobServiceImpl.getInstance();
        List<Statusable> results = new ArrayList<>();
        try {
            results.addAll(service.submitAll(jobs));
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            service.shutdown();
        }
        return results;
    }

    @Test
    public void testSuccess() throws ExecutionException, InterruptedException {
        List<Prioritized> jobs = generateJobs(10);
        List<Statusable> results = submitAll(jobs);
        for (Statusable job : results)
                Assert.assertEquals(job.getResult().get(), JobStatus.SUCCESS);
    }

    @Test
    public void testFailed() throws ExecutionException, InterruptedException, JobException {
        JobService jobService = JobServiceImpl.getInstance();
        Job job = () -> { throw new Exception(); };
        Statusable result = jobService.submit(job);
        Assert.assertEquals(result.getResult().get(), JobStatus.FAILED);
        jobService.shutdown();
    }

    @Test
    public void testRunning() throws JobException {
        JobService jobService = JobServiceImpl.getInstance();
        Job job = this::wait;
        Statusable result = jobService.submit(job);
        Assert.assertEquals(result.getStatus(), JobStatus.RUNNING);
        jobService.shutdownNow();
    }

    @Test
    public void testQueued() throws JobException, PriorityException {
        JobService jobService = JobServiceImpl.getInstance();
        jobService.pause();
        List<Prioritized> jobs = generateJobs(10);
        List<JobResult> results = jobService.submitAll(jobs);
        for (Statusable job : results)
            Assert.assertEquals(job.getStatus(), JobStatus.QUEUED);
        jobService.shutdown();
    }

    @Test
    public void testOrdering() throws JobException, PriorityException {
        final AtomicInteger counter = new AtomicInteger();
        JobService jobService = JobServiceImpl.getInstance();
        //jobs count should be more than corePoolSize due to the fact even internal queue should be sorted
        List<Prioritized> jobs = generateJobs(ServiceUtils.getCorePoolSize() + 100);
        jobService.pause();
        List<JobResult> results = jobService.submitAll(jobs);
        jobService.resume();
        Map<Priority, List<Job>> jobMap = jobs.stream().collect(Collectors.groupingBy(Prioritized::getPriority, Collectors.toList()));
        Stream.of(HIGHT, MIDDLE, LOW).forEach(priority -> {
            int priorityCount = jobMap.get(priority).size();
            IntStream.range(counter.get(), priorityCount).forEach(i -> Assert.assertEquals(priority, results.get(i).getPriority()));
            counter.addAndGet(priorityCount);
        });
        jobService.shutdown();
    }
}
