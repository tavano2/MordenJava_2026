package example.Part2;
import java.util.*;
import java.util.stream.Collectors;

public class RefactoringChallenge2 {

    // Modern Java: 데이터 클래스는 record로 간단하게 선언
    public record Product(Long id, String name, String category, boolean isActive) {}

    public static void main(String[] args) {
        List<Product> products = List.of(
                new Product(1L, "MacBook Pro", "Electronics", true),
                new Product(2L, "iPhone 15", "Electronics", true),
                new Product(3L, "Nike Shoes", "Fashion", true),
                new Product(4L, "Adidas Jacket", "Fashion", false), // 판매 중지
                new Product(5L, "MacBook Air", "Electronics", true)
        );

        LegacyShopService service = new LegacyShopService();

        // 1. Active Products
        System.out.println("1. Active Products: " + service.findActiveProducts(products));

        // 2. Unique Categories
        System.out.println("2. Categories: " + service.getUniqueCategories(products));

        // 3. Product Map (ID -> Name)
        System.out.println("3. Map Cache: " + service.convertToMap(products));
    }

    static class LegacyShopService {

        // [문제 1] 판매 중인(isActive) 상품만 필터링하여 '불변 리스트'로 반환하세요.
        // Hint: Java 16의 Stream.toList() 활용
        public List<Product> findActiveProducts(List<Product> products) {
              return products.stream().filter(Product::isActive).toList();
        }

        // [문제 2] 모든 상품의 카테고리를 중복 없이 추출하여 Set으로 반환하세요.
        public Set<String> getUniqueCategories(List<Product> products) {
            return products.stream().map(Product::category).collect(Collectors.toSet());
        }

        // [문제 3] 상품 ID를 Key, 상품 이름을 Value로 하는 Map을 만드세요.
        // Hint: Collectors.toMap(keyMapper, valueMapper)
        public Map<Long, String> convertToMap(List<Product> products) {
            return products.stream().collect(Collectors
                    .toMap(Product::id,
                            Product::name,
                            (oldVal, newVal) -> newVal));
        }
    }
}
