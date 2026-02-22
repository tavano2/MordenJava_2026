package example.Part1;
import java.util.*;

public class GenericQuiz {
    // 1. 제네릭 인터페이스 정의
    interface MyPredicate<T> {
        boolean test(T t);
    }

    // 2. 제네릭 필터 메서드 구현
    public static <T> List<T> filter(List<T> list, MyPredicate<T> p) {
        List<T> result = new ArrayList<>();
        for (T e : list) {
            if (p.test(e)) {
                result.add(e);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);

        // 3. 람다를 사용해 짝수만 필터링 (정답을 채워보세요)
        List<Integer> evenNumbers = filter(numbers, p-> p%2 == 0);

        System.out.println(evenNumbers); // 출력: [2, 4, 6]
    }
}