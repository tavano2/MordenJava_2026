package example.Part2;
import java.util.Arrays;
import java.util.List;

public class ReduceDeepDive {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        System.out.println("--- [Experiment 1] Sequential Stream (순차) ---");
        // 순차 처리: 메인 스레드 혼자 다 하므로 'Combiner'가 필요 없음.
        int sumSequential = numbers.stream()
                .reduce(0,
                        (subtotal, element) -> {
                            System.out.println("[Accumulator] Subtotal: " + subtotal + " + " + element);
                            return subtotal + element;
                        },
                        (subtotal1, subtotal2) -> {
                            System.out.println("🚨 [Combiner] Called! " + subtotal1 + " + " + subtotal2);
                            return subtotal1 + subtotal2;
                        }
                );
        System.out.println("Result: " + sumSequential + "\n");


        System.out.println("--- [Experiment 2] Parallel Stream (병렬) ---");
        // 병렬 처리: 여러 스레드가 각자 계산(Accumulator)한 뒤, 결과를 합침(Combiner).
        int sumParallel = numbers.parallelStream()
                .reduce(0,
                        (subtotal, element) -> {
                            // System.out.println()은 동기화 문제로 순서가 섞일 수 있음 (단순 확인용)
                            // System.out.println("[" + Thread.currentThread().getName() + "] Accumulator: " + element);
                            return subtotal + element;
                        },
                        (subtotal1, subtotal2) -> {
                            // 여기가 핵심! 병렬 처리에서 부분 합계들이 합쳐지는 순간
                            System.out.println("✅ [" + Thread.currentThread().getName() + "] Combiner Merging: " + subtotal1 + " + " + subtotal2);
                            return subtotal1 + subtotal2;
                        }
                );
        System.out.println("Result: " + sumParallel);
    }
}
