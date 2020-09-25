package app;

import app.runnable.IpUpdateRunnable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
@Component
public class ServerApp {
    public void start(String[] args) {
        System.out.println("111");
        int port = 4907;
        String code = "TEST";
        new Thread(new IpUpdateRunnable(code)).start();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Socket: " + serverSocket.toString());
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    OutputStream out = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(out, true);
                    writer.println(code + ": " + new Date().getTime());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void displayPassword(){
        SetPassword frame1 = new SetPassword();
        frame1.setSize(300, 80);
        frame1.setLocation(500, 300);
        frame1.setVisible(true);

        Producer producer = new Producer();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(producer, 0, 1, TimeUnit.SECONDS);
    }


    static class Producer implements Runnable {
        @Override
        public void run() {
            Socket socket = new Socket();
            try {
                Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

                System.out.println("START");
                while (e.hasMoreElements()) {
                    NetworkInterface networkInterface = e.nextElement();
                    Enumeration<InetAddress> ee = networkInterface.getInetAddresses();
                    if (ee.hasMoreElements()) {
                        while (ee.hasMoreElements()) {
                            InetAddress address = ee.nextElement();
                            if (address instanceof Inet6Address) {
                                // It's ipv6 skip
                            } else if (address instanceof Inet4Address) {
                                System.out.println("My IP: " + address.getHostAddress());
                            }
                        }
                    }
                }

                System.out.println("END");

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }

    }
}
