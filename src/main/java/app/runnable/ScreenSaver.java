package app.runnable;

import app.bean.ScreenPacket;
import app.constants.HostAct;
import app.service.RedisScreenProvider;
import app.service.ScreenPacketProvider;
import app.service.ServerSocketProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

@Slf4j
@RequiredArgsConstructor
public class ScreenSaver implements Runnable {

    private final String code;
    private final Integer port;

    private ServerSocket serverSocket;

    private ScreenPacketProvider provider;

    @Override
    public void run() {
        ObjectInputStream inStream = null;
        provider = new RedisScreenProvider();
        ServerSocketProvider serverSocketProvider = new ServerSocketProvider();
        ScreenPacket packet = null;

        try {
            serverSocket = serverSocketProvider.get(HostAct.SAVE, code, port);
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    while (true) {
                        if (socket.getInputStream().available() != 0) {
                            if (inStream == null) {
                                inStream = new ObjectInputStream(socket.getInputStream());
                            }
                            packet = (ScreenPacket) inStream.readObject();
                            if (packet.getCreateFile() != null) {
                                saveScreen(packet);
                            }
                            if (packet.getRemoveFile() != null) {
                                removeScreen(packet.getCode(), packet.getPosition());
                            }
                            log.info("Received: " + packet.toString());
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log.error("Class cast exception: " + e.getMessage(), e);
                } catch (IOException ioException) {
                    log.error("SocketException: " + ioException.getMessage(), ioException);
                    serverSocket = serverSocketProvider.get(HostAct.SAVE, code, port);
                } finally {
                    if (inStream != null) {
                        try {
                            inStream.close();
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
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
        provider.put(screenPacket.getCode(), screenPacket.getPosition(), screenPacket.getBytes());
    }

    private void removeScreen(String code, int position) {
        provider.remove(code, position);
    }

    private void saveFile(ScreenPacket screenPacket) {
        String fileName = screenPacket.getCreateFile();
        String code = screenPacket.getCode();
        byte[] bytes = screenPacket.getBytes();

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            BufferedImage bufferedImage = ImageIO.read(bais);
            ImageIO.write(bufferedImage, "jpg", new File("screen/" + code + "/" + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeFile(ScreenPacket screenPacket) {
        String fileName = screenPacket.getRemoveFile();
        String code = screenPacket.getCode();
        File file = new File("screen/" + code + "/" + fileName);
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException ioException) {
            log.error("Cannot remove file " + file.getPath());
        }
    }
}
