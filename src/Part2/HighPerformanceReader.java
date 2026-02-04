package Part2;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class HighPerformanceReader {

    // 넉넉하게 읽기 위한 여유분 (가장 긴 라인 길이보다 커야 함)
    private static final long SAFETY_MARGIN = 1024 * 4; // 4KB

    private void processSection(String filePath, long start, long end) {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
             FileChannel channel = file.getChannel()) {

            // [성능 핵심] channel.map() 사용
            // 내 구역(size) + 여유분(SAFETY_MARGIN) 만큼을 메모리에 매핑합니다.
            // 이렇게 하면 디스크 I/O 함수 호출 없이 메모리에서 바로 읽습니다. (Zero-Copy)
            long mapSize = (end - start) + SAFETY_MARGIN;

            // 파일 끝을 넘어가면 안 되니 크기 조정
            if (start + mapSize > channel.size()) {
                mapSize = channel.size() - start;
            }

            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, start, mapSize);

            // 1. Skip First Logic (앞 스레드 찌꺼기 건너뛰기)
            if (start > 0) {
                // 버퍼에서 한 바이트씩 읽으며 '\n' 찾기
                while (buffer.hasRemaining()) {
                    if (buffer.get() == '\n') {
                        break; // 찾았다! 루프 탈출
                    }
                }
            }

            // 2. Read & Process Logic
            // 이제 진짜 내 구역 데이터를 처리합니다.
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                long currentFilePosition = start + buffer.position();

                // 여기서 b(데이터)를 모아서 처리...
                // processByte(b);

                // 3. Read Past End Logic (문장 마무리 확인)
                if (b == '\n') {
                    // 현재 읽은 위치가 내 담당 구역(end)을 지났다면 종료
                    if (currentFilePosition >= end) {
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
