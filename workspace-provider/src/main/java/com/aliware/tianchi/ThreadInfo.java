package com.aliware.tianchi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hum
 */
public class ThreadInfo {
    public static Map<String, Integer> getMap() {
        return map;
    }

    private static Map<String, Integer> map = new ConcurrentHashMap<>();

    public static void addProviderMaxThread(String key, int threads) {
        map.put(key, threads);
    }

}
