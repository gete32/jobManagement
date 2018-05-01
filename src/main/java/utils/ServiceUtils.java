package utils;

public class ServiceUtils {

    /**
     * Number of processor to leave in reserve
     */
    private static final int PROCESSOR_RESERVE = 1;

    /**
     * Number to multiply processors by for number of threads to keep ready.
     */
    private static final int PROCESSOR_MULTIPLIER = 5;

    public static int getCorePoolSize() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        return PROCESSOR_RESERVE >= availableProcessors ?
                PROCESSOR_MULTIPLIER : (availableProcessors - PROCESSOR_RESERVE) * PROCESSOR_MULTIPLIER;
    }

}
