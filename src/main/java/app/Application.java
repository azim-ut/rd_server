package app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import picocli.CommandLine;


public class Application {

    @CommandLine.Option(names = {"-m", "--mode"}, defaultValue = "save", description = "'save': save screen, 'cast': cast screen by code")
    private static String mode = "save";

    public static void main(String[] args) {
        if (mode.equals("cast")) {
            System.out.println("Cast");
        } else {
            ApplicationContext context = new AnnotationConfigApplicationContext(SaveScreenApp.class);
            SaveScreenApp serverApp = context.getBean(SaveScreenApp.class);
            serverApp.start(args);
        }
    }
}
