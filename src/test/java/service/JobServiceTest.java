package service;

import constants.PriorityEnum;
import job.JobTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JobServiceTest {

    private Log log = LogFactory.getLog(this.getClass());
    private JobServiceImpl service = JobServiceImpl.getInstance();

    private List<JobTest> generateJobs(int count, PriorityEnum priority){
        return IntStream.range(0, count).mapToObj(e -> new JobTest(String.valueOf(e), priority)).collect(Collectors.toList());
    }

    @Test
    public void test(){
        List<JobTest> jobs = generateJobs(10, PriorityEnum.LOW);
        jobs.addAll(generateJobs(10, PriorityEnum.MIDDLE));
        try {
            service.pause();
            for (JobTest job : jobs)
                service.submit(job, job.getPriority());
            service.resume();
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
    }
}
