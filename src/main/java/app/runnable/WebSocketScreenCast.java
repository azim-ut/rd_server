package app.runnable;

import app.ScreenCastServer;
import app.bean.ScreenPacket;
import app.service.RedisScreenProvider;
import app.service.ScreenPacketProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class WebSocketScreenCast implements Runnable {

    private final Integer port;

    public void run() {
        boolean enabled = true;
        ScreenPacketProvider provider = new RedisScreenProvider();
        ScreenCastServer webSocket = null;

        try {
            webSocket = new ScreenCastServer(port);
            log.info("ScreenCastServer started on port: " + webSocket.getPort());
            webSocket.start();
            Map<Integer, Long> last = new HashMap<>();
            while (!webSocket.isClosed()) {
                if (webSocket.isCodeRequested()) {
                    List<ScreenPacket> screenKeys = provider.list(webSocket.getCode());

                    boolean same = true;
                    try {
                        for (ScreenPacket row : screenKeys) {
                            if (!last.containsKey(row.getPosition()) || last.get(row.getPosition()) != row.getBytes().length) {
                                same = false;
                                break;
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        same = false;
                    }

                    if (!same) {
                        last = new HashMap<>(screenKeys.size());
                        for (ScreenPacket row : screenKeys) {
                            last.put(row.getPosition(), (long) row.getBytes().length);
                            log.info("Cast screen " + row.getId() + " bytes: " + row.getBytes().length);
                            webSocket.broadcast(row.getId());
                            webSocket.broadcast(row.getBytes());
                            log.info("Screen " + row.getId() + " sent");
                        }
                    }
                    webSocket.broadcast("DONE");
                    webSocket.dropCode();
                }else{
                    Thread.sleep(100);
                }
            }

        } catch (InterruptedException e) {
            log.info("InterruptedException. " + e.getMessage(), e);
        } catch (UnknownHostException e) {
            log.info("UnknownHostException. " + e.getMessage(), e);
        } finally {
            log.info("Screen casting is over.");
        }
    }
}
