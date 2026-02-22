package example.Predicate;

import org.slf4j.MDC;
import java.util.Map;
import java.util.concurrent.Executor;

public class MdcTaskDecorator implements Executor {
    private final Executor delegate;

    public MdcTaskDecorator(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute(Runnable command) {
        // 1. 현재(부모) 스레드의 MDC를 복사합니다.
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        delegate.execute(() -> {
            // 2. [이곳을 채워주세요] 자식 스레드에 MDC 주입
            if (contextMap != null) MDC.setContextMap(contextMap);
            try {
                command.run();
            } finally {
                // 3. [이곳을 채워주세요] 자식 스레드 MDC 청소
                MDC.clear();
            }
        });
    }
}
