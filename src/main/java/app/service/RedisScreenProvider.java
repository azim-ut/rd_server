package app.service;

import app.bean.ScreenPacket;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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

    private String addToStoreAndGetKey(String code, int position) {
        RBucket<Store> rStore = client.getBucket(code);
        String key = defineKey(code, position);
        Store store = rStore.get();
        if (store == null) {
            store = new Store();
        }
        store.add(position, key);
        rStore.set(store);
        return key;
    }

    private String removeFromStoreAndGetKey(String code, int position) {
        RBucket<Store> rStore = client.getBucket(code);
        Store store = rStore.get();
        if (store != null) {
            store.remove(position);
            rStore.set(store);
        }
        return defineKey(code, position);
    }

    private String defineKey(String code, int position){
        return code + "_" + position;
    }


    @Override
    public List<String> keys(String code) {
        RBucket<Store> rStore = client.getBucket(code);
        Store store = rStore.get();
        if (store != null) {
            return store.keys();
        }
        return Collections.emptyList();
    }

    @Override
    public byte[] get(String code, int position) {
        return client.getBinaryStream(code).get();
    }

    @Override
    public void put(String code, int position, byte[] bytes) {
        String key = addToStoreAndGetKey(code, position);
        RBinaryStream set = client.getBinaryStream(key);
        set.set(bytes, 5L, TimeUnit.DAYS);
    }

    @Override
    public void remove(String code, int position) {
        String key = removeFromStoreAndGetKey(code, position);
        RBinaryStream stream = client.getBinaryStream(key);
        stream.delete();
    }

    @Override
    public void clear(String code) {
        RKeys keys = client.getKeys();
        for (String key : keys.getKeysByPattern(code)) {
            client.getBinaryStream(key).deleteAsync();
        }
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
