package Part2;
import java.util.stream.LongStream;

public class Week4Day1_Exp2 {
    public static void main(String[] args) {
        // 데이터 개수는 줄이고 (N = 100), 개당 비용은 높임 (Q = High)
        long n = 100;

        System.out.println("=== High Cost Task Experiment ===");

        // 1. 순차 스트림
        long start = System.currentTimeMillis();
        long sum1 = LongStream.rangeClosed(1, n)
                .map(Week4Day1_Exp2::heavyCalculation) // 🐢 느린 작업
                .reduce(0, Long::sum);
        long end = System.currentTimeMillis();
        System.out.println("Sequential Time: " + (end - start) + "ms");

        // 2. 병렬 스트림
        start = System.currentTimeMillis();
        long sum2 = LongStream.rangeClosed(1, n)
                .parallel() // ⚡ 병렬 모드
                .map(Week4Day1_Exp2::heavyCalculation) // 🐢 느린 작업
                .reduce(0, Long::sum);
        end = System.currentTimeMillis();
        System.out.println("Parallel Time: " + (end - start) + "ms");
    }

    // Q(비용)가 높은 작업을 시뮬레이션 (10ms 대기)
    private static long heavyCalculation(long num) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) { }
        return num * 2;
    }
}
