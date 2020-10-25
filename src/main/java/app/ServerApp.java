package app;

import app.bean.SocketState;
import app.runnable.UpdateSocketRunnable;
import app.runnable.SaveScreenRunnable;
import app.thread.SaveScreenThread;
import app.thread.ShowScreenThread;
import app.thread.UpdateSocketThread;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 */
@Component
public class ServerApp extends BaseScreenApp {
    private SocketState state;

    public void start(String[] args) {

        try {
            state = SocketState
                    .builder()
                    .port_save(Integer.parseInt(args[0]))
                    .port_show(Integer.parseInt(args[1]))
                    .build();

            new SaveScreenThread(state).start();
            new ShowScreenThread(state).start();
            new UpdateSocketThread(state).start();
            while(true){
                Thread.sleep(1000);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            System.out.println("DONE");
        }
    }
}
