package app;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

@Slf4j
@Getter
public class ScreenCastServer extends WebSocketServer {
    private Queue<byte[]> screens = new LinkedList<>();
    private String code = null;
    private boolean closed = false;

    public ScreenCastServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public boolean isClosed(){
        return closed;
    }

    public void dropCode() {
        this.code = null;
    }

    public boolean isCodeRequested() {
        return code != null;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast("new connection: " + handshake.getResourceDescriptor()); //This method sends a message to all clients connected
        log.debug(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        log.debug(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        this.code = message;
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if(ex instanceof BindException){
            closed = true;
        }
        if (conn != null) {
            log.error(ex.getMessage(), ex);
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
//        setConnectionLostTimeout(0);
//        setConnectionLostTimeout(100);
    }
}
