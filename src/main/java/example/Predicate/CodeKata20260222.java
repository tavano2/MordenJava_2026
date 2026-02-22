package example.Predicate;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CodeKata20260222 {

    // [Day 15] 문제: "증발한 로그 ID(MDC)를 찾아라"
    // [상황]
    // 우리 시스템은 마이크로서비스 간의 추적을 위해 **MDC(Mapped Diagnostic Context)**에 traceId를 저장하여 로그를 남깁니다.
    // 하지만 비동기로 작업을 처리하는 순간, 새로운 스레드에서는 기존 스레드의 MDC 정보를 읽지 못해 로그에 traceId가 비어버리는 현상이 발생합니다.

    // 해결 1. 수동 복사 (Manual Copy)
    public static void processWithLog(String traceId) {
        // 1. 메인 스레드에 traceId 설정
        MDC.put("traceId", traceId);
        System.out.println("[Main Thread] 작업을 시작합니다. ID: " + MDC.get("traceId"));
        Map<String, String> parentMdc = MDC.getCopyOfContextMap();
        // 2. 비동기 작업 실행
        CompletableFuture.runAsync(() -> {
            // 2. 자식 스레드에 주입
            if (parentMdc != null) MDC.setContextMap(parentMdc);

            try {
                System.out.println("비동기 스레드 작업 중..."); // 이제 traceId가 잘 찍힙니다!
                // 비즈니스 로직 수행
            } finally {
                // 3. ⚠️ 핵심: 자식 스레드의 뒷정리 (스레드 풀 오염 방지)
                MDC.clear();
            }
        });
        MDC.clear();
    }

    // [Mission 2] "매번 복사하기 귀찮은데? 데코레이터 패턴!"


    public static void main(String[] args) {
        // processWithLog("hihi");
        Executor mdcExecutor = new MdcTaskDecorator(Executors.newFixedThreadPool(2));

        MDC.put("traceId", "SMART-DECORATOR");
        CompletableFuture.runAsync(() -> {
            System.out.println("데코레이터 덕분에 자동으로 찍히는 로그!");
        }, mdcExecutor);
    }
}
