package app.runnable;

import app.constants.Mode;
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
            serverSocket = serverSocketProvider.get(Mode.SHOW, code, port);
            while (true) {
                DataOutputStream outputStream = null;
                DataInputStream inputStream = null;
                try (Socket socket = serverSocket.accept()) {
                    inputStream = new DataInputStream(socket.getInputStream());
                    while (true) {
                        List<String> screenKeys = provider.keys(code);
                        String response = null;
                        for (String key : screenKeys) {
                            try {
                                outputStream = new DataOutputStream(socket.getOutputStream());
                                byte[] bytes = provider.get(key, 0);
                                log.info("Cast screen " + key + " bytes: " + bytes.length);
                                outputStream.writeUTF(key);
                                outputStream.writeInt(bytes.length);
                                outputStream.write(bytes);
//                                outputStream.write('\n');

                                while (response == null) {
                                    if (inputStream.available() > 0) {
                                        response = inputStream.readUTF();
                                    }
                                }

                                log.info("Screen " + key + " sending");
                                outputStream.flush();
                                log.info("Screen " + key + " sent");
                            } catch (SocketException e) {
                                throw e;
                            } catch (IOException e) {
                                log.error("Screen Cast IOException [code: " + key + "]: " + e.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("Screen Cast SocketException: " + e.getMessage());
                    serverSocket = serverSocketProvider.get(Mode.SHOW, code, port);
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
