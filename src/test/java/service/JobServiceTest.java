package service;

import constants.Priority;
import job.JobTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import service.job.Prioritized;
import service.job.Statusable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JobServiceTest {

    private Log log = LogFactory.getLog(this.getClass());
    private JobServiceImpl service = JobServiceImpl.getInstance();

    private List<Prioritized> generateJobs(int count){
        return IntStream.range(0, count).mapToObj(e -> new JobTest(String.valueOf(count), Priority.of(e % 3))).collect(Collectors.toList());
    }

    @Test
    public void test(){
        List<Prioritized> jobs = generateJobs(100);
        List<Statusable> results = new ArrayList<>();
        try {
            results.addAll(service.submitAll(jobs));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
        results.forEach(e -> {
            try {
                e.getResult().get();
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        });
    }
}
