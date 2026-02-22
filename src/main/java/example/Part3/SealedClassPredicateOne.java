package example.Part3;

public class SealedClassPredicateOne {

    public sealed interface FinancialUpdate permits StockPrice, MarketStatus, SystemAlert {}

    public record StockPrice(String symbol, double price, double changeRate) implements FinancialUpdate {}
    public record MarketStatus(String marketName, boolean isOpen) implements FinancialUpdate {}
    public record SystemAlert(int level, String message) implements FinancialUpdate {}

    public static String handleUpdate(FinancialUpdate update) {
        return switch (update) {
            case StockPrice s when s.changeRate() >= 5.0 -> "급등주 포착: " + s.symbol();
            case StockPrice s  -> "가격 정보: " + s.symbol() + " " + s.price();
            case MarketStatus m when !m.isOpen() -> "시장 마감";
            case MarketStatus m -> "시장이 개장되었습니다";
            case SystemAlert sy when sy.level() == 1 -> "경고: " + sy.message();
            case SystemAlert sy -> "알림: " + sy.message();
        };
    }

    public static void main(String[] args) {
        FinancialUpdate event = new StockPrice("hello", 10.2, 1.1);
        System.out.println(handleUpdate(event));
    }
}
