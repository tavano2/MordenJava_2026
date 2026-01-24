package Part1;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaMaster {

    // Java 14 Record (불변 DTO)
    public record Apple(String color, int weight) {}

    public static void main(String[] args) {
        // 1. [Supplier] 데이터 공급
        // : () -> T 형태. 사과 리스트를 만들어주는 공장
        Supplier<List<Apple>> appleFactory = () -> List.of(
                new Apple("GREEN", 150),
                new Apple("RED", 120),
                new Apple("GREEN", 170)
        );
        List<Apple> inventory = appleFactory.get(); // 리스트 생성

        System.out.println("--- 1. 녹색 사과만 필터링 (Predicate) ---");
        // 우리가 만든 filter 메서드 사용
        List<Apple> greenApples = filter(inventory, apple -> "GREEN".equals(apple.color()));
        System.out.println(greenApples);

        System.out.println("\n--- 2. 사과를 무게로 변환 (Function & Method Ref) ---");
        // T(Apple) -> R(Integer) 변환
        // 람다: apple -> apple.weight()
        // 참조: Apple::weight (훨씬 깔끔하죠?)
        List<Integer> weights = map(inventory, Apple::weight);
        System.out.println(weights);

        System.out.println("\n--- 3. 결과 출력 (Consumer & Method Ref) ---");
        // T(Integer) -> void 소비
        // 람다: w -> System.out.println(w)
        // 참조: System.out::println
        forEach(weights, System.out::println);
    }

    // --- [핵심] 우리가 직접 구현한 "Mini Stream API" ---
    // 나중에 배울 stream().filter()가 내부적으로 이렇게 생겼습니다.
    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        for (T e : list) {
            if (p.test(e)) { // Predicate의 test() 메서드로 검사
                result.add(e);
            }
        }
        return result;
    }

    // 나중에 배울 stream().map()의 원리입니다.
    public static <T, R> List<R> map(List<T> list, Function<T, R> f) {
        List<R> result = new ArrayList<>();
        for (T e : list) {
            result.add(f.apply(e)); // Function의 apply() 메서드로 변환
        }
        return result;
    }

    // 나중에 배울 stream().forEach()의 원리입니다.
    public static <T> void forEach(List<T> list, Consumer<T> c) {
        for (T e : list) {
            c.accept(e); // Consumer의 accept() 메서드로 소비
        }
    }
}
