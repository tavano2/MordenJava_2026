package Part2;
import java.util.Arrays;
import java.util.List;

public class CombinerTest {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        System.out.println("--- [Test] Sequential Stream ---");

        // 순차 스트림
        Integer result = numbers.stream()
                .reduce(0,
                        (acc, val) -> acc + val, // Accumulator: 정상 작동
                        (a, b) -> {
                            // Combiner: 무시되는지 테스트
                            throw new RuntimeException("이 메시지가 보이면 저는 거짓말쟁이입니다.");
                        }
                );

        System.out.println("결과: " + result);
        System.out.println("✅ 증명 완료: 에러가 안 났으니 Combiner는 실행되지 않았습니다.");
    }
}
