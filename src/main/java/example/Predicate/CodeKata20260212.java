package example.Predicate;

public class CodeKata20260212 {

    // 문제 2 "조건부 할인 로직의 캡슐화" (Rich Enum)
    // B (Good Start, But...)
    public enum PayType {
        CASH, CARD, POINT;

        public long applyDiscount(PayType type, long amount) {
            if (type == PayType.CASH) {
                return (long) (amount * 0.9); // 10% 할인
            } else if (type == PayType.CARD) {
                return (long) (amount * 0.95); // 5% 할인
            } else if (type == PayType.POINT) {
                return amount; // 할인 없음
            }
            return amount;
        }
    }
    // Service 로직
    public long calculateDiscountedAmount(PayType type, long amount) {
        return type.applyDiscount(type, amount);
    }
    // 총평: 일단 로직을 Enum 내부로 가져오려는 시도(Encapsulation)는 아주 좋습니다. 하지만 "아쉬운 점이 두 가지" 있습니다.
    // 불필요한 인자: type.applyDiscount(type, amount) -> 자기 자신(this)이 이미 누군지 아는데, 굳이 type을 인자로 또 받을 필요가 없습니다.
    // 여전한 if-else: 장소만 Service에서 Enum으로 옮겼을 뿐, if-else 분기문은 살아있습니다. 이것은 **객체지향의 다형성(Polymorphism)**을 100% 활용하지 못한 것입니다.
    // 자세한 내용은 20260212 ENUM 참고


    // 문제 3 "결제 상태 메시지 처리" (Switch Expressions)
    public String getStatusMessage(String status) {
        String message;
        switch (status) {
            case "READY":
                message = "결제 대기 중입니다.";
                break;
            case "COMPLETED":
                message = "결제가 완료되었습니다.";
                break;
            case "FAILED":
                message = "결제에 실패했습니다.";
                break;
            case "CANCELLED":
                message = "취소된 주문입니다.";
                break;
            default:
                message = "알 수 없는 상태입니다.";
        }
        return message;
    }

    // 등급: B+ (Good Attempt)
    // 작성하신 코드는 여전히 message 변수에 값을 할당하는 "Switch Statement(문)" 스타일입니다.
    // 상세한 내용은 20260212 Switch Expression 참고
    public String getStatusMessageTobe(String status) {
        String message;
        switch (status) {
            case "READY" -> message = "결제 대기 중입니다.";
            case "COMPLETED" -> message = "결제가 완료되었습니다.";
            case "FAILED" -> message = "결제에 실패했습니다.";
            case "CANCELLED" -> message = "취소된 주문입니다.";
            default ->  message = "알 수 없는 상태입니다.";
        }
        return message;
    }

}
