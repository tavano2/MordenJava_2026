package Part2;
import java.util.Arrays;
import java.util.List;

public class ReviewAnalyzer {

    // 1. 통계 정보를 담을 불변 객체 (State Container)
    record Stats(long validCount, long totalLength) {

        // [Accumulator Logic] 문자열 하나가 들어왔을 때 나의 상태를 갱신
        public Stats add(String review) {
            if (review == null || review.isBlank()) {
                return this; // 변화 없음
            }
            // TODO: count는 1 증가, length는 review 길이만큼 증가시킨 새로운 Stats 반환
            // Hint: Record는 불변이므로 new Stats(...)를 리턴해야 함
            return new Stats(this.validCount() + 1, this.totalLength() + review.length());
        }

        // [Combiner Logic] 두 개의 Stats(부분 합계)를 하나로 합침
        public Stats merge(Stats other) {
            // TODO: 내 count + 상대 count, 내 length + 상대 length 합산 반환
            return new Stats(this.validCount() + other.validCount(), this.totalLength() + other.totalLength());
        }
    }

    public static void main(String[] args) {
        List<String> reviews = Arrays.asList(
                "Great product!",           // 14 chars
                "",                         // Skip
                "Bad quality, do not buy.", // 24 chars
                null,                       // Skip
                "So so."                    // 6 chars
        );

        Stats result = analyzeReviews(reviews);

        System.out.println("Valid Reviews: " + result.validCount()); // 예상: 3
        System.out.println("Total Length: " + result.totalLength()); // 예상: 44
    }

    public static Stats analyzeReviews(List<String> reviews) {
        return reviews.parallelStream()
                .reduce(
                        new Stats(0, 0), // 1. Identity

                        // 2. Accumulator: (Stats s, String r) -> s.add(r)
                        (stats, review) -> stats.add(review),

                        // 3. Combiner: (Stats s1, Stats s2) -> s1.merge(s2)
                        (stats1, stats2) -> stats1.merge(stats2)
                );
    }
}