package Part3;

public class TowWeekPredicate {

    public sealed interface PaymentEvent permits CardApproved, BankTransferPending, PaymentRejected {};

    public record CardApproved(String cardNumber, int amount) implements PaymentEvent {};
    public record BankTransferPending(String accountId) implements PaymentEvent {};
    public record PaymentRejected(String reason) implements PaymentEvent {};

    public static void processEvent(PaymentEvent event) {
        if (event instanceof CardApproved c) {
            System.out.println("카드 번호는 다음과 같습니다 ::" + c.cardNumber());
            System.out.println("결제 금액은 다음과 같습니다 ::" + c.amount());
        } else if (event instanceof BankTransferPending b) {
            System.out.println("계정 아이디는 다음과 같습니다 ::" + b.accountId());
        } else if (event instanceof PaymentRejected p) {
            System.out.println("결제 거부 사유는 다음과 같습니다 ::" + p.reason());
        } else {
            throw new IllegalArgumentException("알 수 없는 타입입니다.");
        }
    }

    public static String getEventMessage(PaymentEvent event) {
        // [Modern Java 21 Switch Pattern Matching]
        return switch (event) {
            // 1. 타입 패턴 매칭 + 화살표 구문
            case CardApproved c -> "카드 결제 승인: " + c.amount();

            // 2. 가드(Guard) 패턴: 'when' 키워드로 세부 조건 추가 (Java 21)
            case PaymentRejected p when p.reason().contains("잔액") ->
                    "잔액 부족으로 거절됨";

            case PaymentRejected p -> "결제 거절 사유: " + p.reason();

            case BankTransferPending b -> "계좌 이체 대기중: " + b.accountId();

            // 🚨 핵심: default가 필요 없다!
            // 만약 PaymentEvent에 새로운 자식이 추가되면 여기서 컴파일 에러가 발생한다.
        };
    }


}
