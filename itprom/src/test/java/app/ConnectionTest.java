package app;

import app.bean.ConnectionState;
import app.runnable.HostUpdateRunnable;
import app.service.ScreenService;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Unit test for simple App.
 */

@RunWith(MockitoJUnitRunner.class)
public class ConnectionTest {

    @InjectMocks
    ScreenService screenService = new ScreenService();

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
                .port(4907)
                .code("TEST")
                .build();
        new Thread(new HostUpdateRunnable(state)).start();

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

    public void sendScreen(File file) {
        String url = "http://it-prom.com/upload.php";

        threadPoolExecutor.execute(new UploadFileTask(client, url, file));
    }

    @Ignore
    @Test
    public void resetScreen() {

    }
}

