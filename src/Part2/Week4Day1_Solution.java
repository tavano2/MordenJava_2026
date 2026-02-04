package Part2;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.LongStream;

public class Week4Day1_Solution {
    public static void main(String[] args) {
        // 1. 나만의 전용 스레드 풀 생성 (스레드 4개)
        ForkJoinPool myPool = new ForkJoinPool(4);

        try {
            // 2. 이 풀 안에서 submit을 통해 스트림 실행
            long result = myPool.submit(() -> {
                return LongStream.rangeClosed(1, 10_000_000)
                        .parallel() // ⚡ 여기서는 myPool의 스레드를 씁니다!
                        .reduce(0, Long::sum);
            }).get(); // 결과 기다림

            System.out.println("Result: " + result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myPool.shutdown(); // 사용 후 정리
        }
    }
}