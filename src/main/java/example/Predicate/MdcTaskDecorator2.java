package example.Predicate;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Executor;

public class MdcTaskDecorator2 implements Executor {
    private final Executor delegate;

    public MdcTaskDecorator2(Executor delegate) { this.delegate = delegate; }

    @Override
    public void execute(Runnable command) {
        // 1. 부모 스레드 MDC 캡처
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        delegate.execute(() -> {
            // 2. [이곳을 채워주세요] 자식 스레드 주입 및 뒷정리
            if (contextMap != null) MDC.setContextMap(contextMap);
            try {
                command.run();
            } finally {
                MDC.clear();
            }
        });
    }

}
