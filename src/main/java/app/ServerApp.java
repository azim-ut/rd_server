package app;

import app.bean.SocketState;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class ServerApp extends BaseScreenApp {
    private SocketState state;
    private ScreenSaveService screenSaveService;

    public void start(String[] args) {

        try {
            state = SocketState
                    .builder()
                    .port_save(Integer.parseInt(args[0]))
                    .port_show(Integer.parseInt(args[1]))
                    .build();

            screenSaveService = new ScreenSaveService(state);

            screenSaveService.start();

            while (true) {
                if (screenSaveService.isStopped()) {
                    screenSaveService.start();
                }
                Thread.sleep(1000);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            System.out.println("DONE");
        }
    }
}
