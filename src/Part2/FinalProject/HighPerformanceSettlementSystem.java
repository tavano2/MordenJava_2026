package Part2.FinalProject;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HighPerformanceSettlementSystem {
    private static final String FILE_PATH = OrderLogGenerator.FILE_PATH;
    private static final int THREAD_COUNT = 4; // 학습자님 코어 수에 맞춤

    public static void main(String[] args) throws Exception {
        System.out.println("🚀 [High-Performance] Zero-Copy 정산 시스템 가동");
        long start = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        // 결과 취합용 (ConcurrentHashMap 사용)
        ConcurrentHashMap<String, Long> totalSales = new ConcurrentHashMap<>();

        try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "r");
             FileChannel channel = file.getChannel()) {

            long fileSize = channel.size();
            long sectionSize = fileSize / THREAD_COUNT;

            for (int i = 0; i < THREAD_COUNT; i++) {
                long startPos = i * sectionSize;
                long endPos = (i == THREAD_COUNT - 1) ? fileSize : startPos + sectionSize;

                // 각 스레드에게 작업 할당
                executor.submit(() -> processSection(startPos, endPos, totalSales));
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long end = System.currentTimeMillis();
        System.out.println("⏱️ 소요 시간: " + (end - start) + "ms");

        printTop5(totalSales);
    }

    private static void processSection(long start, long end, ConcurrentHashMap<String, Long> resultMap) {
        try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "r");
             FileChannel channel = file.getChannel()) {

            // 매핑 크기 설정 (여유분 1KB)
            long mapSize = (end - start) + 1024;
            if (start + mapSize > channel.size()) {
                mapSize = channel.size() - start;
            }

            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, start, mapSize);

            // 1. Skip First: 첫 번째 줄바꿈까지 건너뛰기 (0번 스레드 제외)
            if (start > 0) {
                while (buffer.hasRemaining() && buffer.get() != '\n') {
                    // Skip
                }
            }

            // 2. 데이터 처리 루프
            // 포맷: ID,MERCHANT,AMOUNT,STATUS
            Map<String, Long> localMap = new HashMap<>(); // 스레드 로컬 집계

            while (buffer.hasRemaining()) {
                long currentPos = start + buffer.position();
                if (currentPos >= end) break; // 내 구역 끝

                // 한 줄 파싱 시작
                // 1) ID 건너뛰기
                skipToComma(buffer);

                // 2) MERCHANT 읽기
                String merchant = readStringUntilComma(buffer);

                // 3) AMOUNT 읽기
                int amount = readIntUntilComma(buffer);

                // 4) STATUS 읽기 & 줄바꿈 처리
                String status = readStringUntilNewline(buffer);

                if ("PAID".equals(status)) {
                    localMap.put(merchant, localMap.getOrDefault(merchant, 0L) + amount);
                }
            }

            // 로컬 결과를 전역 맵에 병합 (Lock 최소화)
            localMap.forEach((k, v) ->
                    resultMap.merge(k, v, Long::sum)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 헬퍼 메서드: 콤마까지 건너뛰기
    private static void skipToComma(MappedByteBuffer buffer) {
        while (buffer.hasRemaining() && buffer.get() != ',') {}
    }

    // 헬퍼 메서드: 콤마까지 읽어서 String 변환 (최적화 여지 있음)
    private static String readStringUntilComma(MappedByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b == ',') break;
            sb.append((char) b);
        }
        return sb.toString();
    }

    // 헬퍼 메서드: 엔터까지 읽어서 String 변환
    private static String readStringUntilNewline(MappedByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b == '\n' || b == '\r') break; // 윈도우/리눅스 개행 호환
            sb.append((char) b);
        }
        // 윈도우(\r\n) 처리용: 혹시 다음 글자가 \n이면 소비
        if (buffer.hasRemaining()) {
            buffer.mark();
            if (buffer.get() != '\n') buffer.reset();
        }
        return sb.toString();
    }

    // 헬퍼 메서드: 바이트 -> int 직접 변환 (Integer.parseInt 제거)
    private static int readIntUntilComma(MappedByteBuffer buffer) {
        int result = 0;
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b == ',') break;
            if (b >= '0' && b <= '9') {
                result = result * 10 + (b - '0');
            }
        }
        return result;
    }

    private static void printTop5(Map<String, Long> salesMap) {
        System.out.println("=== Top 5 매출 ===");
        salesMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
}
