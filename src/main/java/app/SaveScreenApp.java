package app;

import app.bean.ConnectionState;
import app.runnable.DefineHost;
import app.runnable.ScreenProcessor;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class SaveScreenApp {

    public void start(String[] args) {
        new Thread(new DefineHost(ConnectionState.builder()
                .port(Constants.PORT)
                .code(Constants.CODE)
                .build())).start();

        new Thread(new ScreenProcessor(Constants.PORT)).start();
    }
}
