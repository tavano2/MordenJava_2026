package Predicate;

import java.util.*;

public class CodeKata20260208 {

    public record Order(
            Long id,
            String customerName,
            String customerEmail, // null일 수 있음
            Double amount,        // null일 수 있음
            String status         // "SHIPPED", "CANCELLED", "PENDING"
    ) {}


    // [문제 1] "VIP 고객을 찾아라" (Filter + Sort + Limit)
    public List<Order> getTop3ShippedOrders(List<Order> orders) {
        List<Order> result = new ArrayList<>();
        // 1. 배송된 것만 필터링
        for (Order order : orders) {
            if (order != null && "SHIPPED".equals(order.status())) {
                if (order.amount() != null) {
                    result.add(order);
                }
            }
        }

        // 2. 금액 내림차순 정렬 (지저분한 익명 클래스)
        Collections.sort(result, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return Double.compare(o2.amount(), o1.amount());
            }
        });

        // 3. 상위 3개 자르기
        if (result.size() > 3) {
            return result.subList(0, 3);
        }
        return result;
    }
    // tobe 결과 : 등급: B (Business Logic Fail) -> 내림차순으로 정렬해야 함
    public List<Order> getTop3ShippedOrdersTobe(List<Order> orders) {
        return orders.stream()
                .filter(o -> "SHIPPED".equals(o.status()))
                .filter(o -> o.amount() != null)
               // .sorted(Comparator.comparingDouble(Order::amount)) 오름차순!
                .sorted(Comparator.comparingDouble(Order::amount).reversed())
                .limit(3)
                .toList();
    }


    // [문제 2] "이메일 마케팅 리스트" (Map + Distinct)
    public List<String> getUniqueCustomerEmails(List<Order> orders) {
        Set<String> uniqueEmails = new HashSet<>(); // 이 부분을 잘 봤어야 함!
        for (Order order : orders) {
            if (order != null) {
                String email = order.customerEmail();
                if (email != null && !email.isEmpty()) {
                    uniqueEmails.add(email);
                }
            }
        }
        return new ArrayList<>(uniqueEmails);
    }
    // tobe 결과 : 등급: B- (Functional Defect) -- 셋이여서 중복이 제거 되어야 하는데 넣지 않음.
    public List<String> getUniqueCustomerEmailsTobe(List<Order> orders) {
        return orders.stream()
                .filter(Objects::nonNull)
                .filter(this::hasValidEmail)
                .map(Order::customerEmail)
                .distinct() // 중복 제거 필요할 때 필수로 넣어야 함!
                .toList();
    }

    private boolean hasValidEmail(Order o) { // 서브 메소드를 작성할 때는 public이 아닌 private로!
        String email = o.customerEmail();
        return email != null && !email.isEmpty();
    }

    // [문제 3] "오늘의 최고가 주문" (Max + Optional)
    public Order getMostExpensiveOrder(List<Order> orders) {
        Order maxOrder = null;
        double maxAmount = -1.0;

        for (Order order : orders) {
            if (order != null && order.amount() != null) {
                if (order.amount() > maxAmount) {
                    maxAmount = order.amount();
                    maxOrder = order;
                }
            }
        }
        return maxOrder; // 없을 경우 null 반환 (위험!)
    }

    // tobe 결과 : 등급: A+ (Perfect)
    // 완벽합니다. 군더더기가 하나도 없습니다. 특히 filter로 amount가 null인 것을 먼저 제거한 후에 Comparator.comparingDouble을 사용한 점이 아주 훌륭합니다.
    public Optional<Order> getMostExpensiveOrderTobe(List<Order> orders) {
        return orders.stream()
                .filter(Objects::nonNull)
                .filter(o -> o.amount() != null)
                .max(Comparator.comparingDouble(Order::amount));
    }


}
