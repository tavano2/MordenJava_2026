package Part2;
import java.util.*;
import java.util.stream.*;

public class BoxingPerformanceLab {

    // 테스트용 주문 객체
    public record Order(long id, int amount) {}

    public static void main(String[] args) {
        // 1. 데이터 준비: 500만 건 (성능 차이 체감을 위해 대용량 생성)
        int N = 5_000_000;
        System.out.println("--- 데이터 생성 중... (" + N + "건) ---");

        List<Order> orders = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            orders.add(new Order(i, 100)); // 100원짜리 주문
        }

        System.out.println("--- 성능 비교 시작 ---");

        // ❌ [1] Bad Practice: Stream<Integer>
        // 흐름: Order 객체 -> (map) -> Integer 객체 생성(Boxing) 📦 -> (reduce) -> Unboxing 후 덧셈
        long start1 = System.currentTimeMillis();
        long sum1 = orders.stream()
                .map(Order::amount) // ⚠️ 여기서 Stream<Integer>가 생성됨 (오토박싱)
                .reduce(0, Integer::sum);
        long end1 = System.currentTimeMillis();

        System.out.println("1. Boxed Stream (Stream<Integer>): " + (end1 - start1) + " ms");


        // ✅ [2] Best Practice: IntStream
        // 흐름: Order 객체 -> (mapToInt) -> int 값 추출(No Object) 🚀 -> sum()
        long start2 = System.currentTimeMillis();
        long sum2 = orders.stream()
                .mapToInt(Order::amount) // ✅ IntStream으로 변환 (No Boxing)
                .sum();
        long end2 = System.currentTimeMillis();

        OptionalDouble avg = orders.stream().mapToInt(Order::amount).average();

        System.out.println("2. IntStream (Primitive Stream) : " + (end2 - start2) + " ms");
        System.out.println("avg ::"+ avg);

        // 결과 검증
        if (sum1 == sum2) {
            double speedup = (double)(end1 - start1) / (end2 - start2);
            System.out.printf("\n💡 결론: 기본형 스트림이 약 %.1f배 더 빠릅니다.\n", speedup);
        }
    }
}
