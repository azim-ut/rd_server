package app;

import app.constants.Mode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Application {

    public static void main(String[] args) {
        if (args.length > 0) {
            ApplicationContext context;
            switch (Mode.valueOf(args[0])) {
                case SAVE:
                    context = new AnnotationConfigApplicationContext(SaveScreenApp.class);
                    context.getBean(SaveScreenApp.class).start(args);
                    break;
                case SHOW:
                    context = new AnnotationConfigApplicationContext(CastScreenApp.class);
                    context.getBean(CastScreenApp.class).start(args);
                    break;
            }
        }
    }
}
