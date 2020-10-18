package app.service.entity;

import app.bean.ScreenPacket;
import lombok.Getter;

@Getter
public class RedisScreenPacket {

    private String id;

    private String code;

    private Integer position;

    private Long epoch;

    private Integer x;

    private Integer y;

    private Integer w;

    private Integer h;

    private byte[] bytes;

    public RedisScreenPacket() {
    }

    public RedisScreenPacket(ScreenPacket screenPacket) {
        id = screenPacket.getId();
        code = screenPacket.getCode();
        position = screenPacket.getPosition();
        epoch = screenPacket.getEpoch();
        w = screenPacket.getW();
        h = screenPacket.getH();
        x = screenPacket.getX();
        y = screenPacket.getY();
        bytes = screenPacket.getBytes();
    }


}
