package Part3;
import java.util.Objects;

public class RecordDeepDive {

    // --- 1. The Record (Modern DTO) ---
    // Lombok @Value, @Builder 없이도 완벽한 불변 객체
    public record Product(String name, int price, String category) {

        // [Core Feature] Compact Constructor (간결한 생성자)
        // 파라미터 선언부()가 없다! -> 중복 코드를 제거함.
        // 여기서 유효성 검사(Validation)와 데이터 정제(Normalization)를 수행한다.
        public Product {
            // 1. Validation: "방어적 복사" 없이 바로 필드 접근 가능
            Objects.requireNonNull(name, "상품명은 필수입니다.");

            if (price < 0) {
                throw new IllegalArgumentException("가격은 0원 이상이어야 합니다: " + price);
            }

            // 2. Normalization: 입력된 데이터를 "보정"해서 저장할 수 있다.
            // (this.category = ... 가 아니라, 그냥 파라미터 변수에 재할당하면 됨)
            if (category == null) {
                category = "General"; // null이 들어오면 기본값 설정
            } else {
                category = category.toUpperCase().trim(); // 대문자 변환 & 공백 제거
            }

            // 컴파일러가 마지막에 자동으로 `this.name = name;` 등을 수행해 줌.
            // 개발자는 비즈니스 로직(검증/보정)에만 집중하면 됨.
        }

        // 추가 메서드도 자유롭게 정의 가능
        public boolean isExpensive() {
            return this.price > 10000;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Record Validation & Normalization ===");

        // 정상 케이스 (Category 소문자 입력 -> 대문자로 보정됨)
        Product p1 = new Product("  MacBook Pro  ", 3500, "notebook");
        // 주의: Record는 불변이지만, 필드 자체(String)가 불변이 아니면
        // 입력받은 참조를 그대로 쓰므로 외부에서 변경될 수 있음. (String은 불변이라 안전)

        System.out.println(p1);
        System.out.println(p1.isExpensive());
        // 출력: Product[name=  MacBook Pro  , price=3500, category=NOTEBOOK]
        // (name은 trim 안 했고, category는 trim+upper 했음을 확인)


        System.out.println("\n=== 2. Text Block Magic (Java 15) ===");

        // JSON 예시 (이스케이프 시퀀스 \ 없이 그대로 복사-붙여넣기)
        String jsonBlock = """
            {
                "product_name": "%s",
                "price": %d
            }
            """.formatted(p1.name().trim(), p1.price());

        System.out.println(jsonBlock);

        // SQL 예시 (가독성 끝판왕)
        // .stripIndent()는 컴파일러가 자동으로 해주지만, 명시적으로 호출도 가능.
        String sql = """
            SELECT *
            FROM product
            WHERE price > 1000
              AND category = 'NOTEBOOK'
            """;

        System.out.println("SQL Query:\n" + sql);
    }
}
