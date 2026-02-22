package example.Part2;
import java.util.stream.IntStream;

public class Week4Day1_Trap {

    // 공유 변수 (공유 자원)
    static int totalSum = 0;

    public static void main(String[] args) {
        System.out.println("=== Parallel Stream Side-Effect Experiment ===");

        // 1부터 1000까지 병렬로 돌면서 totalSum에 더하기
        IntStream.rangeClosed(1, 1000)
                .parallel() // ⚡ 병렬 모드
                .forEach(i -> {
                    totalSum += i; // 🚨 위험! 여러 스레드가 동시에 접근
                });

        // 기대값: 500500 (1~1000의 합)
        System.out.println("Expected: 500500");
        System.out.println("Actual  : " + totalSum);
    }
}
