package Part2;

import java.util.*;
import java.util.stream.Collectors;

public class RefactoringChallenge {

    public enum OrderStatus { PAYMENT_COMPLETED, PENDING, CANCELLED }
    public enum PaymentMethod { CREDIT_CARD, BANK_TRANSFER, PAYPAL }

    public record Order(long id, OrderStatus status, PaymentMethod method, int amount) {}

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order(101, OrderStatus.PENDING, PaymentMethod.CREDIT_CARD, 40000),
                new Order(102, OrderStatus.PAYMENT_COMPLETED, PaymentMethod.BANK_TRANSFER, 50000),
                new Order(103, OrderStatus.PAYMENT_COMPLETED, PaymentMethod.CREDIT_CARD, 25000), // 금액 미달
                new Order(104, OrderStatus.PAYMENT_COMPLETED, PaymentMethod.CREDIT_CARD, 55000), // ✅ Target 1
                new Order(105, OrderStatus.CANCELLED, PaymentMethod.CREDIT_CARD, 100000),
                new Order(106, OrderStatus.PAYMENT_COMPLETED, PaymentMethod.CREDIT_CARD, 45000), // ✅ Target 2
                new Order(107, OrderStatus.PAYMENT_COMPLETED, PaymentMethod.PAYPAL, 80000),
                new Order(108, OrderStatus.PAYMENT_COMPLETED, PaymentMethod.CREDIT_CARD, 90000)  // ✅ Target 3
        );

        // ---------------------------------------------------------
        // 1. Legacy Code (Bad Smell: Deeply Nested, Imperative)
        // ---------------------------------------------------------
        List<Long> resultLegacy = new ArrayList<>();
        int count = 0;
        for (Order order : orders) {
            // Bad Smell: Arrow Anti-pattern (화살표 모양 코드)
            if (order.status() == OrderStatus.PAYMENT_COMPLETED) {
                if (order.method() == PaymentMethod.CREDIT_CARD) {
                    if (order.amount() >= 30000) {
                        resultLegacy.add(order.id());
                        count++;
                        if (count == 3) {
                            break; // 흐름을 끊는 break 문
                        }
                    }
                }
            }
        }
        System.out.println("Legacy Result: " + resultLegacy);

        // ---------------------------------------------------------
        // 2. Modern Code (Stream API)
        // TODO: 위 로직을 Stream으로 리팩토링 하시오.
        // ---------------------------------------------------------
//        1. **조건 1:** 주문 상태가 `PAYMENT_COMPLETED` (결제 완료) 일 것.
//        2. **조건 2:** 결제 수단이 `CREDIT_CARD` (신용카드) 일 것.
//        3. **조건 3:** 주문 금액이 `30,000원` 이상일 것.
//        4. **조건 4:** 결과는 주문 ID만 추출해서 `List<Long>`으로 반환.
//        5. **조건 5:** 최대 3개까지만 찾을 것.
        List<Long> resultStream = orders.stream()
                .filter(o-> o.status() == OrderStatus.PAYMENT_COMPLETED
                        && o.method() == PaymentMethod.CREDIT_CARD
                        && o.amount() >= 30000)
                .map(Order::id)
                .limit(3)
                .toList();
                // .filter(...)
                // .map(...)
                // .limit(...)
                // .collect(...)
                // .collect(Collectors.toList()); // (일단 컴파일 되게 둠)

        System.out.println("Stream Result: " + resultStream);

        // 검증
        if (resultLegacy.equals(resultStream)) {
            System.out.println("🎉 성공! 완벽하게 리팩토링 되었습니다.");
        } else {
            System.out.println("❌ 실패! 결과가 다릅니다.");
        }
    }
}
