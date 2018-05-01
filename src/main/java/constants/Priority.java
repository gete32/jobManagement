package constants;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Priority {

    HIGHT(2), MIDDLE(1), LOW(0);

    private int code;
    private static Map<Integer, Priority> codes = null;

    Priority(int code) {
        this.code = code;
    }

    private static void init(){
        if (codes == null) {
            codes = Stream.of(values()).collect(Collectors.toMap(k -> k.code, Function.identity()));
        }
    }

    public static Priority of(Integer code){
        if (code == null) return LOW;
        init();
        Priority priority = codes.get(code);
        return priority == null ? LOW : priority;
    }
}
