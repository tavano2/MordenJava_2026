package example.Predicate;
import java.util.*;
import java.util.stream.Collectors;

public class CodeKata20260211 {

    public record Product(
            String id,
            String name,
            String category,
            int quantity
    ) {}

    // 문제1 "카테고리별 상품 목록" (computeIfAbsent)
    // 상품 리스트를 순회하면서 카테고리별로 상품 이름을 List에 담아야 합니다.
    public Map<String, List<String>> groupProductsByCategory(List<Product> products) {
        Map<String, List<String>> categoryMap = new HashMap<>();

        for (Product p : products) {
            if (p != null) {
                String category = p.category();
                // 1. 키가 있는지 확인
                if (!categoryMap.containsKey(category)) {
                    // 2. 없으면 빈 리스트 생성 후 put
                    categoryMap.put(category, new ArrayList<>());
                }
                // 3. 리스트 꺼내서 add
                categoryMap.get(category).add(p.name());
            }
        }
        return categoryMap;
    }

    // C (Logic Error)
    // "빈 리스트만 만들고 끝?" -> computeIfAbsent는 **"키가 없으면 값을 계산해서 넣고, 그 값(Value)을 반환"**합니다.
    // 현재 코드는 빈 리스트만 맵에 넣어두고, 정작 상품 이름(p.name())은 담지 않았습니다.
    public Map<String, List<String>> groupProductsByCategoryTobe(List<Product> products) {
        Map<String, List<String>> categoryMap = new HashMap<>();
        for (Product p : products) {
            if (p != null) {
                String category = p.category();
                categoryMap.computeIfAbsent(category, s -> new ArrayList<>());
            }
        }
        return categoryMap;
    }

    // 두번쨰 인자에 람다 함수를 넣는 이유
    // [가상 시나리오] 람다가 아니라 객체를 바로 받는다면?
    // map.computeIfAbsent(key, new ArrayList<>());
    // 쓸데없이 빈 리스트 객체를 하나 만들었다가 버리게 됩니다. (Garbage Collection 대상 증가)
    // 람다로 작성 할 시에는
    // 먼저 맵에 key가 있는지 확인합니다.
    // 있으면? 람다(new ...)를 실행조차 하지 않습니다. (비용 0)
    // 없으면? 그때서야 비로소 람다를 실행해서 객체를 만듭니다.
    public Map<String, List<String>> groupProductsByCategoryTobeS(List<Product> products) {
        Map<String, List<String>> categoryMap = new HashMap<>();
        for (Product p : products) {
            if (p != null) {
                String category = p.category();
                categoryMap.computeIfAbsent(category, k -> new ArrayList<>()).add(p.name());
            }
        }
        return categoryMap;
    }

    // 문제 2 "재고 수량 합산" (merge)
    // 두 개의 창고(Warehouse A, B)에서 재고 데이터를 가져왔습니다. 두 맵을 합쳐서 전체 재고 수량을 계산해야 합니다.
    public Map<String, Integer> mergeInventory(Map<String, Integer> warehouseA, Map<String, Integer> warehouseB) {
        Map<String, Integer> totalInventory = new HashMap<>(warehouseA);

        for (String productId : warehouseB.keySet()) {
            int quantityB = warehouseB.get(productId);
            // 1. 기존 맵에 있는지 확인
            if (totalInventory.containsKey(productId)) {
                // 2. 있으면 더하기
                int currentQuantity = totalInventory.get(productId);
                totalInventory.put(productId, currentQuantity + quantityB);
            } else {
                // 3. 없으면 그냥 넣기
                totalInventory.put(productId, quantityB);
            }
        }
        return totalInventory;
    }

    // D (Critical Logic Fail)
    // **"사이드 이펙트(Side-Effect)의 함정"**에 제대로 빠지셨습니다.
    // merge 메서드는 **"함수형 프로그래밍"**의 철학을 따릅니다.
    // 즉, 람다 내부에서 외부 상태를 변경(put)하면 안 되고, **"새로운 값만 반환"**해야 합니다.
    public Map<String, Integer> mergeInventoryTobe(Map<String, Integer> warehouseA, Map<String, Integer> warehouseB) {
        Map<String, Integer> totalInventory = new HashMap<>(warehouseA);
        for (String productId : warehouseB.keySet()) {
            totalInventory
                    .merge(productId,
                            warehouseB.get(productId),
                            (integer, integer2) -> totalInventory.put(productId, integer + integer2)
                    );
        }
        return totalInventory;
    }

    // merge의 세 번째 인자는 BiFunction입니다. "옛날 값과 새 값을 줄 테니, 합쳐진 결과만 다오. 저장은 내가 알아서 할게." 라는 뜻입니다.
    // Integer::sum을 쓰면 더 깔끔합니다. 그리고 warehouseB 루프도 forEach로 돌리면... 환상적이죠.
    public Map<String, Integer> mergeInventoryTobeS(Map<String, Integer> warehouseA, Map<String, Integer> warehouseB) {
        Map<String, Integer> totalInventory = new HashMap<>(warehouseA);
        warehouseB.forEach((key, value) ->{
            totalInventory.merge(key, value, Integer::sum);
        });
        return totalInventory;
    }

    // 문제 3 "재고 부족 알림" (forEach)
    // 전체 재고 맵을 순회하면서 수량이 10개 미만인 상품을 발견하면 경고 로그를 출력해야 합니다.
    public void checkLowInventory(Map<String, Integer> inventory) {
        // entrySet으로 순회... 지저분함
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            String productId = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity < 10) {
                System.out.println("Low stock alert for Product: " + productId + ", Qty: " + quantity);
            }
        }
    }

    // 등급: S (Perfect Execution)
    // 모던 자바에서는 Map의 형식을 foreach로 풀어내 key와 value 값을 쉽게 얻을 수 있게 되었다.
    public void checkLowInventoryTobe(Map<String, Integer> inventory) {
        inventory.forEach((key, value) -> {
            if (value < 10) {
                System.out.println("Low stock alert for Product: " + key + ", Qty: " + value);
            }
        });
    }

}
