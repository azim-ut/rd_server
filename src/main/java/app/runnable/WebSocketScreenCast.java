package app.runnable;

import app.ScreenCastServer;
import app.service.RedisScreenProvider;
import app.service.ScreenPacketProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class WebSocketScreenCast implements Runnable {

    private final String code;
    private final Integer port;

    @Override
    public void run() {
        ScreenPacketProvider provider = new RedisScreenProvider();
        ScreenCastServer webSocket = null;

        try {
            webSocket = new ScreenCastServer(port);
            log.info("ScreenCastServer started on port: " + webSocket.getPort());
            webSocket.start();
            while (true) {
                List<String> screenKeys = provider.keys(code);
                if (!webSocket.ready()) {
                    continue;
                }
                for (String key : screenKeys) {
                    byte[] bytes = provider.get(key, 0);
                    log.info("Cast screen " + key + " bytes: " + bytes.length);
                    webSocket.broadcast(key.getBytes());
                    log.info("Screen " + key + " sent");
                }
            }

        } catch (UnknownHostException e) {
            log.info("UnknownHostException. " + e.getMessage(), e);
        } finally {
            log.info("Screen casting is over.");
        }
    }
}
