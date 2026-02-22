package example.Part2;
import java.util.Arrays;
import java.util.List;

public class LegacyFraudDetectorTobe {

    record Transaction(String id, long amount, String countryCode) {}

    public static void main(String[] args) {
        List<Transaction> batch = Arrays.asList(
                new Transaction("TX_001", 50000, "KR"),
                new Transaction("TX_002", 12000000, "US"), // 고액 거래 (차단 대상)
                new Transaction("TX_003", 1000, "JP")
        );

        boolean isFraudFound = validateBatch(batch);

        if (isFraudFound) {
            System.out.println("🚨 [BLOCK] Suspicious transaction detected! Batch rejected.");
        } else {
            System.out.println("✅ [PASS] All transactions are safe.");
        }
    }

    // Refactor this method using Stream API
    public static boolean validateBatch(List<Transaction> transactions) {
        return transactions.parallelStream()
                .anyMatch(t-> "BAN".equals(t.countryCode()) || t.amount > 10000000);
    }
}
