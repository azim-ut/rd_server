package app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Application {

    public static void main(String[] args) {
        if (args.length == 2) {
            ApplicationContext context;
            context = new AnnotationConfigApplicationContext(ServerApp.class);
            context.getBean(ServerApp.class).start(args);
        }
    }
}
