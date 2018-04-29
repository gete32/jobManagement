package service;

import job.JobTest;
import org.junit.Test;

import java.util.stream.IntStream;

public class JobServiceTest {

    @Test
    public void test(){
        JobServiceImpl service = JobServiceImpl.getInstance();
        IntStream.range(0, 100).forEach(e -> service.submit(new JobTest(String.valueOf(e))));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.shutdown();
    }
}
