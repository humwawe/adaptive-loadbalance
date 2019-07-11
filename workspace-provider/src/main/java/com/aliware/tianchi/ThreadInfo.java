package com.aliware.tianchi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hum
 */
public class ThreadInfo {
    public static Map<Integer, Integer> getMap() {
        return map;
    }

    private static Map<Integer, Integer> map = new ConcurrentHashMap<>();

    public static void addProviderMaxThread(int key, int threads) {
        map.put(key, threads);
    }

}
