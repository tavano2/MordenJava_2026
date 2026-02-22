package example.Part2;

// 데이터를 계속 축적할 가변(Mutable) 객체
public class PerformanceStats {
    long count = 0;
    long totalTime = 0;
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;

    // 2. accumulator(): 데이터 하나가 들어왔을 때 처리
    public void add(int latency) {
        count++;
        totalTime += latency;
        min = Math.min(min, latency);
        max = Math.max(max, latency);
    }

    // 3. combiner(): 병렬 처리 시 두 객체를 합치는 로직 (중요!)
    public PerformanceStats merge(PerformanceStats other) {
        count += other.count;
        totalTime += other.totalTime;
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
        return this;
    }

    // 최종 평균값 계산용
    public double getAverage() {
        return count == 0 ? 0 : (double) totalTime / count;
    }

    @Override
    public String toString() {
        return String.format("Stats[cnt=%d, min=%d, max=%d, avg=%.2f]", count, min, max, getAverage());
    }
}
