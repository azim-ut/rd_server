package app.listener;

import app.bean.ConnectionState;

import java.net.ServerSocket;

public interface SocketDataListener {

    void process(ServerSocket socket, ConnectionState state);
}
