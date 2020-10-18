package app;

import app.runnable.SocketScreenCast;
import app.runnable.WebSocketScreenCast;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class ShowScreenApp extends BaseScreenApp {

    public void start(String[] args) {
        init(args);
//        new Thread(new SocketScreenCast(code, port)).start();
        new Thread(new WebSocketScreenCast(code, port)).start();
    }
}
