package app.runnable;

import app.ScreenCastServer;
import app.bean.ScreenPacket;
import app.bean.SocketState;
import app.service.RedisScreenProvider;
import app.service.ScreenPacketProvider;
import app.service.ServerSocketProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ShowScreenRunnable implements Runnable {

    private SocketState state;

    private ServerSocketProvider serverSocketProvider;
    private ScreenPacketProvider provider;

    public ShowScreenRunnable(SocketState state) {
        serverSocketProvider = new ServerSocketProvider();
        this.state = state;
    }

    public void run() {
        runSocket();
    }

    private void runSocket() {
        ScreenPacketProvider provider = new RedisScreenProvider();
        ObjectOutputStream outStream = null;
        while (true) {
            try (ServerSocket serverSocket = serverSocketProvider.get(state.getPort_show())) {
                log.info("Ready to connect");

                Socket socket = serverSocket.accept();
                state.incBusySave();


                try (
                        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                        ObjectInputStream inStream = new ObjectInputStream(bis);
                ) {

                    Map<String, Long> last = new HashMap<>();
                    log.info("Socket connected");
                    String code = inStream.readUTF();
                    int i = 0;

                    outStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                    while (true) {
                        Thread.sleep(50);
                        List<ScreenPacket> screenKeys = provider.list(code);

                        boolean same = true;
                        try {
                            for (ScreenPacket row : screenKeys) {
                                if (!last.containsKey(row.getId()) || last.get(row.getId()) != row.getBytes().length) {
                                    same = false;
                                    break;
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            same = false;
                        }
                        if (!same || i>10) {
                            last = new HashMap<>(screenKeys.size());
                            for (ScreenPacket row : screenKeys) {
                                last.put(row.getId(), (long) row.getBytes().length);
                                log.info("Cast screen " + row.getId() + " bytes: " + row.getBytes().length);
                                outStream.writeObject(row);
                                outStream.flush();
                            }
                            i = 0;
                        }else{
                            outStream.writeObject(ScreenPacket.builder().build());
                            outStream.flush();
                            Thread.sleep(50);
                            i++;
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("ShowScreenRunnableException : " + e.getMessage());
                }
            } catch (IOException e) {
                log.error("ServerSocket define exception: " + e.getMessage());
            }
        }
    }

    private void runWebSocket() {
        boolean enabled = true;
        ScreenPacketProvider provider = new RedisScreenProvider();
        ScreenCastServer webSocket = null;

        try {
            webSocket = new ScreenCastServer(state.getPort_show());
            log.info("ScreenCastServer started on port: " + webSocket.getPort());
            webSocket.start();
            Map<String, Long> last = new HashMap<>();

            state.incBusyShow();
            while (!webSocket.isClosed()) {
                if (webSocket.isCodeRequested()) {
                    List<ScreenPacket> screenKeys = provider.list(webSocket.getCode());

                    boolean same = true;
                    try {
                        for (ScreenPacket row : screenKeys) {
                            if (!last.containsKey(row.getId()) || last.get(row.getId()) != row.getBytes().length) {
                                same = false;
                                break;
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        same = false;
                    }

                    if (!same) {
                        last = new HashMap<>(screenKeys.size());
                        for (ScreenPacket row : screenKeys) {
                            last.put(row.getId(), (long) row.getBytes().length);
                            log.info("Cast screen " + row.getId() + " bytes: " + row.getBytes().length);
                            webSocket.broadcast(row.getId());
                            webSocket.broadcast(row.getBytes());
                            log.info("Screen " + row.getId() + " sent");
                        }
                    }
                    webSocket.broadcast("DONE");
                    webSocket.dropCode();
                } else {
                    Thread.sleep(50);
                }
            }
            state.decBusyShow();

        } catch (InterruptedException e) {
            log.info("InterruptedException. " + e.getMessage(), e);
        } catch (UnknownHostException e) {
            log.info("UnknownHostException. " + e.getMessage(), e);
        } finally {
            log.info("Screen casting is over.");
        }
    }
}
