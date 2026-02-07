package Part2.FinalProject;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FinalSettlementSystem {
    private static final String FILE_PATH = OrderLogGenerator.FILE_PATH;
    private static final int THREAD_COUNT = 4;

    // 통계 객체 (위에서 정의한 내용)
    static class MerchantStats {
        long totalAmount = 0;
        long count = 0;
        int maxAmount = 0;

        public void add(int amount) {
            this.totalAmount += amount;
            this.count++;
            if (amount > this.maxAmount) this.maxAmount = amount; // Math.max보다 if문이 미세하게 더 빠름
        }

        public MerchantStats merge(MerchantStats other) {
            this.totalAmount += other.totalAmount;
            this.count += other.count;
            if (other.maxAmount > this.maxAmount) this.maxAmount = other.maxAmount;
            return this;
        }

        @Override
        public String toString() {
            return String.format("총매출: %,15d원 | 건수: %,8d건 | 최고가: %,8d원", totalAmount, count, maxAmount);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("🔥 [Final] Ultimate 정산 시스템 가동 (One-Pass Aggregation)");
        long start = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        ConcurrentHashMap<String, MerchantStats> globalStats = new ConcurrentHashMap<>();

        try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "r");
             FileChannel channel = file.getChannel()) {

            long fileSize = channel.size();
            long sectionSize = fileSize / THREAD_COUNT;

            for (int i = 0; i < THREAD_COUNT; i++) {
                long startPos = i * sectionSize;
                long endPos = (i == THREAD_COUNT - 1) ? fileSize : startPos + sectionSize;

                executor.submit(() -> processSection(startPos, endPos, globalStats));
            }
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long end = System.currentTimeMillis();
        System.out.println("⏱️ 최종 소요 시간: " + (end - start) + "ms");

        printTop5(globalStats);
    }

    private static void processSection(long start, long end, ConcurrentHashMap<String, MerchantStats> globalStats) {
        try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "r");
             FileChannel channel = file.getChannel()) {

            long mapSize = (end - start) + 1024;
            if (start + mapSize > channel.size()) mapSize = channel.size() - start;

            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, start, mapSize);

            if (start > 0) {
                while (buffer.hasRemaining() && buffer.get() != '\n') {}
            }

            // Local Aggregation (스레드 로컬 맵)
            Map<String, MerchantStats> localStats = new HashMap<>();

            while (buffer.hasRemaining()) {
                long currentPos = start + buffer.position();
                if (currentPos >= end) break;

                // Parsing Logic (Zero-GC)
                skipToComma(buffer); // Skip ID
                String merchant = readStringUntilComma(buffer); // Merchant
                int amount = readIntUntilComma(buffer); // Amount
                String status = readStringUntilNewline(buffer); // Status

                if ("PAID".equals(status)) {
                    // 1. 없으면 새로 만들고, 있으면 가져오기
                    MerchantStats stats = localStats.computeIfAbsent(merchant, k -> new MerchantStats());
                    // 2. 데이터 누적 (One-Pass)
                    stats.add(amount);
                }
            }

            // Global Merge (Lock 최소화)
            localStats.forEach((key, localStat) ->
                    globalStats.merge(key, localStat, MerchantStats::merge)
            );

        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- Helper Methods (Zero-GC) ---
    private static void skipToComma(MappedByteBuffer buffer) {
        while (buffer.hasRemaining() && buffer.get() != ',') {}
    }
    private static String readStringUntilComma(MappedByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b == ',') break;
            sb.append((char) b);
        }
        return sb.toString();
    }
    private static String readStringUntilNewline(MappedByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b == '\n' || b == '\r') break;
            sb.append((char) b);
        }
        if (buffer.hasRemaining()) {
            buffer.mark();
            if (buffer.get() != '\n') buffer.reset();
        }
        return sb.toString();
    }
    private static int readIntUntilComma(MappedByteBuffer buffer) {
        int result = 0;
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b == ',') break;
            result = result * 10 + (b - '0');
        }
        return result;
    }

    private static void printTop5(Map<String, MerchantStats> statsMap) {
        System.out.println("=== 🏆 Top 5 판매자 리포트 ===");
        statsMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue().totalAmount, e1.getValue().totalAmount))
                .limit(5)
                .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));
    }
}
