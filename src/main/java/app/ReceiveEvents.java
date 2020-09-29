package app;

import java.awt.*;
import java.net.Socket;
import java.util.Scanner;

public class ReceiveEvents extends Thread{
    Socket socket;
    Robot robot;
    boolean continueLoop = true;

    public ReceiveEvents(Socket socket, Robot robot) {
        this.socket = socket;
        this.robot = robot;
        start();
    }

    @Override
    public void run() {
        Scanner scanner;
        try{
            scanner = new Scanner(socket.getInputStream());
            while(continueLoop){
                if(scanner.hasNext()){
                    int command = scanner.nextInt();
                    switch (command){
                        case -1:
                            robot.mousePress(scanner.nextInt());
                            break;
                        case -2:
                            robot.mouseRelease(scanner.nextInt());
                            break;
                        case -3:
                            robot.keyPress(scanner.nextInt());
                            break;
                        case -4:
                            robot.keyRelease(scanner.nextInt());
                            break;
                        case -5:
                            robot.mouseMove(scanner.nextInt(), scanner.nextInt());
                            break;

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
