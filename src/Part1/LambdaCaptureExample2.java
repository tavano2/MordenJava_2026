package Part1;

// 참조값은 final이지만, 객체 상태는 바꿀 수 있다
public class LambdaCaptureExample2 {

    static class Counter {
        private int value = 0;

        void increment() {
            value++;
        }

        int getValue() {
            return value;
        }
    }

    public static void main(String[] args) {

        Counter counter = new Counter();

        Runnable task = () -> {
            counter.increment(); // ✅ 가능
            System.out.println("counter value = " + counter.getValue());
        };

        task.run();
        task.run();

        // counter = new Counter(); // ❌ 컴파일 에러
    }
}
