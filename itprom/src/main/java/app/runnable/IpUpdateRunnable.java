package app.runnable;

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

public class IpUpdateRunnable implements Runnable {
    private String code;

    public IpUpdateRunnable(String code) {
        this.code = code;
    }

    @Override
    public void run() {
        String ip = null;
        int cnt = 20;
        int i = cnt;
        while (true) {
            try {
                String newIp = getMyIp();
                if (!newIp.equals(ip) || i--<0) {
                    String res = postMyIp(newIp);
                    ip = newIp;
                    i = cnt;
                }
                Thread.sleep(500);
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private String getMyIp() throws IOException {
        URL url = new URL("http://checkip.amazonaws.com/");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        return br.readLine();
    }

    private String postMyIp(String ip) throws IOException {
        HttpPost post = new HttpPost("https://it-prom.com/charts/rest/ip");

        String data = "{\"code\":\"" + this.code + "\", \"ip\":\"" + ip + "\"}";
        post.setEntity(new StringEntity(data, ContentType.APPLICATION_FORM_URLENCODED));

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
