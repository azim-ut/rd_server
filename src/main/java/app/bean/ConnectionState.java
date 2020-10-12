package app.bean;

import app.constants.HostAct;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ConnectionState {
    private HostAct act;
    private String code;
    private String ip;
    private int port;

    public synchronized void setIp(String val) {
        this.ip = val;
    }
}
