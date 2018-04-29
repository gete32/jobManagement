package constants;

public enum PriorityEnum {

    HIGHT(3), MIDDLE(2), LOW(1);

    private final int priority;

    PriorityEnum(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
