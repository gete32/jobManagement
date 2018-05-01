package service.job;

import constants.Priority;

public interface Prioritized extends Job, Comparable<Prioritized> {

    Priority getPriority();
}
