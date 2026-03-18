package example.Part3_5;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * [Week 1 - 3교시 - Step 2]
 * 단일 스레드로 다중 접속을 처리하는 NIO Selector 서버
 */
public class NioSelectorServer {

    private static final int PORT = 8080;
    private static final int BUFFER_SIZE = 256;
    private static final ExecutorService workerPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        // 1. Selector와 ServerSocketChannel 오픈
        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            // [중요] 비블로킹 모드 설정 - 이걸 안 하면 Selector에 등록이 안 됨!
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(PORT));

            // 2. 서버 채널을 Selector에 등록 (접속 수용 이벤트 감시)
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("[Server] 단일 스레드 NIO 서버 시작 (Port: " + PORT + ")");

            while (true) {
                // 3. 이벤트가 발생할 때까지 대기 (Blocking call이지만, 수천 개 채널을 한 번에 감시)
                // 내부적으로 OS의 epoll/kqueue 시스템 콜을 호출함
                if (selector.select() == 0) continue;

                // 4. 발생한 이벤트(SelectionKey) 세트를 가져옴
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();

                    // [중요] 처리할 이벤트를 꺼냈으면 세트에서 반드시 제거해야 함 (중복 처리 방지)
                    iter.remove();

                    if (key.isAcceptable()) {
                        handleAccept(selector, key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                }
            }
        }
    }

    private static void handleAccept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();

        // 클라이언트 채널도 반드시 비블로킹으로 설정해야 Selector가 감시 가능
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);

        System.out.println("[Server] 신규 클라이언트 접속: " + client.getRemoteAddress());
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        int bytesRead = client.read(buffer);
        if (bytesRead == -1) { // 연결 종료
            System.out.println("[Server] 클라이언트 연결 종료: " + client.getRemoteAddress());
            client.close();
            return;
        }

        buffer.flip();
        // [Mission] 아래 비즈니스 로직을 workerPool.submit()으로 감싸서 리팩토링하세요.
        // 힌트: 람다 안에서 buffer를 직접 쓰면 다른 요청과 섞일 수 있으니
        // byte[]나 String으로 변환 후 넘기는 것이 안전합니다.

        String reqData = new String(buffer.array(), 0, bytesRead).trim();
        workerPool.submit(() -> {
            try {
                // 가상의 무거운 비즈니스 로직 (2초 소요)
                Thread.sleep(2000);
                System.out.println("[Server] 데이터 수신: " + reqData + " (From: " + client.getRemoteAddress() + ")");
                // Echo: 받은 데이터를 다시 돌려줌
                client.write(ByteBuffer.wrap(("Echo: " + reqData + "\n").getBytes()));
            } catch (Exception e) {
               e.getCause();
            }
        });




    }
}
