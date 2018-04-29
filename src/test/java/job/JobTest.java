package job;

import service.AbstractJob;
import service.Job;

public class JobTest extends AbstractJob implements Job {

    private String code;

    public JobTest(String code) {
        this.code = code;
    }

    @Override
    public void process() {
        System.out.println("service " + code);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
