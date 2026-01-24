package Part1;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppleFarmApp {
    // [Modern Java Tip] Java 14+ Record
    // : 불변 데이터 객체(DTO)를 위한 획기적인 단축 문법입니다.
    // : getter, toString, equals, hashCode가 자동으로 생성됩니다.
    // : (주의) getter 메서드 이름에 get이 붙지 않습니다. (apple.color())
    public record Apple(String color, int weight) {}

    // 1. 동작(Behavior)을 정의할 인터페이스
    // : "사과를 검사하는 기준"을 추상화합니다.
    interface ApplePredicate {
        boolean test(Apple apple);
    }

    // 2. 만능 필터 메서드 (동작 파라미터화 적용)
    // : 이제 더 이상 메서드를 여러 개 만들 필요가 없습니다.
    // : '어떤 기준(p)'으로 거를지만 알려주면 됩니다.
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) { // 👈 동작(p)을 실행(Execute)하는 부분
                result.add(apple);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        // 재고 목록 준비 (Java 9+ List.of 사용)
        List<Apple> inventory = List.of(
                new Apple("GREEN", 100),
                new Apple("RED", 160),
                new Apple("GREEN", 200)
        );

        System.out.println("--- [Step 1] 익명 클래스 (Java 7 스타일) ---");
        // 과도기: 인터페이스를 구현하는 클래스를 즉석에서 만듦
        // 문제점: 코드가 너무 깁니다. 핵심 로직은 한 줄인데, 껍데기가 4줄이나 되죠.
        List<Apple> heavyApples = filterApples(inventory, new ApplePredicate() {
            @Override
            public boolean test(Apple apple) {
                return apple.weight() > 150; // 👈 핵심 로직
            }
        });
        System.out.println("무거운 사과: " + heavyApples);


        System.out.println("\n--- [Step 2] 람다 표현식 (Java 8+ 스타일) ---");
        // 혁신: 껍데기를 다 벗겨내고 '동작'만 남김!
        // 문법: (파라미터) -> { 바디 }
        List<Apple> greenApples = filterApples(inventory, apple -> "GREEN".equals(apple.color()));
        System.out.println("초록 사과: " + greenApples);

        // 연습: "빨갛고 무거운 사과"도 메서드 추가 없이 즉석에서 해결!
        List<Apple> redAndHeavy = filterApples(inventory, apple ->
                "RED".equals(apple.color()) && apple.weight() > 150
        );
        System.out.println("빨갛고 무거운 사과: " + redAndHeavy);

        // inventory 리스트를 무게(weight) 순으로 정렬
        inventory.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {
                return Integer.compare(o1.weight(), o2.weight());
            }
        });
        System.out.println("java 7 sort:: " + inventory);

        inventory.sort((a1, a2) -> Integer.compare(a1.weight(), a2.weight()));

    }
}
