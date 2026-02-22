package example.Predicate;

import java.util.*;

public class CodeKata20260211Two {

    public record Doc(
            Long id,
            String title,
            String content,
            boolean isDeleted
    ) {}

    // 문제 1 "삭제된 문서 정리하기" (List.removeIf)
    public void purgeDeletedDocs(List<Doc> docs) {
        Iterator<Doc> it = docs.iterator();
        while (it.hasNext()) {
            Doc doc = it.next();
            if (doc.isDeleted()) {
                it.remove(); // 지저분한 Iterator 호출
            }
        }
    }
    // S (Perfect Execution)
    public void purgeDeletedDocsTobe(List<Doc> docs) {
        docs.removeIf(Doc::isDeleted);
    }
    // 문제 2 "제목 일괄 수정" (List.replaceAll)
    public void censorTitles(List<Doc> docs) {
        for (int i = 0; i < docs.size(); i++) {
            Doc doc = docs.get(i);
            if (doc.title().contains("bad")) {
                String newTitle = doc.title().replace("bad", "**");
                // 새로운 객체로 갈아끼우기 (불변 객체이므로)
                docs.set(i, new Doc(doc.id(), newTitle, doc.content(), doc.isDeleted()));
            }
        }
    }
    // A (Excellent)
    // 만약 조건을 확인하지 않고 무조건 new Doc(...)을 반환했다면, 제목이 정상적인 문서들까지도 불필요하게 객체를 새로 생성(Allocation)했을 겁니다.
    // 작성하신 코드는 **"바꿀 필요가 있을 때만 바꾼다"**는 효율성을 챙겼습니다.
    public void censorTitlesTobe(List<Doc> docs) {
        docs.replaceAll(doc ->{
            if(doc.title().contains("bad")) {
                String newTitle = doc.title().replace("bad", "**");
                return new Doc(doc.id(),newTitle,doc.content(), doc.isDeleted());
            }
            return doc;
        } );
    }
    // S등급 코드
    public void censorTitlesTobeS(List<Doc> docs) {
        docs.replaceAll(doc ->
                doc.title().contains("bad")
                        ? new Doc(doc.id(), doc.title().replace("bad", "**"), doc.content(), doc.isDeleted())
                        : doc
        );
    }

    // 문제 3 "설정값 초기화" (Map.of / List.of)
    public Map<String, List<String>> getDefaultConfig() {
        // 1. 리스트 생성
        List<String> adminRoles = new ArrayList<>();
        adminRoles.add("ROOT");
        adminRoles.add("SUPER");

        // 2. 맵 생성 및 값 주입
        Map<String, List<String>> config = new HashMap<>();
        config.put("admins", Collections.unmodifiableList(adminRoles));
        config.put("users", Collections.emptyList());

        // 3. 불변으로 감싸기
        return Collections.unmodifiableMap(config);
    }
    // 결과 S (Perfect Execution)
    // "10줄짜리 코드를 1줄로" 줄여버리는 마법을 제대로 보여주셨습니다.
    // Map.of와 List.of는 단순히 코드를 줄이는 것을 넘어, **"불변성(Immutability)"**을 언어 차원에서 보장한다는 점에서 아주 강력합니다.
    public Map<String, List<String>> getDefaultConfigTobe() {
        return Map.of("admins", List.of("ROOT", "SUPER"),"users", Collections.emptyList());
    }

}
