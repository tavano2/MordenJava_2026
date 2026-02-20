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

}
