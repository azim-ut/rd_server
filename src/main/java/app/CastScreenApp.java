package app;

import app.runnable.ScreenCast;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class CastScreenApp extends BaseScreenApp {

    public void start(String[] args) {
        init(args);
        new Thread(new ScreenCast(code, port)).start();
    }
}
