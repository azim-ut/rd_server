package app.runnable;

import app.Constants;
import app.bean.ActionPacket;
import app.bean.ConnectionState;
import app.bean.ResponsePacket;
import app.service.RedisScreenProvider;
import app.service.ScreenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

@Slf4j
@RequiredArgsConstructor
public class ScreenProcessor implements Runnable {

    private final Integer port;

    private ServerSocket serverSocket;

    private ScreenProvider provider;

    @Override
    public void run() {
        ObjectInputStream inStream = null;
        ObjectOutputStream outStream = null;
        provider = new RedisScreenProvider();
        ActionPacket packet = null;

        try {
            defineServerSocket();
            while (true) {
                try (Socket socket = serverSocket.accept()) {
//                    outStream = new ObjectOutputStream(socket.getOutputStream());
                    while (true) {
                        if (socket.getInputStream().available() != 0) {
                            if (inStream == null) {
                                inStream = new ObjectInputStream(socket.getInputStream());
                            }
                            packet = (ActionPacket) inStream.readObject();
                            if (packet.getCreateFile() != null) {
                                saveScreen(packet);
                            }
                            if (packet.getRemoveFile() != null) {
                                removeScreen(packet);
//                                removeFile(packet);
                            }
                            log.info("Received: " + packet.toString());

//                            ResponsePacket answer = ResponsePacket
//                                    .builder()
//                                    .msg("Ready")
//                                    .build();
//
//                            outStream.writeObject(answer);
//                            outStream.flush();
//                            log.info("Answer: " + answer.toString());
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log.error("Class cast exception: " + e.getMessage(), e);
                } catch (IOException ioException) {
                    log.error("SocketException: " + ioException.getMessage(), ioException);
                    defineServerSocket();
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
                serverSocket.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void saveScreen(ActionPacket screenPacket) {
        provider.put(screenPacket.getCode() + "_" + screenPacket.getPosition(), screenPacket.getBytes());
    }

    private void removeScreen(ActionPacket screenPacket) {
        provider.remove(screenPacket.getCode() + "_" + screenPacket.getPosition());
    }

    private void saveFile(ActionPacket screenPacket) {
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

    private void removeFile(ActionPacket screenPacket) {
        String fileName = screenPacket.getRemoveFile();
        String code = screenPacket.getCode();
        File file = new File("screen/" + code + "/" + fileName);
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException ioException) {
            log.error("Cannot remove file " + file.getPath());
        }
    }

    private void defineServerSocket() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error("CloseServerSocketException: " + e.getMessage(), e);
            }
        }
        try {
            serverSocket = new ServerSocket(port);
            new DefineHost(
                    ConnectionState
                            .builder()
                            .port(Constants.PORT)
                            .code(Constants.CODE)
                            .build()
            ).run();
        } catch (IOException e) {
            log.error("DefineSocket Exception. " + e.getMessage(), e);
        }
    }
}
