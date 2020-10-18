package app.service;

import app.bean.ScreenPacket;
import app.service.entity.RedisScreenPacket;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisScreenProvider implements ScreenPacketProvider {

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
    public List<ScreenPacket> list(String code) {
        RBucket<Store> rStore = client.getBucket(code);
        Store store = rStore.get();
        if (store == null) {
            store = new Store();
        }
        List<ScreenPacket> res = new ArrayList<>();
        for (String key : store.keys()) {
            if(key.equals(code)){
                continue;
            }
            RBucket<RedisScreenPacket> bucket = client.getBucket(key);
            if (bucket.isExists()) {
                res.add(cast(bucket.get()));
            }
        }
        return res;
    }

    @Override
    public ScreenPacket get(String code, int position) {
        String id = defineId(code, position);
        RBucket<RedisScreenPacket> bucket = client.getBucket(id);
        if (bucket.isExists()) {
            return cast(bucket.get());
        }
        return null;
    }

    @Override
    public void put(ScreenPacket screenPacket) {
        RBucket<RedisScreenPacket> bucket = client.getBucket(screenPacket.getId());
        bucket.set(new RedisScreenPacket(screenPacket), 5L, TimeUnit.DAYS);
        addToStoreAndGetKey(screenPacket);
    }

    @Override
    public void remove(String code, int position) {
        RBucket<RedisScreenPacket> bucket = client.getBucket(defineId(code, position));
        if (bucket.isExists()) {
            bucket.delete();
        }
        removeFromStoreAndGetKey(code, position);
    }

    @Override
    public void clear(String code) {
        RKeys keys = client.getKeys();
        for (String key : keys.getKeysByPattern(code)) {
            client.getBinaryStream(key).deleteAsync();
        }
    }

    private String addToStoreAndGetKey(ScreenPacket screenPacket) {
        RBucket<Store> rStore = client.getBucket(screenPacket.getCode());
        String key = defineId(screenPacket.getCode(), screenPacket.getPosition());
        Store store = rStore.get();
        if (store == null) {
            store = new Store();
        }
        store.add(screenPacket.getPosition(), screenPacket.getId());
        rStore.set(store);
        return key;
    }

    private ScreenPacket cast(RedisScreenPacket obj) {
        return ScreenPacket.builder()
                .id(obj.getId())
                .code(obj.getCode())
                .position(obj.getPosition())
                .epoch(obj.getEpoch())
                .w(obj.getW())
                .h(obj.getH())
                .x(obj.getX())
                .y(obj.getY())
                .bytes(obj.getBytes())
                .build();
    }

    private String removeFromStoreAndGetKey(String code, int position) {
        RBucket<Store> rStore = client.getBucket(code);
        Store store = rStore.get();
        if (store != null) {
            store.remove(position);
            rStore.set(store);
        }
        return defineId(code, position);
    }

    private String defineId(String code, int position) {
        return code + "_" + position;
    }

    static class Store {
        public Map<Integer, String> map = new TreeMap<>();
        public Long epoch;

        public List<String> keys() {
            List<String> res = new ArrayList<>();
            for (Integer position : map.keySet()) {
                res.add(map.get(position));
            }
            return res;
        }

        public Store add(int position, String key) {
            map.put(position, key);
            return this;
        }

        public Store remove(int position) {
            map.remove(position);
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Store store = (Store) o;
            return Objects.equals(map, store.map) &&
                    Objects.equals(epoch, store.epoch);
        }

        @Override
        public int hashCode() {
            return Objects.hash(map, epoch);
        }
    }
}
