package app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ServerSocketProvider {
    private static Map<Integer, ServerSocket> sockets = new HashMap<>();

    public ServerSocket get(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = sockets.get(port);
            if (serverSocket != null && !serverSocket.isClosed()) {
//                return serverSocket;
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    log.error("CloseServerSocketException: " + e.getMessage(), e);
                }
            }
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            sockets.put(port, serverSocket);
        } catch (BindException e) {
            log.error("BindException Exception. " + e.getMessage());
        } catch (IOException e) {
            log.error("DefineSocket Exception. " + e.getMessage());
        }
        return serverSocket;
    }
}
