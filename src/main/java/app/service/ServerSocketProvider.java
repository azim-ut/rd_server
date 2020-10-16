package app.service;

import app.bean.ConnectionState;
import app.constants.Mode;
import app.runnable.DefineHost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ServerSocketProvider {
    private Map<Integer, ServerSocket> sockets = new HashMap<>();

    public ServerSocket get(Mode act, String code, int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = sockets.get(port);
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    log.error("CloseServerSocketException: " + e.getMessage(), e);
                }
            }
            serverSocket = new ServerSocket(port);
            sockets.put(port, serverSocket);
            pushHostData(act, code, port);
        } catch (IOException e) {
            log.error("DefineSocket Exception. " + e.getMessage(), e);
        }
        return serverSocket;
    }

    public void pushHostData(Mode hostType, String code, int port) {
        new DefineHost(
                ConnectionState
                        .builder()
                        .act(hostType)
                        .port(port)
                        .code(code)
                        .build()
        ).run();
    }
}
