package app;

import app.constants.ServerMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Application {

    public static void main(String[] args) {
        if (args.length > 0) {
            ApplicationContext context;
            switch (ServerMode.valueOf(args[0])) {
                case SAVE:
                    context = new AnnotationConfigApplicationContext(SaveScreenApp.class);
                    context.getBean(SaveScreenApp.class).start(args);
                    break;
                case SHOW:
                    context = new AnnotationConfigApplicationContext(ShowScreenApp.class);
                    context.getBean(ShowScreenApp.class).start(args);
                    break;
            }
        }
    }
}
