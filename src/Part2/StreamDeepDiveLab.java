package Part2;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamDeepDiveLab {

    // Java 14+ Record: 불변 데이터 객체 (간결함)
    public record Product(String name, int price) {}

    public static void main(String[] args) {
        List<Product> products = List.of(
                new Product("TV", 1_200_000),       // 1. 고가
                new Product("Refrigerator", 2_500_000), // 2. 고가
                new Product("Keyboard", 150_000),   // 3. 저가 (Target)
                new Product("Mouse", 50_000),       // 4. 저가
                new Product("Monitor", 300_000),    // 5. 고가 (Target)
                new Product("USB", 10_000)          // 6. 저가
        );

        System.out.println("--- 1. 스트림 파이프라인 정의 (아직 실행 안 됨) ---");

        // 시나리오: "20만원 이상인 제품 중, 이름을 대문자로 변환하여 2개만 조회"
        Stream<String> stream = products.stream()
                .filter(p -> {
                    System.out.println("🔍 Filtering (Price >= 200k): " + p.name());
                    return p.price() >= 200_000;
                })
                .map(p -> {
                    System.out.println("🔄 Mapping (To Upper): " + p.name());
                    return p.name().toUpperCase();
                })
                .limit(2); // Short-circuit 조건 (2개 찾으면 끝)

        System.out.println("--- 2. 정의 완료. (엔터 키를 누르면 터미널 연산 시작) ---");
        try { System.in.read(); } catch (Exception e) {}

        System.out.println(">>> collect() 호출! (이제야 물이 흐르기 시작함)");
        List<String> result = stream.collect(Collectors.toList());

        System.out.println("--- 3. 결과 확인 ---");
        System.out.println("Result: " + result);
    }
}