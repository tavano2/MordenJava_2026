package example.Predicate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CodeKata20260219 {

    // 문제 1: "블로킹 지옥(Future.get)에서 벗어나기"
    // 미션
    // .get() 호출을 완전히 제거하세요. 메인 스레드는 작업을 던져만 놓고 자기 일을 계속해야 합니다.
    // CompletableFuture의 체이닝 메서드(supplyAsync, thenApply, thenAccept 등)를 사용하여 파이프라인을 구축하세요.
    // 별도의 ExecutorService를 직접 관리하고 shutdown()하는 번거로움을 줄여보세요. (필요하다면 ForkJoinPool.commonPool()을 써도 좋지만, 어제 배운 커스텀 풀을 적용해도 좋습니다.)

    // 평가 : 노션 20260219 블로킹 지옥(Future.get)에서 벗어나기 참조
    public void processOrderLegacyTobe(String orderId) {

        try ( ExecutorService executor = Executors.newFixedThreadPool(10) ){
            executor.submit(() -> {
                CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(1000); // API 호출 시뮬레이션
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "Order_Details_of_" + orderId;
                }).thenApply(s -> {
                    try {
                        Thread.sleep(1000); // API 호출 시뮬레이션
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return s + " is PAID";
                }).thenAccept(s -> {
                    System.out.println("최종 결과: " + s);
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processOrderLegacyTobeS(String orderId) {
        // 1. 실무에서는 스레드 풀을 메서드 내부가 아닌, 스프링 빈(Bean)이나 전역 변수로 관리합니다.
        ExecutorService customExecutor = Executors.newFixedThreadPool(10);

        // 2. executor.submit() 래핑 없이 바로 CompletableFuture를 시작합니다.
        CompletableFuture.supplyAsync(() -> {
                    simulateDelay(1000);
                    return "Order_Details_of_" + orderId;
                }, customExecutor) // <--- 핵심: 두 번째 인자로 커스텀 풀을 지정!

                .thenApply(s -> {
                    simulateDelay(1000);
                    return s + " is PAID";
                }) // thenApply는 기본적으로 이전 작업을 수행한 스레드를 그대로 이어서 사용합니다.

                .thenAccept(s -> {
                    System.out.println("최종 결과: " + s);
                });

        // (테스트 환경이라면 메인 스레드가 죽지 않도록 끝에 .join()을 붙여 대기시킬 수 있습니다)
    }

    // 중복되는 Thread.sleep 로직은 별도 메서드로 빼는 것이 가독성에 좋습니다.
    private void simulateDelay(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    // 문제 2: "스레드를 넘나드는 예외 처리"
    // CompletableFuture가 제공하는 예외 처리 체이닝 메서드인 .exceptionally() 또는 **.handle()**을 파이프라인에 추가하세요.
    // 에러가 발생하면 "결제 실패: " + 에러 메시지를 반환하여, 파이프라인이 멈추지 않고 thenAccept까지 무사히 도달해 출력되도록 복구(Recovery) 해보세요.
    public void processOrderWithExceptionTobe(String orderId) {
        CompletableFuture.supplyAsync(() -> "Order_Details_of_" + orderId)
        .thenApply(s -> s + " is PAID")
        .exceptionally(throwable -> {
            return "결재 실패" + throwable.getMessage(); // 실패 메세지 추가!
            // exceptionally의 경우 thenApply 뒤에 붙이거나
            // exceptionally의 메시지를 supply쪽 뒤에 붙이고 싶다면 hanlde()을 사용해서 분기처리를 진행해야한다.
        }).thenAccept(System.out::println);
    }

    // 문제 3: "Scatter-Gather (모아치기) 패턴"
    public record Product(String name) {}
    public record Review(int score) {}
    public record Delivery(String address) {}
    public record ProductPage(Product p, Review r, Delivery d) {} // 최종 결과

    // CompletableFuture.supplyAsync를 사용해 3개의 API를 동시에 호출하세요.
    // **CompletableFuture.allOf(...)**를 사용하여 3개의 작업이 모두 끝날 때까지 기다리세요.
    // 모든 결과가 도착하면 ProductPage 레코드를 생성하여 반환하세요.
    // (주의: allOf 자체는 리턴값이 Void입니다. 각 Future에서 .join()으로 값을 꺼내야 합니다.)
    public ProductPage getProductPageLegacy() {
        long start = System.currentTimeMillis();

        // 1. 상품 조회 (1초)
        Product product = getProductAPI();

        // 2. 리뷰 조회 (1초) - 상품 조회가 끝나야 실행됨 (Blocking)
        Review review = getReviewAPI();

        // 3. 배송 조회 (1초) - 리뷰 조회가 끝나야 실행됨 (Blocking)
        Delivery delivery = getDeliveryAPI();

        System.out.println("총 소요 시간: " + (System.currentTimeMillis() - start) + "ms");
        return new ProductPage(product, review, delivery);
    }

    public ProductPage getProductPageTobe() {
        long start = System.currentTimeMillis();

        // 1 뿌리기
        CompletableFuture<Product> productCompletableFuture = CompletableFuture.supplyAsync(this::getProductAPI);
        CompletableFuture<Review> reviewCompletableFuture = CompletableFuture.supplyAsync(this::getReviewAPI);
        CompletableFuture<Delivery> deliveryCompletableFuture = CompletableFuture.supplyAsync(this::getDeliveryAPI);

        CompletableFuture.allOf(productCompletableFuture, reviewCompletableFuture, deliveryCompletableFuture).join();

        System.out.println("총 소요 시간: " + (System.currentTimeMillis() - start) + "ms");
        return new ProductPage(productCompletableFuture.join(), reviewCompletableFuture.join(), deliveryCompletableFuture.join());
    }

    // (Mock API Methods - 1초씩 걸림)
    private Product getProductAPI() { simulateDelay(1000); return new Product("MacBook Pro"); }
    private Review getReviewAPI() { simulateDelay(1000); return new Review(5); }
    private Delivery getDeliveryAPI() { simulateDelay(1000); return new Delivery("Seoul"); }


}
