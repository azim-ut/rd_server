package app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Application {
    public static void main(String[] args) {
        System.out.println("1");
        ApplicationContext context = new AnnotationConfigApplicationContext(ServerApp.class);
        ServerApp serverApp = context.getBean(ServerApp.class);
        serverApp.start(args);
    }
}
