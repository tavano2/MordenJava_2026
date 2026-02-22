package example.Predicate;
import org.slf4j.MDC;
import java.util.concurrent.CompletableFuture;

public class CodeKata20260222 {

    public void processWithLog(String traceId) {
        // 1. 메인 스레드에 traceId 설정
        MDC.put("traceId", traceId);
        System.out.println("[Main Thread] 작업을 시작합니다. ID: " + MDC.get("traceId"));

        // 2. 비동기 작업 실행
        CompletableFuture.runAsync(() -> {
            // ⚠️ 문제 발생: 새로운 스레드에는 MDC 정보가 복사되지 않음!
            String currentTraceId = MDC.get("traceId");
            System.out.println("[Async Thread] 비동기 작업 중... ID: " + currentTraceId);

            if (currentTraceId == null) {
                System.err.println("[Error] traceId가 유실되었습니다!");
            }
        });

        MDC.clear();
    }
}
