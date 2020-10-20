package app.runnable;

import app.bean.ScreenPacket;
import app.constants.ServerMode;
import app.service.RedisScreenProvider;
import app.service.ScreenPacketProvider;
import app.service.ServerSocketProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class ScreenSaver implements Runnable {

    private final String code;
    private final Integer port;

    private ServerSocket serverSocket;

    private ScreenPacketProvider provider;

    @Override
    public void run() {
        provider = new RedisScreenProvider();
        ServerSocketProvider serverSocketProvider = new ServerSocketProvider();
        ScreenPacket packet = null;

        try {
            serverSocket = serverSocketProvider.get(ServerMode.SAVE, code, port);
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    ObjectInputStream inStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    try {
                        while (true) {
                            packet = (ScreenPacket) inStream.readObject();
                            if (packet.getBytes().length > 0) {
                                saveScreen(packet);
                            } else {
                                removeScreen(packet);
                            }
                            log.info("Received: " + packet.toString());
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    } finally {
                        try {
                            inStream.close();
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log.error("Class cast exception: " + e.getMessage(), e);
                } catch (IOException e) {
                    log.error("SocketException: " + e.getMessage());
                    serverSocket = serverSocketProvider.get(ServerMode.SAVE, code, port);
                }
            }
        } finally {
            try {
//                if (outStream != null) {
//                    outStream.close();
//                }
                if (packet != null) {
                    provider.clear(packet.getCode() + "_");
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void saveScreen(ScreenPacket screenPacket) {
        try {
            provider.put(screenPacket);
        } catch (Exception exception) {
            log.error("ScreenPacket save Exception: " + screenPacket.toString(), exception);
        }
    }

    private void removeScreen(ScreenPacket screenPacket) {
        provider.remove(screenPacket);
    }
}
