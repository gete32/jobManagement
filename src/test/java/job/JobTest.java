package job;

import constants.Priority;
import service.job.Job;
import service.job.Prioritized;

public class JobTest implements Job, Prioritized {

    private String code;
    private Priority priority;

    public JobTest(String code, Priority priority) {
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

    public Priority getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Prioritized o) {
        return this.priority.compareTo(o.getPriority());
    }
}
