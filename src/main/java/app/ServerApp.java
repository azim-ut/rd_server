package app;

import app.bean.SocketState;
import app.service.ServerSocketProvider;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class ServerApp {
    private SocketState state;
    private ScreenSaveService screenSaveService;

    private ServerSocketProvider serverSocketProvider;

    public void start(String[] args) {

        try {
            serverSocketProvider = new ServerSocketProvider();

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
