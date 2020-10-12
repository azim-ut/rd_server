package app;

import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class BaseScreenApp {
    protected int port;
    protected String code;
    protected String mode;

    public void init(String[] args) {
        System.out.println("MODE: " + args[0] + " CODE: " + args[1] + " PORT: " + args[2]);
        String code = args[1];
        int port = Integer.parseInt(args[2]);
    }
}
