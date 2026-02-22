package example.Part2;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class LegacyCouponValidatorTobe {
    // to-be
    record Coupon (String name, String status, int discountPercent, LocalDate expiryDate) {}

    public static void main(String[] args) {
        List<Coupon> coupons = Arrays.asList(
                new Coupon("Welcome", "INACTIVE", 30, LocalDate.now().plusDays(5)),
                new Coupon("Summer Sale", "ACTIVE", 10, LocalDate.now().plusDays(3)),
                new Coupon("VIP Special", "ACTIVE", 25, LocalDate.now().plusDays(1)), // Target!
                new Coupon("Black Friday", "ACTIVE", 50, LocalDate.now().minusDays(1)) // Expired
        );
        // to-be
        Coupon validCoupon = coupons.stream()
                .filter(c -> "ACTIVE".equals(c.status())) // NPE 방지를 위한 Yoda Condition 권장
                .filter(c -> c.expiryDate().isAfter(LocalDate.now()))
                .filter(c -> c.discountPercent() >= 20)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available coupon found!"));
                // 값을 찾으면 반환하고, 없으면 즉시 예외 던짐 (if-else 제거)

        System.out.println("Applied Coupon: " + validCoupon.name());
    }
}
