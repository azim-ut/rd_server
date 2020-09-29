package app.bean;

import lombok.Builder;

@Builder
public class ConnectionPath {
    String code;
    String ip;
    int port;
}
