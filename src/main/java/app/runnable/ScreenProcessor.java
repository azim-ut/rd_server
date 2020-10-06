package app.runnable;

import app.bean.ScreenPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
                    inStream = new ObjectInputStream(socket.getInputStream());
                    ScreenPacket packet = (ScreenPacket) inStream.readObject();
                    log.info(packet.getFileInfo());
                    saveTo(packet);
                } catch (ClassNotFoundException e) {
                    log.error("Class cast exception: " + e.getMessage(), e);
                } catch (IOException ioException) {
                    log.error("SocketException: " + ioException.getMessage(), ioException);
                    defineServerSocket();
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
                log.error(e.getMessage());
            }
        }
    }

    private void saveTo(ScreenPacket screenPacket){
        String fileName = screenPacket.getFileName();
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
