package app;

import app.bean.ConnectionState;
import app.runnable.DefineHost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Unit test for simple App.
 */

@RunWith(MockitoJUnitRunner.class)
public class ConnectionTest {

    CloseableHttpClient client;
    ThreadPoolExecutor threadPoolExecutor;

    @Before
    public void start() {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(5);
        client = HttpClients.custom().setConnectionManager(poolingConnManager).build();
    }

    @Ignore
    @Test
    public void testConnection() throws IOException {
        ConnectionState state = ConnectionState.builder()
                .port(Constants.PORT_READ)
                .code(Constants.CODE)
                .build();
        new Thread(new DefineHost(state)).start();
        while (state.getIp() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        InetAddress.getByName(state.getIp());
        try (ServerSocket serverSocket = new ServerSocket(state.getPort(), 50, InetAddress.getByName(state.getIp()))) {
            while (true) {
                Socket socket = serverSocket.accept();
                OutputStream out = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(out, true);
                writer.println(new Date().getTime());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void resetScreen() {

    }
}

