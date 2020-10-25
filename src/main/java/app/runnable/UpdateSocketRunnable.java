package app.runnable;

import app.bean.ConnectionPath;
import app.bean.SocketState;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

@Slf4j
public class UpdateSocketRunnable implements Runnable {
    private final SocketState state;
    private final Gson gson = new Gson();

    public UpdateSocketRunnable(SocketState state) {
        this.state = state;
    }

    @Override
    public void run() {
        try {
            SocketState lastState = null;
            while (true) {
                try {
                    if (!state.equals(lastState)) {
                        lastState = state;
                        String newIp = getMyIp();
                        log.info("Server IP: " + newIp);
                        state.setIp(newIp);
                        String res = postMySocket(state);
                        log.info("IP info updated: " + res);
                    }
                    Thread.sleep(100);
                } catch (IOException e) {
                    log.error("UpdateSocketRunnable IOException: " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            log.error("UpdateSocketRunnable interrupted");
        }
    }

    private String getMyIp() throws IOException {
        URL url = new URL("http://checkip.amazonaws.com/");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        return br.readLine();
    }

    private String postMySocket(SocketState state) throws IOException {
        HttpPost post = new HttpPost("https://it-prom.com/charts/rest/socket");

        ConnectionPath data = ConnectionPath
                .builder()
                .port_save(state.getPort_save())
                .port_show(state.getPort_show())
                .busy_save(state.getBusy_save())
                .busy_show(state.getBusy_show())
//                .ip("127.0.0.1")
                .ip(state.getIp())
                .build();
        post.setEntity(new StringEntity(gson.toJson(data), ContentType.APPLICATION_FORM_URLENCODED));

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpClient.execute(post);
        InputStream is = response.getEntity().getContent();
        InputStreamReader isReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        return sb.toString();
    }
}
