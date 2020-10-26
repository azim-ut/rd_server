package app.runnable;

import app.bean.ScreenPacket;
import app.bean.SocketState;
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
public class SaveScreenRunnable implements Runnable {

    private SocketState state;

    private ScreenPacketProvider provider;

    public SaveScreenRunnable(SocketState state) {
        this.state = state;
    }

    @Override
    public void run() {
        provider = new RedisScreenProvider();
        ServerSocketProvider serverSocketProvider = new ServerSocketProvider();


        try (ServerSocket serverSocket = serverSocketProvider.get(state.getPort_save())){
            while (true) {
                Socket socket = serverSocket.accept();
                state.incBusySave();
                new Thread(() -> {
                    ScreenPacket packet = null;
                    try (BufferedInputStream buff = new BufferedInputStream(socket.getInputStream());
                         ObjectInputStream inStream = new ObjectInputStream(buff);) {
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
                    } catch (ClassNotFoundException e) {
                        log.error("Class cast exception: " + e.getMessage(), e);
                    } catch (IOException e) {
                        log.error("SocketException: " + e.getMessage());
                    }
                }).start();
                state.decBusySave();
            }
        } catch (IOException ioException) {
            log.error("ScreenSaverException IOException: " + ioException.getMessage());
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
