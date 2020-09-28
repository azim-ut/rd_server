package app;

import app.bean.ConnectionState;
import app.runnable.HostUpdateRunnable;
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
        ConnectionState state = ConnectionState.builder()
                .port(4907)
                .code("TEST")
                .build();
        new Thread(new HostUpdateRunnable(state)).start();
        while (true) {
            if(state.getIp() != null){
                try (ServerSocket serverSocket = new ServerSocket(state.getPort(), 50, InetAddress.getByName(state.getIp()))) {
                    System.out.println("Socket: " + serverSocket.toString());
                    while (true) {
                        try {
                            Socket socket = serverSocket.accept();
                            OutputStream out = socket.getOutputStream();
                            PrintWriter writer = new PrintWriter(out, true);
                            writer.println(state.getCode() + ": " + new Date().getTime());
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayPassword() {
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
