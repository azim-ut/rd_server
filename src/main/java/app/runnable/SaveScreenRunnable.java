package app.runnable;

import app.bean.ScreenPacket;
import app.bean.SocketState;
import app.service.RedisScreenProvider;
import app.service.ScreenPacketProvider;
import app.service.ServerSocketProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class SaveScreenRunnable implements Runnable {

    private SocketState state;

    private final ScreenPacketProvider provider;

    @Autowired
    private ServerSocketProvider serverSocketProvider;

    public SaveScreenRunnable(SocketState state) {
        provider = new RedisScreenProvider();
        serverSocketProvider = new ServerSocketProvider();
        this.state = state;
    }

    @Override
    public void run() {
        int lastFrame = -1;
        while (true) {
            try (ServerSocket serverSocket = serverSocketProvider.get(state.getPort_save())) {
                log.info("SAVE SOCKET READY to accept connections. IP:{}, port:{}", state.getIp(), state.getPort_save());
                Socket socket = serverSocket.accept();
                state.incBusySave();
                ScreenPacket packet = null;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("SaveScreenRunnable interrupted on screen receiving. {}", e.getMessage());
                }
                try (
                        BufferedInputStream buff = new BufferedInputStream(socket.getInputStream());
                        ObjectInputStream inStream = new ObjectInputStream(buff);
                ) {
                    while (true) {
                        packet = (ScreenPacket) inStream.readObject();
                        if(lastFrame < packet.getFrame()){
                            provider.remove(packet);
                            lastFrame = packet.getFrame();
                        }
                        if (packet.getBytes().length > 0) {
                            saveScreen(packet);
                        } else if (packet.getCommand() != null && packet.getCommand().equals("ONLY_BG")) {
                            leaveOnlyBg(packet);
                        } else {
                            removeScreen(packet);
                        }

                        log.info("Received: " + packet.toString());
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            log.error("SaveScreenRunnable interrupted on screen receiving. {}", e.getMessage());
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log.error("Class cast exception: " + e.getMessage(), e);
                } catch (IOException e) {
                    log.error("SAVE SOCKET CLOSED. Socket: IP:{}, port:{}", state.getIp(), state.getPort_save());
                }
                state.decBusySave();
            } catch (IOException ioException) {

                log.error("ScreenSaverException IOException: " + ioException.getMessage());
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

    private void leaveOnlyBg(ScreenPacket screenPacket) {
        provider.clear(screenPacket.getCode());
    }
}
