package job;

import service.Job;

public class JobTest implements Job {

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
