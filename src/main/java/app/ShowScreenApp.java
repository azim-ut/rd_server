package app;

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
        try {
            Runnable runnable = new WebSocketScreenCast(port);
            Thread thread = new Thread(runnable, "WebSocketScreenCast");
            thread.start();
            thread.join();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            System.out.println("DONE");
        }
    }
}
