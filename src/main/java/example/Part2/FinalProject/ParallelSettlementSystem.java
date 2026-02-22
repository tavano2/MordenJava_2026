package example.Part2.FinalProject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class ParallelSettlementSystem {
    public static void main(String[] args) {
        String filePath = OrderLogGenerator.FILE_PATH;

        // 1. 나만의 전용 스레드 풀 생성 (코어 수에 맞춰 조정 가능, 여기선 4개로 설정)
        // 학습자님의 PC 코어 수가 많다면 8로 늘려보셔도 됩니다.
        int threadCount = 4;
        ForkJoinPool customPool = new ForkJoinPool(threadCount);

        System.out.println("🚀 [Parallel] 정산 시스템 가동 (Threads: " + threadCount + ")");
        long start = System.currentTimeMillis();

        try {
            // 2. 커스텀 풀 내부에서 스트림 실행
            Map<String, Long> salesMap = customPool.submit(() ->
                    Files.lines(Paths.get(filePath))
                            .parallel() // ⚡ 병렬 모드 활성화!
                            .map(line -> line.split(",")) // 파싱 (비용 큼)
                            .filter(parts -> "PAID".equals(parts[3])) // 필터링
                            .collect(Collectors.groupingByConcurrent( // 병렬 집계 (Thread-Safe)
                                    parts -> parts[1], // Key: MerchantID
                                    Collectors.summingLong(parts -> Long.parseLong(parts[2])) // Value: Sum Amount
                            ))
            ).get(); // 결과 대기

            long end = System.currentTimeMillis();
            System.out.println("⏱️ 소요 시간: " + (end - start) + "ms");

            // 결과 검증 (Top 5)
            printTop5(salesMap);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customPool.shutdown(); // 풀 정리
        }
    }

    private static void printTop5(Map<String, Long> salesMap) {
        System.out.println("=== Top 5 매출 ===");
        salesMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
}
