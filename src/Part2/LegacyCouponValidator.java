package Part2;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LegacyCouponValidator {

    // as-is
//    static class Coupon {
//        String name;
//        String status; // "ACTIVE", "INACTIVE"
//        int discountPercent;
//        LocalDate expiryDate;
//
//        public Coupon(String name, String status, int discountPercent, LocalDate expiryDate) {
//            this.name = name;
//            this.status = status;
//            this.discountPercent = discountPercent;
//            this.expiryDate = expiryDate;
//        }
//    }

    // to-be
    record Coupon (String name, String status, int discountPercent, LocalDate expiryDate) {}

    public static void main(String[] args) {
        List<Coupon> coupons = Arrays.asList(
                new Coupon("Welcome", "INACTIVE", 30, LocalDate.now().plusDays(5)),
                new Coupon("Summer Sale", "ACTIVE", 10, LocalDate.now().plusDays(3)),
                new Coupon("VIP Special", "ACTIVE", 25, LocalDate.now().plusDays(1)), // Target!
                new Coupon("Black Friday", "ACTIVE", 50, LocalDate.now().minusDays(1)) // Expired
        );

        // Coupon validCoupon = null;

        // Dirty Legacy Logic -----------------------------------------
//        for (Coupon c : coupons) {
//            if ("ACTIVE".equals(c.status)) {
//                if (c.expiryDate.isAfter(LocalDate.now())) {
//                    if (c.discountPercent >= 20) {
//                        validCoupon = c;
//                        break; // 찾았으니 탈출
//                    }
//                }
//            }
//        }
        // -----------------------------------------------------------

        // to-be
        Optional<Coupon> validCoupon = coupons.stream()
                .filter(c -> c.status().equals("ACTIVE"))
                .filter(c -> c.expiryDate().isAfter(LocalDate.now()))
                .filter(c -> c.discountPercent() >= 20)
                .findFirst();

        if (validCoupon.isPresent()) {
            System.out.println("Applied Coupon: " + validCoupon.get().name());
        } else {
            throw new RuntimeException("No available coupon found!");
        }

//        if (validCoupon != null) {
//            System.out.println("Applied Coupon: " + validCoupon.name);
//        } else {
//            throw new RuntimeException("No available coupon found!");
//        }
    }
}
