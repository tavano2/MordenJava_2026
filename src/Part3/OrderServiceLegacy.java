package Part3;

public class OrderServiceLegacy {

    enum OrderStatus {
        PAYMENT_WAITING, PREPARING, SHIPPED, DELIVERED, CANCELED
    }

    public static String getStatusMessageTobe(OrderStatus status, boolean isUrgent) {
        return switch (status) {
            case OrderStatus.PAYMENT_WAITING -> "입금 확인 중입니다.";
            case OrderStatus.PREPARING -> {
                if (isUrgent) {
                    System.out.println("🚨 긴급 주문 발생! 창고에 알림 발송!");
                    yield "긴급 출고 준비 중!";
                } else {
                    yield "상품 준비 중입니다.";
                }
            }
            case OrderStatus.SHIPPED -> "배송이 시작되었습니다.";
            case OrderStatus.DELIVERED -> "배송 완료";
            case OrderStatus.CANCELED -> "주문 취소됨";
        };
    }

    // java 17버전일 때 위 문법대로 작성하면 컴파일 에러 발생
    public String getStatusMessageTobeJava17(OrderStatus status, boolean isUrgent) {
        return switch (status) {
            // Enum 타입명(OrderStatus) 제거
            case PAYMENT_WAITING -> "입금 확인 중입니다.";

            case PREPARING -> {
                if (isUrgent) {
                    System.out.println("🚨 긴급 주문 발생! 창고에 알림 발송!");
                    // yield: 이 블록(중괄호)을 탈출하며 값을 던져준다.
                    yield "긴급 출고 준비 중!";
                } else {
                    yield "상품 준비 중입니다.";
                }
            }

            case SHIPPED -> "배송이 시작되었습니다.";
            case DELIVERED -> "배송 완료";
            case CANCELED -> "주문 취소됨";

            // default가 없어도 되는 이유:
            // OrderStatus의 모든 상수를 다 적었기 때문에 컴파일러가 안심함.
        };
    }

    static void main() {
        OrderStatus sta = OrderStatus.CANCELED;
        String test = getStatusMessageTobe(sta, true);
        System.out.println(test);
    }
}