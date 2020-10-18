package app.bean;

import app.constants.ServerMode;
import lombok.Builder;

@Builder
public class ConnectionPath {
    ServerMode act;
    String code;
    String ip;
    int port;
}
