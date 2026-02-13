package Part3;

public class LegacySearchRequest {
    private String keyword;
    private int page;
    private int size;

    public LegacySearchRequest(String keyword, int page, int size) {
        this.keyword = (keyword == null) ? "" : keyword;
        this.page = (page < 0) ? 0 : page;
        this.size = (size > 100) ? 100 : size; // 최대 100개 제한
    }

    // ... Getter/Setter 생략 ...
    // ... equals/hashCode/toString 생략 (Lombok 없던 시절)...

    public String getDebugQuery() {
        return "SELECT * FROM items WHERE name LIKE '%" + keyword + "%' " +
                "LIMIT " + size + " OFFSET " + (page * size);
    }
}

//public record LegacySearchRequestTobe(String keyword, int page, int size) {
//    public LegacySearchRequestTobe {
//        keyword = (keyword == null) ? "" : keyword;
//        page = Math.max(page, 0);
//        size = Math.min(size, 100);
//    }
//    public String getDebugQuery() {
//        return """
//                SELECT * FROM items
//                WHERE name LIKE %%%s%%
//                LIMIT %d OFFSET (%d*%d)
//                """.formatted(keyword, size, page, size);
//    }
//}

//public record LegacySearchRequestTobeS(String keyword, int page, int size) {
//    // Compact Constructor: 파라미터() 생략
//    public LegacySearchRequestTobe {
//        // 1. Normalization
//        keyword = (keyword == null) ? "" : keyword;
//        page = Math.max(page, 0);
//
//        // 2. Logic Correction: size < 1 체크 추가
//        if (size < 1) {
//            size = 10; // 기본값
//        } else {
//            size = Math.min(size, 100); // 최대값 제한
//        }
//    }

//    public String getDebugQuery() {
//        // 3. Calculation: 계산은 자바가 더 빠르고 명확하다.
//        int offset = page * size;
//
//        // 4. Text Block & Formatting
//        return """
//                SELECT * FROM items
//                WHERE name LIKE '%%%s%%'
//                LIMIT %d OFFSET %d
//                """.formatted(keyword, size, offset);
//    }
//}