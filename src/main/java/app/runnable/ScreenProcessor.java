package app.runnable;

import app.bean.ActionPacket;
import app.bean.ResponsePacket;
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

    @Override
    public void run() {
        ObjectInputStream inStream = null;
        ObjectOutputStream outStream = null;

        try {
            defineServerSocket();
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    outStream = new ObjectOutputStream(socket.getOutputStream());
                    while (true) {
                        if (socket.getInputStream().available() != 0) {
                            if (inStream == null) {
                                inStream = new ObjectInputStream(socket.getInputStream());
                            }
                            ActionPacket packet = (ActionPacket) inStream.readObject();
                            if (packet.getCreateFile() != null) {
                                saveFile(packet);
                            }
                            if (packet.getRemoveFile() != null) {
                                removeFile(packet);
                            }
                            log.info("Received: " + packet.toString());

                            ResponsePacket answer = ResponsePacket
                                    .builder()
                                    .msg("Ready")
                                    .build();

                            outStream.writeObject(answer);
                            outStream.flush();
                            log.info("Answer: " + answer.toString());
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
                if (outStream != null) {
                    outStream.close();
                }
                serverSocket.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
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
        } catch (IOException e) {
            log.error("DefineSocket Exception. " + e.getMessage(), e);
        }
    }
}