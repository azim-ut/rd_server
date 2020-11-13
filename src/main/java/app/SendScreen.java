package app;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SendScreen extends Thread {
    Socket socket;
    Robot robot;
    Rectangle rectangle;
    boolean continueLoop = true;
    OutputStream oos = null;


    public SendScreen(Socket socket, Robot robot, Rectangle rectangle) {
        this.socket = socket;
        this.robot = robot;
        this.rectangle = rectangle;
        start();
    }

    @Override
    public void run() {
        try {
            oos = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (continueLoop) {
            BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
            try {
                ImageIO.write(bufferedImage, "jpeg", oos);
            } catch (IOException ioException) {
//                ioException.printStackTrace();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
//                e.printStackTrace();
//                Thread.currentThread().interrupt();
            }
        }
    }
}
