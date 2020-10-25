package app.listener;

import app.bean.SocketState;

import java.net.ServerSocket;

public interface SocketDataListener {

    void process(ServerSocket socket, SocketState state);
}
