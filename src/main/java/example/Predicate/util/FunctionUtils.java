package example.Predicate.util;

import java.util.function.Function;

public class FunctionUtils {

    // 인스턴스화 방지 (Utility Pattern)
    private FunctionUtils() {}

    public static <T, R> Function<T, R> wrap(ThrowingFunction<T, R> throwingFunction) {
        return  t -> {
            try {
                return throwingFunction.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

}
