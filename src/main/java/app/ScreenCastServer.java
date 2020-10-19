package app;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

@Slf4j
public class ScreenCastServer extends WebSocketServer {
    private Queue<byte[]> screens = new LinkedList<>();
    private String code = null;

    public ScreenCastServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public ScreenCastServer(InetSocketAddress address) {
        super(address);
    }

    public ScreenCastServer(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    public boolean ready() {
        return screens.size() == 0;
    }

    public void dropCode(){
        this.code = null;
    }

    public boolean isCodeRequested(){
        return code != null;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast("new connection: " + handshake.getResourceDescriptor()); //This method sends a message to all clients connected
        log.debug(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    public boolean cast(byte[] bytes) {
        return screens.add(bytes);
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
    public void onMessage(WebSocket conn, ByteBuffer message) {
        broadcast(message.array());
        System.out.println(conn + ": " + message);
    }

    public void process(){

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        if(screens.size()>0){
            for(byte[] row : screens){

            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
}
