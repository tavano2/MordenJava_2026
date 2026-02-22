package example.Predicate;

// 문제 1
// 아래의 클래스를 레코드로 한줄 처리하기.
//public final class PaymentRequest {
//    private final String orderId;
//    private final long amount;
//    private final String currency;
//
//    public PaymentRequest(String orderId, long amount, String currency) {
//        this.orderId = orderId;
//        this.amount = amount;
//        this.currency = currency;
//    }
//
//    public String getOrderId() { return orderId; }
//    public long getAmount() { return amount; }
//    public String getCurrency() { return currency; }
//
//    @Override
//    public boolean equals(Object o) { /* 생략 */ return true; }
//    @Override
//    public int hashCode() { /* 생략 */ return 0; }
//    @Override
//    public String toString() { /* 생략 */ return ""; }
//}

// 등급: S (Perfect Execution)
// "20줄짜리 코드를 단 1줄로" 압축했습니다. 이것이 바로 모던 자바가 추구하는 **"Boilerplate(상용구) 제거"**의 정수입니다.
public record  PaymentRequest(String orderId, long amount, String currency) {}

// [Senior Tip] Compact Constructor (유효성 검증)
// DTO를 만들 때 "가격이 음수면 안 된다" 같은 검증 로직이 필요하죠?
// ecord는 **"Compact Constructor"**라는 아주 우아한 문법을 지원합니다.

//public record PaymentRequest(String orderId, long amount, String currency) {
//    // 괄호 () 없이 생성자 이름만 작성
//    public PaymentRequest {
//        if (amount < 0) {
//            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
//        }
//        // this.amount = amount; <-- 이 코드는 컴파일러가 알아서 해줍니다. 생략 가능!
//    }
//}
