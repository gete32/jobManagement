package service;

import constants.Priority;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

class ComparableFutureTask<V> extends FutureTask<V> implements Comparable<ComparableFutureTask<V>> {

    private Priority priority;

    ComparableFutureTask(Callable<V> callable, Priority priority) {
        super(callable);
        this.priority = priority;
    }

    @Override
    public int compareTo(ComparableFutureTask<V> o) {
        return this.priority.compareTo(o.priority);
    }

}