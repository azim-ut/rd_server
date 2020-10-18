package app.runnable;

import app.ScreenCastServer;
import app.bean.ScreenPacket;
import app.service.RedisScreenProvider;
import app.service.ScreenPacketProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
                List<ScreenPacket> screenKeys = provider.list(code);
                if (!webSocket.ready()) {
                    continue;
                }
                for (ScreenPacket row : screenKeys) {
                    log.info("Cast screen " + row.getId() + " bytes: " + row.getBytes().length);
                    webSocket.broadcast(row.getCode() + ":" + row.getPosition() + ":" + row.getX() + ":" + row.getY() + ":" + row.getW() + ":" + row.getH());
                    webSocket.broadcast(row.getBytes());
                    log.info("Screen " + row.getId() + " sent");
                }
            }

        } catch (UnknownHostException e) {
            log.info("UnknownHostException. " + e.getMessage(), e);
        } finally {
            log.info("Screen casting is over.");
        }
    }
}
