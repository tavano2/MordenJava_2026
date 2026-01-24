package Part1;

// 람다는 변수가 아니라 “값”을 캡처한다
public class LambdaCaptureExample1 {

    public static void main(String[] args) {

        int count = 0;

        Runnable task = () -> {
            // count++; // ❌ 컴파일 에러 발생
            System.out.println("count = " + count);
        };

        task.run();
        // “람다가 캡처 할 수 있으려면 그 변수가 끝까지 안 변해야 해서” 주석을 풀면 에러가 발생함.
        // 람다에서 사용하는 지역 변수는 불변이어야 한다
        // count = 1; // ❌ 이것도 컴파일 에러
    }
}
