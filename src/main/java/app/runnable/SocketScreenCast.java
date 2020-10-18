package app.runnable;

import app.bean.ScreenPacket;
import app.constants.ServerMode;
import app.service.RedisScreenProvider;
import app.service.ScreenPacketProvider;
import app.service.ServerSocketProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SocketScreenCast implements Runnable {

    private final String code;
    private final Integer port;

    @Override
    public void run() {
        ScreenPacketProvider provider = new RedisScreenProvider();
        ServerSocketProvider serverSocketProvider = new ServerSocketProvider();
        ServerSocket serverSocket = null;

        try {
            serverSocket = serverSocketProvider.get(ServerMode.SHOW, code, port);
            while (true) {
                DataOutputStream outputStream = null;
                DataInputStream inputStream = null;
                try (Socket socket = serverSocket.accept()) {
                    inputStream = new DataInputStream(socket.getInputStream());
                    while (true) {
                        List<ScreenPacket> screenPackets = provider.list(code);
                        String response = null;
                        for (ScreenPacket screenPacket : screenPackets) {
                            try {
                                outputStream = new DataOutputStream(socket.getOutputStream());
                                log.info("Cast screen " + screenPacket.getId() + " bytes: " + screenPacket.getBytes().length);
                                outputStream.writeUTF(screenPacket.getId());
                                outputStream.writeInt(screenPacket.getBytes().length);
                                outputStream.write(screenPacket.getBytes());
//                                outputStream.write('\n');

                                while (response == null) {
                                    if (inputStream.available() > 0) {
                                        response = inputStream.readUTF();
                                    }
                                }

                                log.info("Screen " + screenPacket.getId() + " sending");
                                outputStream.flush();
                                log.info("Screen " + screenPacket.getId() + " sent");
                            } catch (SocketException e) {
                                throw e;
                            } catch (IOException e) {
                                log.error("Screen Cast IOException [code: " + screenPacket.getId() + "]: " + e.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("Screen Cast SocketException: " + e.getMessage());
                    serverSocket = serverSocketProvider.get(ServerMode.SHOW, code, port);
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
