package app.bean;

import app.listener.SocketDataListener;
import app.runnable.DefineHost;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;

@RequiredArgsConstructor
public class Connect implements Runnable {

    private final Integer port;
    private final String code;
    private final SocketDataListener listener;

    @Override
    public void run() {
        ConnectionState state = ConnectionState.builder()
                .port(port)
                .code(code)
                .build();
        new Thread(new DefineHost(state)).start();

        while (state.getIp() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while(true){
            try (ServerSocket serverSocket = new ServerSocket(state.getPort())) {
                System.out.println("Socket: " + serverSocket.toString());
                listener.process(serverSocket, state);
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
