package example.Part2;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class CustomCollectorLab {

    public static void main(String[] args) {
        Stream<Integer> latencies = Stream.of(100, 200, 50, 300, 150, 10, 500);

        // [미션] 아래 collect 부분을 완성하여 Custom Collector를 적용하세요.
        PerformanceStats stats = latencies.collect(
                // 여기에 Collector.of()를 사용하거나, 직접 구현체를 넣어보세요.
                // 힌트: Collector.of(supplier, accumulator, combiner, characteristics...)
                Collector.of(PerformanceStats::new, PerformanceStats::add, PerformanceStats::merge)
        );

        System.out.println("Result: " + stats);
    }
}