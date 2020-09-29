package app.listener;

import app.bean.ConnectionState;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ScreenSave implements SocketDataListener {
    @Override
    public void process(ServerSocket serverSocket, ConnectionState state) {
        Socket socket = null;
        DataInputStream inStream = null;
        DataOutputStream outStream = null;
        BufferedReader reader = null;
        try {
            socket = serverSocket.accept();
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(System.in));

            String clientMessage = "", serverMessage = "";
            while (!clientMessage.equals("close")) {
                clientMessage = inStream.readUTF();
                System.out.println("From client: " + clientMessage);
//                serverMessage = reader.readLine();
//                outStream.writeUTF(serverMessage);
//                outStream.flush();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if(inStream != null){
                    inStream.close();
                }
                if(outStream != null){
                    outStream.close();
                }
                if(socket != null){
                    socket.close();
                }
                serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
