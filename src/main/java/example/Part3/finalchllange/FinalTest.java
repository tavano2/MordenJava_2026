package example.Part3.finalchllange;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FinalTest {

    public List<SettlementResult> process(List<Transaction> txs) {
        return Optional.ofNullable(txs).orElseGet(List::of).stream()
                .filter(Objects::nonNull)
                .map(SettlementResult::calculateSettlement) // 여기서 Pattern Matching switch 사용!
                .toList();
    }

    public void finalAudit(List<Transaction> transactions) {
        List<SettlementResult> results = process(transactions);

        // 1. 총 정산 금액 (환불 상계 포함)
        double totalAmount = results.stream()
                .mapToDouble(SettlementResult::finalAmount)
                .sum();

        // 2. VIP 리포트만 출력
        results.stream()
                .filter(r -> r.report().contains("VIP"))
                .forEach(v -> System.out.println("★ VIP 알림: " + v.report() + " (TX: " + v.txId() + ")"));

        System.out.println("------------------------------------");
        System.out.println("최종 정산 예정 금액: " + totalAmount + " 달러");
    }

    public static void main(String[] args) {
        // 테스트 테스트
    }

}
