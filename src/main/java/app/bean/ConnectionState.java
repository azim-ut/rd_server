package app.bean;

import app.constants.ServerMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ConnectionState {
    private ServerMode act;
    private String code;
    private String ip;
    private int port;

    public synchronized void setIp(String val) {
        this.ip = val;
    }
}
