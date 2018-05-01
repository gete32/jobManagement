package constants;

public enum JobStatus {

    QUEUED(1), RUNNING(2), SUCCESS(3), FAILED(4);

    private int code;

    JobStatus(int code) {
        this.code = code;
    }

    public boolean isNext(JobStatus status){
        return isNext(this, status);
    }

    public boolean isNext(JobStatus from, JobStatus to) {
        return from == null && QUEUED.equals(to) || to != null && (to.code - code == 1 || JobStatus.FAILED.equals(to));
    }
}
