package Part2;
import java.util.*;
import java.util.stream.Collectors;

public class GroupingExperiment {

    enum Category { ELECTRONICS, FASHION, HOME }

    // 데이터 구조 (Java 14 Record)
    record Product(String name, String category, int price, boolean isSale) {}

    public static void main(String[] args) {
        List<Product> products = List.of(
                new Product("Laptop", "Electronics", 1200, false),
                new Product("Phone", "Electronics", 800, true),
                new Product("TV", "Electronics", 1500, true),
                new Product("Sneakers", "Fashion", 100, false),
                new Product("T-Shirt", "Fashion", 50, true),
                new Product("Sofa", "Home", 500, false)
        );

        // 여기서부터 실험 코드를 하나씩 작성할 겁니다.
        // experiment1_Grouping(products);
        // experiment2_Partitioning(products);
        // experiment3_Grouping(products);
        experiment4_Downstream(products);
    }

    private static void experiment1_Grouping(List<Product> products) {
        System.out.println("=== 1. Grouping (Classification) ===");

        // [실험] 카테고리(String)를 기준으로 그룹핑
        Map<String, List<Product>> byCategory = products.stream()
                .collect(Collectors.groupingBy(Product::category));
        // Product::category 대신 p -> p.price() > 1000 ? "High" : "Low" 라는 람다식을 분류 함수로 넣는다면,
        // 결과 Map의 Key는 어떤 형태가 될까
        // key : high , low로 반환

        // 결과 출력
        byCategory.forEach((key, list) -> {
            System.out.println("Category: " + key + " -> Count: " + list.size());
            list.forEach(System.out::println); // 내부 데이터 확인용
        });
    }

    private static void experiment2_Partitioning(List<Product> products) {
        System.out.println("\n=== 2. Partitioning (Boolean Logic) ===");

        // [실험] 세일 중(isSale)인 상품과 아닌 상품 분할
        Map<Boolean, List<Product>> partitioned = products.stream()
                .collect(Collectors.partitioningBy(Product::isSale));

        partitioned.forEach((key, list) -> {
            System.out.println("On Sale: " + key + " -> Count: " + list.size());
        });
    }

    private static void experiment3_Grouping(List<Product> products) {
        System.out.println("=== 3. Enum ===");
        Map<Category, List<Product>> byEnum = products.stream()
                .collect(Collectors.groupingBy(product ->
                        Category.valueOf(product.category().toUpperCase())));

        byEnum.forEach((key, list) -> {
            System.out.println("Enum: " + key + " -> Count: " + list.size());
        });
    }

    private static void experiment4_Downstream(List<Product> products) {
        System.out.println("=== 4. Downstream (Total Price per Category) ===");

        // [미션] 카테고리(Enum)별 상품 가격의 총합을 구하세요.
        Map<Category, Integer> totalPriceByCategory = products.stream()
                .collect(Collectors.groupingBy(
                        // 1. 분류 기준 (Key): String -> Enum 변환
                        product -> Category.valueOf(product.category().toUpperCase()),

                        // 2. 다단 집계 (Value): 리스트 대신 가격의 합계를 원함
                        /* 이곳을 채워보세요 (Hint: summingInt) */
                        Collectors.summingInt(Product::price)
                ));

        totalPriceByCategory.forEach((key, sum) -> {
            System.out.println("Category: " + key + " -> Total Price: $" + sum);
        });
    }
}
