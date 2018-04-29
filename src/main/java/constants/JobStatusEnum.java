package constants;

public enum  JobStatusEnum {

    NOT_REGISTERED(0), QUEUED(1), RUNNING(2), SUCCESS(3), FAILED(4);

    private int code;

    JobStatusEnum(int code) {
        this.code = code;
    }

    public boolean isNext(JobStatusEnum status){
        return status != null && (status.code - code == 1 || JobStatusEnum.FAILED.equals(status));
    }

    public boolean is(JobStatusEnum status){
        return this.equals(status);
    }
}
