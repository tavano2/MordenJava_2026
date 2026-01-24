package Part2;
import java.util.Arrays;
import java.util.List;

public class ShoppingCart {

    enum Category { ELECTRONICS, CLOTHING, FOOD }

    record CartItem(String name, Category category, long price) {}

    record Bill(long totalOriginalPrice, long totalFinalPrice) {
        // Combiner용 병합 메서드
        public Bill merge(Bill other) {
            return new Bill(
                    this.totalOriginalPrice + other.totalOriginalPrice,
                    this.totalFinalPrice + other.totalFinalPrice
            );
        }
    }

    public static void main(String[] args) {
        List<CartItem> cart = Arrays.asList(
                new CartItem("iPad", Category.ELECTRONICS, 100000), // 10% Off -> 90000
                new CartItem("T-Shirt", Category.CLOTHING, 50000),  // 20% Off -> 40000
                new CartItem("Apple", Category.FOOD, 5000)          // 0% Off  -> 5000
        );

        Bill finalBill = calculateTotalBill(cart);

        System.out.println("Original: " + finalBill.totalOriginalPrice()); // 예상: 155000
        System.out.println("Final:    " + finalBill.totalFinalPrice());    // 예상: 135000
    }

    public static Bill calculateTotalBill(List<CartItem> items) {
        return items.parallelStream()
                .reduce(
                        // 1. Identity
                        new Bill(0, 0),

                        // 2. Accumulator (여기에 비즈니스 로직 구현!)
                        (bill, item) -> {
                            long discountedPrice = item.price();

                            // TODO: 카테고리에 따른 할인 로직 구현
                            // ELECTRONICS -> 10% 할인
                            // CLOTHING -> 20% 할인
                            // FOOD -> 그대로
                            switch (item.category()) {
                                case ELECTRONICS -> discountedPrice = (long) (discountedPrice * 0.9);
                                case CLOTHING -> discountedPrice = (long) (discountedPrice * 0.8);
                            }

                            return new Bill(
                                    bill.totalOriginalPrice() + item.price(),
                                    bill.totalFinalPrice() + discountedPrice
                            );
                        },

                        // 3. Combiner
                        Bill::merge
                );
    }
}
