package service;

import java.util.List;
import java.util.concurrent.Future;

public interface JobService {

    void shutdown();

    List<Runnable> shutdownNow();

    Future<?> submit(final Job job);

}
