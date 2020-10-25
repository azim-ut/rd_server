package app;

import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class BaseScreenApp {
    protected int port;
    protected String mode;

    public void init(String[] args) {
        System.out.println("MODE: " + args[0] + " PORT: " + args[1]);
        this.mode = args[0];
        this.port = Integer.parseInt(args[1]);
    }
}
