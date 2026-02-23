package example.Part3.finalchllange;

public record SettlementResult(
        String txId,
        double finalAmount,
        String report
) {
    public static SettlementResult calculateSettlement(Transaction tsx) {
        return switch (tsx) {
            // 1. 특수 케이스 (환불/VIP/특정수단)
            case Transaction(String id, Payment p, Refunded r) ->
                    new SettlementResult(id, p.amount() * -1.0, "환불 처리됨");

            case Transaction(String id, CreditCard c, Success s) when c.amount() >= 10000 ->
                    new SettlementResult(id, c.amount() * 1.01, "VIP 1% 캐시백 적용");
            case Transaction(String id, CreditCard c, Success s) ->
                    new SettlementResult(id, c.amount(), "일반 카드 결제");

            case Transaction(String id, BankTransfer b, Pending p) ->
                    new SettlementResult(id, 0.0, "은행 승인 대기 중(정산 제외)");
            case Transaction(String id, BankTransfer b, Success s) ->
                    new SettlementResult(id, b.amount(), "계좌 이체 정산 완료");

            case Transaction(String id, CryptoCurrency cry, Failed f) -> {
                // 로깅 후 '정산 제외' 객체 반환 (null 방지)
                System.out.println("보안팀 확인 필요: " + id);
                yield new SettlementResult(id, 0.0, "보안 검토 대상");
            }

            // 2. 나머지 공통 케이스 (DOP의 정수)
            case Transaction(String id, Payment p, Success s) ->
                    new SettlementResult(id, p.amount(), "결제 성공");

            case Transaction(String id, Payment p, Failed f) ->
                    new SettlementResult(id, 0.0, "결제 실패(정산 제외)");

            case Transaction(String id, Payment p, Pending pen) ->
                    new SettlementResult(id, 0.0, "결제 진행 중");
        };
    };
}
