package example.Predicate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CodeKata20260226 {

    public enum Status { SUCCESS, FAIL }
    public enum PayMethod { CARD, CASH }

    public record Order(
            Long id,
            Status status,
            PayMethod payMethod,
            long amount
    ) {}

    public Map<Status, Map<PayMethod, Long>> calculateStatistics(List<Order> orders) {
        // 8년 차의 팁: 입력 리스트 방어
        if (orders == null || orders.isEmpty()) return Map.of();

        return orders.stream()
                // TODO: 이곳에 groupingBy를 중첩하여 통계 로직을 완성하세요.
                .collect(
                        // 힌트: Collectors.groupingBy를 두 번 중첩하고,
                        // 마지막에 Collectors.summingLong을 사용하세요.
                        Collectors.groupingBy(Order::status, Collectors.groupingBy(Order::payMethod, Collectors.summingLong(Order::amount)))
                );
    }

    // 문제 2
    // 결제 금액이 100,000원 이상인 주문을 'VIP 주문'으로 분류하려고 합니다.
    public Map<Boolean, List<Long>> partitionOrderIdsByAmount(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return Map.of();

        return orders.stream().collect(Collectors.partitioningBy((Order order) -> order.amount() >= 100_000, Collectors.mapping(Order::id, Collectors.toList())));
    }

    // 문제 3
    // 운영팀에서 각 상태별로 어떤 주문들이 있었는지 한눈에 보고 싶어 합니다.
    // 결제 상태(Status)별로 그룹화하세요.
    // 각 상태에 속한 주문 ID들을 뽑아서 **", "(쉼표+공백)**로 구분된 하나의 문자열로 만드세요.
    // 결과는 Map<Status, String> 형태여야 합니다. (예: SUCCESS -> "1, 2, 5")
    public Map<Status, String> getOrderIdsSummaryByStatus(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return Map.of();

        return orders.stream()
                .collect(
                        // TODO: groupingBy, mapping, joining을 조합하세요.
                        Collectors
                                .groupingBy(Order::status,
                                        Collectors.mapping(o -> o.id().toString(), Collectors.joining(", "))
                                )
                );
    }


}
