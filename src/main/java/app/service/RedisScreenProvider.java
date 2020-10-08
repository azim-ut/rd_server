package app.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisScreenProvider implements ScreenProvider {

    RedissonClient client;

    public RedisScreenProvider() {
        try {
            InputStream is = RedissonClient.class.getClassLoader().getResourceAsStream("resisson.yaml");
            Config config = Config.fromYAML(is);
            client = Redisson.create(config);
        } catch (IOException e) {
            log.error("Can't read redisson yaml properties.");
        }
    }

    @Override
    public byte[] get(String key) {
        RBinaryStream bytes = client.getBinaryStream(key);
        return bytes.get();
    }

    @Override
    public void put(String key, byte[] bytes) {
        RBinaryStream stream = client.getBinaryStream(key);
        stream.set(bytes, 1L, TimeUnit.SECONDS);
    }

    @Override
    public void remove(String key) {
        RBinaryStream stream = client.getBinaryStream(key);
        stream.delete();
    }

    @Override
    public void clear(String pattern) {
        RKeys keys = client.getKeys();
        for (String key : keys.getKeysByPattern(pattern)) {
            client.getBinaryStream(key).deleteAsync();
        }
    }
}
