package app;

import app.runnable.ScreenSaver;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class SaveScreenApp extends BaseScreenApp{

    public void start(String[] args) {
        init(args);
        new Thread(new ScreenSaver(code, port)).start();
    }
}
