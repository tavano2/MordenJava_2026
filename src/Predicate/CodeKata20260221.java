package Predicate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CodeKata20260221 {


    /**
     * 지정된 밀리초(ms)만큼 현재 스레드를 중단시켜
     * 외부 API 호출이나 무거운 연산을 시뮬레이션합니다.
     */
    private static void simulateHeavyLoad(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // 비동기 작업 취소 시 인터럽트 발생 대응
            Thread.currentThread().interrupt();
            throw new RuntimeException("시뮬레이션 중 인터럽트 발생", e);
        }
    }

    // 문제 1  - "무한 대기(Hang) 방지 - orTimeout"
    // 결제 외부 API를 호출했는데, 상대 서버의 네트워크 장애로 응답이 오지 않습니다.
    // 이대로 두면 해당 스레드는 영원히 응답을 기다리고, 결국 서버의 모든 스레드가 고갈되어 서비스가 뻗게 됩니다.
    public void processPayment(String paymentId) {
        CompletableFuture.supplyAsync(() -> {
                    simulateHeavyLoad(10000); // 10초나 걸리는 아주 느린 API (장애 상황)
                    return "SUCCESS";
                })
                .thenAccept(result -> System.out.println("결제 상태: " + result));

        // ⚠️ 이 코드는 10초 동안 스레드를 붙잡고 있거나, 끝날 때까지 무한 대기합니다.
    }


    public void processPaymentTobe(String paymentId) {
        // 8년 차라면 예외를 단순히 던지기보다, 로그를 정확히 남기고 파이프라인을 안전하게 종료하는 코드가 더 어울립니다.
        CompletableFuture.supplyAsync(() -> {
                    simulateHeavyLoad(10000); // 10초나 걸리는 아주 느린 API (장애 상황)
                    return "SUCCESS";
                })
                .orTimeout(3, TimeUnit.SECONDS)
                .thenAccept(result -> System.out.println("결제 상태: " + result))
                .exceptionally(throwable -> {
                    // 2. throwable.getCause()를 하면 실제 발생한 TimeoutException을 확인할 수 있습니다.
                    System.err.println("결제 처리 중 장애 발생: " + throwable.getMessage());
                    // 3. 복구할 값이 없다면 null을 반환하거나, 에러 로깅 후 종료합니다.
                    return null;
                });
    }

    // 문제 2: "우아한 회복 - completeOnTimeout"
    // 사용자의 개인화 추천 목록을 가져오는 로직입니다.
    // 추천 API가 너무 늦으면 에러를 내보내는 대신, 미리 준비된 **'기본 추천 리스트(Default List)'**를 보여주는 것이 사용자 경험(UX) 측면에서 훨씬 좋습니다.
    public CompletableFuture<String> getRecommendations() {
        return CompletableFuture.supplyAsync(() -> {
            simulateHeavyLoad(5000); // API가 평소보다 5초나 늦음
            return "Personalized_Recommendation_List";
        });
    }

    public CompletableFuture<String> getRecommendationsTobe() {
        return CompletableFuture.supplyAsync(() -> {
            simulateHeavyLoad(5000); // API가 평소보다 5초나 늦음
            return "Personalized_Recommendation_List";
        }).completeOnTimeout("Default_Popular_List", 1, TimeUnit.SECONDS);
    }

    // 문제 3: "가장 빠른 놈이 이긴다 - anyOf"
    // 서버 이중화를 넘어, 동일한 검색 결과를 주는 두 리전(Seoul, Tokyo)의 서버가 있습니다.
    // 네트워크 상태에 따라 어디가 빠를지 모르는 상황에서, "가장 먼저 도착하는 응답" 하나만 채택하고 싶습니다.
    public static void raceConditionExample() {
        CompletableFuture<String> seoulServer = CompletableFuture.supplyAsync(() -> {
            simulateHeavyLoad(2000);
            return "Result from Seoul";
        });

        CompletableFuture<String> tokyoServer = CompletableFuture.supplyAsync(() -> {
            simulateHeavyLoad(1000);
            return "Result from Tokyo";
        });

        // TODO: anyOf를 사용해 가장 빠른 결과를 출력하세요.
        System.out.println(CompletableFuture.anyOf(seoulServer, tokyoServer).join());
    }

    public static void main(String[] args) {
        raceConditionExample();
    }


}
