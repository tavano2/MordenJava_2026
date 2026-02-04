package Part2;
import java.util.stream.LongStream;

public class Week4Day1 {
    public static void main(String[] args) {
        long n = 10_000_000; // 1,000만 건

        // 1. 순차 스트림 (Sequential)
        long start = System.currentTimeMillis();
        long sum1 = LongStream.rangeClosed(1, n)
                .reduce(0, Long::sum);
        long end = System.currentTimeMillis();
        System.out.println("Sequential Time: " + (end - start) + "ms");

        // 2. 병렬 스트림 (Parallel)
        start = System.currentTimeMillis();
        long sum2 = LongStream.rangeClosed(1, n)
                .parallel() // ⚡ 병렬 모드
                .reduce(0, Long::sum);
        end = System.currentTimeMillis();
        System.out.println("Parallel Time: " + (end - start) + "ms");
    }
}
