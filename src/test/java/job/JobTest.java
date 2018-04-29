package job;

import constants.PriorityEnum;
import service.Job;

public class JobTest implements Job {

    private String code;
    private PriorityEnum priority;

    public JobTest(String code, PriorityEnum priority) {
        this.code = code;
        this.priority = priority;
    }

    @Override
    public void process() {
        System.out.println("service " + code + " priority " + priority.name());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PriorityEnum getPriority() {
        return priority;
    }
}
