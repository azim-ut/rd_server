package app.runnable;

import app.bean.ConnectionPath;
import app.bean.ConnectionState;
import app.constants.Mode;
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
public class DefineHost implements Runnable {
    private final ConnectionState state;
    private final Gson gson = new Gson();

    public DefineHost(ConnectionState state) {
        this.state = state;
    }

    @Override
    public void run() {
        try {
            String newIp = getMyIp();
            log.info("Server IP: " + newIp);
            String res = postMyIp(state.getAct(), newIp, state.getPort());
            state.setIp(newIp);
            log.info("IP info updated: " + res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMyIp() throws IOException {
        URL url = new URL("http://checkip.amazonaws.com/");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        return br.readLine();
    }

    private String postMyIp(Mode act, String ip, int port) throws IOException {
        HttpPost post = new HttpPost("https://it-prom.com/charts/rest/ip");

        ConnectionPath data = ConnectionPath
                .builder()
                .code("TEST")
                .act(act)
                .port(port)
                .ip("127.0.0.1")
//                .ip(ip)
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
