package job;

import constants.Priority;
import service.job.Prioritized;

public class JobTest implements Prioritized {

    private Priority priority;

    public JobTest(Priority priority) {
        this.priority = priority;
    }

    @Override
    public void process() { }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Prioritized o) {
        return this.priority.compareTo(o.getPriority());
    }
}
