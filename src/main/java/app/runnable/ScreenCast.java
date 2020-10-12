package app.runnable;

import app.constants.HostAct;
import app.service.RedisScreenProvider;
import app.service.ScreenPacketProvider;
import app.service.ServerSocketProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ScreenCast implements Runnable {

    private final String code;
    private final Integer port;

    @Override
    public void run() {
        ScreenPacketProvider provider = new RedisScreenProvider();
        ServerSocketProvider serverSocketProvider = new ServerSocketProvider();
        ServerSocket serverSocket = null;

        try {
            serverSocket = serverSocketProvider.get(HostAct.SHOW, code, port);
            while (true) {
//                DataOutputStream outputStream = null;
                OutputStream outputStream = null;
                try (Socket socket = serverSocket.accept()) {

                    while (true) {
                        List<String> screenKeys = provider.keys(code);
                        for (String key : screenKeys) {
                            try {
                                if (outputStream == null) {
                                    outputStream = socket.getOutputStream();
                                }
                                byte[] bytes = provider.get(key, 0);
                                outputStream.write(bytes, 0, 10);
                                outputStream.flush();
                                log.info("Cast screen bytes: " + bytes.length);
                            } catch (IOException e) {
                                log.error("Screen Cast StreamException: " + e.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("Screen Cast SocketException: " + e.getMessage());
                    serverSocket = serverSocketProvider.get(HostAct.SHOW, code, port);
                }
            }
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    log.info("Exception on sever socket close." + e.getMessage(), e);
                }
            }
            log.info("Screen casting is over.");
        }
    }
}
