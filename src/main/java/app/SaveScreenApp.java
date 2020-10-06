package app;

import app.runnable.ScreenProcessor;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class SaveScreenApp {

    public void start(String[] args) {
        new Thread(new ScreenProcessor(Constants.PORT)).start();
    }
}
