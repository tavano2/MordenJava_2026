package example.Predicate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CodeKata20260318 {

    /**
     * 주문 정보 원천 데이터
     */
    public record Order(Long id, String productName, int price) {}

    /**
     * 배송 정보 원천 데이터
     */
    public record Delivery(Long id, String address, String status) {}

    /**
     * 최종 합쳐진 요약 데이터
     */
    public record OrderSummary(String productName, String deliveryStatus) {
        // 두 객체를 합치는 정적 팩토리 메서드 (선택 사항)
        public static OrderSummary of(Order order, Delivery delivery) {
            return new OrderSummary(order.productName(), delivery.status());
        }
    }


    //    orderService.getOrder(id)와 deliveryService.getDelivery(id)를 비동기로 동시에 호출하세요.
    //    두 작업이 모두 완료되면 하나의 OrderSummary 객체로 합치세요.
    //    전체 작업은 최대 3초 안에 끝나야 하며, 넘을 경우 기본값을 반환합니다
    public CompletableFuture<OrderSummary> getSummary(Long id) {
        // TODO: supplyAsync, thenCombine, orTimeout을 활용해 보세요.
        Order order = new Order(id, "order hi", 500);
        Delivery dy = new Delivery(id, "Korea", "check");
        return CompletableFuture.supplyAsync(order::productName)
                .thenCombine(CompletableFuture.supplyAsync(dy::status),
                        OrderSummary::new).orTimeout(3, TimeUnit.SECONDS);
    }

    // 위 코드는 이미 동기적으로 다 생성후 사용한 것
    // 비동기 코드는 아래와 같음
    /*
    public CompletableFuture<OrderSummary> getSummaryS(Long id) {
        // 1. 주문 정보와 배송 정보를 '동시에' 요청 (각각 다른 스레드에서 API 호출 중...)
        CompletableFuture<Order> orderFuture = CompletableFuture.supplyAsync(() -> orderService.getOrder(id));
        CompletableFuture<Delivery> deliveryFuture = CompletableFuture.supplyAsync(() -> deliveryService.getDelivery(id));

        // 2. 두 작업이 모두 완료되면 결과물을 합침
        return orderFuture.thenCombine(deliveryFuture, (order, delivery) -> {
                    return new OrderSummary(order.productName(), delivery.status());
                })
                // 3. 전체 프로세스 타임아웃 설정 (Java 9+)
                .orTimeout(3, TimeUnit.SECONDS)
                // 4. 예외 발생 시 (타임아웃 포함) 기본값 처리
                .exceptionally(ex -> new OrderSummary("알 수 없는 상품", "조회 실패"));
    }

     */

    /**
     * 문자열 리스트로 푸실 경우 별도 record 불필요
     * 만약 객체로 푸신다면 아래 구조를 참고하세요.
     */
    public record UserProfile(String name, String email) {}

    // [상황] 리스트 안에 null이 섞여 있는 경우, Optional을 사용해 우아하게 걸러내고 싶습니다.
    // 입력 리스트 List<String> rawData에서 null인 요소는 제거하고, 나머지 문자열은 대문자로 바꿔서 리스트로 반환하세요.
    // (단, filter(Objects::nonNull) 대신 flatMap(Optional::stream) 패턴을 사용하세요.)
    public List<String> cleanAndUpperCase(List<String> rawData) {
        return rawData.stream()
                .flatMap(s -> Optional.ofNullable(s).stream())
                .map(String::toUpperCase)
                .toList();
    }

}
