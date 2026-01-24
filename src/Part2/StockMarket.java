package Part2;
import java.util.Arrays;
import java.util.List;

public class StockMarket {

    record DayPrice(String date, int price) {}

    // 분석 결과: 최저가와 최고가를 담는 객체
    record PriceRange(int minPrice, int maxPrice) {

        // [Accumulator] 현재 범위(min, max)와 새로운 주가(price)를 비교해 갱신
        public PriceRange update(int newPrice) {
            // TODO: Math.min과 Math.max를 활용하여 새로운 PriceRange 반환
            // 현재 내 minPrice와 newPrice 중 더 작은 것
            // 현재 내 maxPrice와 newPrice 중 더 큰 것
            return new PriceRange(Math.min(this.minPrice(), newPrice), Math.max(this.maxPrice(), newPrice));
        }

        // [Combiner] 두 개의 범위(Range A, Range B)를 병합
        public PriceRange merge(PriceRange other) {
            // TODO: 두 범위 중 "더 작은 최저가"와 "더 큰 최고가"를 선택하여 병합
            return new PriceRange(Math.min(this.minPrice(), other.minPrice()), Math.max(this.maxPrice(), other.maxPrice()));
        }
    }

    public static void main(String[] args) {
        List<DayPrice> history = Arrays.asList(
                new DayPrice("2026-01-01", 1000),
                new DayPrice("2026-01-02", 1200),
                new DayPrice("2026-01-03", 800),  // Low
                new DayPrice("2026-01-04", 1500), // High
                new DayPrice("2026-01-05", 1100)
        );

        PriceRange result = analyzeMarket(history);

        System.out.println("Range: " + result.minPrice() + " ~ " + result.maxPrice());
        // 예상 결과: 800 ~ 1500
    }

    public static PriceRange analyzeMarket(List<DayPrice> history) {
        return history.parallelStream()
                .reduce(
                        // 1. Identity: 초기값 설정 (Logic의 핵심!)
                        // min은 무조건 줄어들어야 하니 정수 최대값으로 시작
                        // max는 무조건 커져야 하니 정수 최소값(또는 0)으로 시작
                        new PriceRange(Integer.MAX_VALUE, Integer.MIN_VALUE),

                        // 2. Accumulator: (Range, DayPrice) -> Range
                        (range, dayPrice) -> range.update(dayPrice.price()),

                        // 3. Combiner: (Range, Range) -> Range
                        (range1, range2) -> range1.merge(range2)
                );
    }
}
