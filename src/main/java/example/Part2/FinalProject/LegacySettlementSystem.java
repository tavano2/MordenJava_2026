package example.Part2.FinalProject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LegacySettlementSystem {
    public static void main(String[] args) {
        String filePath = OrderLogGenerator.FILE_PATH;

        System.out.println("🐢 [Legacy] 정산 시스템 가동 (Single Thread)");
        long start = System.currentTimeMillis();

        Map<String, Long> salesMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 1. 파싱 (String split 비용 발생)
                String[] parts = line.split(",");

                String merchant = parts[1];
                int amount = Integer.parseInt(parts[2]);
                String status = parts[3];

                // 2. 필터링 및 집계
                if ("PAID".equals(status)) {
                    salesMap.put(merchant, salesMap.getOrDefault(merchant, 0L) + amount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 결과 출력 (Top 5)
        printTop5(salesMap);

        long end = System.currentTimeMillis();
        System.out.println("⏱️ 소요 시간: " + (end - start) + "ms");
    }

    private static void printTop5(Map<String, Long> salesMap) {
        System.out.println("=== Top 5 매출 ===");
        salesMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
}
