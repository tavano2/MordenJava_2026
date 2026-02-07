package Part2.FinalProject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class OrderLogGenerator {
    // 파일 경로 (프로젝트 루트에 생성됨)
    public static final String FILE_PATH = "orders.csv";
    private static final int TARGET_COUNT = 10_000_000; // 1,000만 건
    private static final String[] MERCHANTS = {"M_SAMSUNG", "M_APPLE", "M_LG", "M_SONY", "M_GOOGLE"};
    private static final String[] STATUSES = {"PAID", "PAID", "PAID", "CANCEL", "REFUND"}; // PAID 확률 높임

    public static void main(String[] args) {
        System.out.println("🚀 데이터 생성을 시작합니다... (" + TARGET_COUNT + "건)");
        long start = System.currentTimeMillis();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            Random random = new Random();

            for (int i = 0; i < TARGET_COUNT; i++) {
                String merchant = MERCHANTS[random.nextInt(MERCHANTS.length)];
                int amount = (random.nextInt(100) + 1) * 1000; // 1,000 ~ 100,000원
                String status = STATUSES[random.nextInt(STATUSES.length)];

                // CSV 포맷: orderId,merchantId,amount,status
                writer.write(i + "," + merchant + "," + amount + "," + status);
                writer.newLine();

                if (i % 1_000_000 == 0) {
                    System.out.println("... " + (i / 1_000_000) + "00만 건 생성 중");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        System.out.println("✅ 데이터 생성 완료! 소요 시간: " + (end - start) + "ms");
        System.out.println("파일 위치: " + FILE_PATH);
    }
}