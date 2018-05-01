package service.job;

@FunctionalInterface
public interface Job {

    void process() throws Exception;

}
