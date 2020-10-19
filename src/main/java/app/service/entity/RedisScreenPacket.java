package app.service.entity;

import app.bean.ScreenPacket;
import lombok.Getter;

@Getter
public class RedisScreenPacket {

    private String id;

    private byte[] bytes;

    public RedisScreenPacket() {
    }

    public RedisScreenPacket(ScreenPacket screenPacket) {
        id = screenPacket.getId();
        bytes = screenPacket.getBytes();
    }


}
