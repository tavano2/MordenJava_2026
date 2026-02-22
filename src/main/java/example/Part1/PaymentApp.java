package example.Part1;

public class PaymentApp {

    // 1. [Strategy Interface] 결제라는 행위(알고리즘)를 추상화
    interface PaymentStrategy {
        void pay(int amount);
    }

    // 2. [Context] 결제 서비스 (전략을 사용하는 주체)
    static class PaymentService {
        // 이 서비스는 '어떻게(How)' 결제하는지 몰라도 됩니다.
        // 누군가 주입해준 전략을 실행(What)할 뿐입니다.
        public void processOrder(int amount, PaymentStrategy strategy) {
            System.out.println("--- 주문 생성 중 ---");
            strategy.pay(amount); // 핵심: 주입받은 전략의 메서드를 실행 (위임)
            System.out.println("--- 주문 완료 ---\n");
        }
    }

    public static void main(String[] args) {
        PaymentService service = new PaymentService();

        // Case A. [Classic Java] 클래스로 정의된 전략 사용
        // 보통은 별도 파일로 클래스를 만들어서 new로 주입합니다.
        service.processOrder(10000, new PaymentStrategy() {
            @Override
            public void pay(int amount) {
                System.out.println("💳 신용카드로 " + amount + "원 결제");
            }
        });

        // Case B. [Modern Java] 람다를 이용한 전략
        // 상황: "이번만 특별히 네이버페이로 결제할래요. 클래스 따로 만들기 귀찮아요."
        // 해석: PaymentStrategy 인터페이스가 메서드 하나짜리니까 람다로 즉석 구현 가능!
        service.processOrder(25000, amount ->
                System.out.println("🟩 네이버페이로 " + amount + "원 결제 (람다)")
        );
    }
}

