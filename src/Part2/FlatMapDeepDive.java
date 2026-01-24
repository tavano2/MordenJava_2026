package Part2;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlatMapDeepDive {

    // Record 정의
    record OrderItem(String itemName, int price) {}
    record Order(long orderId, List<OrderItem> items) {}

    public static void main(String[] args) {
        // 데이터 준비: 3개의 주문, 각 주문마다 여러 상품이 들어있음
        List<Order> orders = Arrays.asList(
                new Order(1001, Arrays.asList(
                        new OrderItem("Keyboard", 150000),
                        new OrderItem("Mouse", 50000)
                )),
                new Order(1002, Arrays.asList(
                        new OrderItem("Monitor", 300000)
                )),
                new Order(1003, Arrays.asList(
                        new OrderItem("RAM 16GB", 80000),
                        new OrderItem("SSD 1TB", 120000),
                        new OrderItem("HDMI Cable", 10000)
                ))
        );

        System.out.println("--- [Experiment 1] Map vs FlatMap ---");

        // 1. Map을 사용했을 때 (Bad Case)
        // 결과: Stream<List<OrderItem>> -> 2차원 구조
        // 우리가 원하는 건 "모든 상품의 목록"인데, "리스트들의 스트림"이 되어버림.
        List<List<OrderItem>> nestedList = orders.stream()
                .map(Order::items)
                .toList();

        System.out.println("1. Map Result (Nested): " + nestedList);
        System.out.println("   Size: " + nestedList.size()); // 3 (주문 개수)


        // 2. FlatMap을 사용했을 때 (Good Case)
        // 결과: Stream<OrderItem> -> 1차원 구조 (Flattening)
        // Order 객체는 사라지고, 그 안의 items들이 메인 스트림으로 쏟아져 나옴.
        List<OrderItem> flatList = orders.stream()
                .flatMap(order -> order.items().stream()) // 각 주문의 리스트를 스트림으로 변환 후 합침
                .toList();

        System.out.println("2. FlatMap Result (Flattened): " + flatList);
        System.out.println("   Size: " + flatList.size()); // 6 (총 상품 개수)


        System.out.println("\n--- [Experiment 2] Advanced: Cartesian Product (교차곱) ---");
        // 시나리오: 모든 '팀'과 모든 '유니폼 색상'의 가능한 조합을 만들어라 (이중 for문 대체)
        List<String> teams = Arrays.asList("TeamA", "TeamB");
        List<String> colors = Arrays.asList("Red", "Blue", "White");

        // 이중 for문 없이 모든 경우의 수 생성
        List<String> combinations = teams.stream()
                .flatMap(team -> colors.stream() // 팀 1개당 색상 3개 스트림 생성
                        .map(color -> team + "-" + color)) // 조합 문자열 생성
                .toList();

        System.out.println("Combinations: " + combinations);
    }
}
