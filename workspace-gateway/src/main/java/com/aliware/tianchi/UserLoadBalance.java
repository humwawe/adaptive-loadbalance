package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author daofeng.xjf
 * <p>
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {


    private volatile static Map<Integer, InvokerInfo> invokerMap = new ConcurrentHashMap<>(5);
//    private volatile static int totalWeight = 600;

    public static void addActive(int key) {
        InvokerInfo invokerInfo = invokerMap.get(key);
        if (invokerInfo != null) {
            invokerInfo.getCur().incrementAndGet();
        }

    }

    public static void subActive(int key) {
        InvokerInfo invokerInfo = invokerMap.get(key);
        if (invokerInfo != null) {
            invokerInfo.getCur().decrementAndGet();
        }
    }

    public synchronized static void setMaxThread(int key, int value) {
        invokerMap.get(key).setMax(value);
//        totalWeight += value - 200;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (invokerMap.isEmpty()) {
            synchronized (UserLoadBalance.class) {
                if (invokerMap.isEmpty()) {
                    System.out.println("init");
                    for (Invoker<T> invoker : invokers) {
                        int key = invoker.getUrl().getPort();
                        invokerMap.put(key, new InvokerInfo(invoker, new AtomicInteger()));
                    }
                }
            }
        }
        Invoker retInvoker = getMinValue(invokerMap);
        if (retInvoker != null) {
            return (Invoker<T>) retInvoker;
        }
        return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));

    }

//    private Invoker getMinValue(Map<Integer, InvokerInfo> invokerMap) {
//        System.out.println(totalWeight);
//        int offset = ThreadLocalRandom.current().nextInt(totalWeight);
//        for (InvokerInfo invokerInfo : invokerMap.values()) {
//            offset -= invokerInfo.getMax();
//            if (offset < 0) {
//                return invokerInfo.getInvoker();
//            }
//        }
//        return null;
//    }

    private Invoker getMinValue(Map<Integer, InvokerInfo> invokerMap) {
        int tmp = 0;
        Invoker tmpInvoker = null;
        for (InvokerInfo invokerInfo : invokerMap.values()) {
            int available = invokerInfo.getMax() - invokerInfo.getCur().get();
            if (tmp < available) {
                tmp = available;
                tmpInvoker = invokerInfo.getInvoker();
            }
        }
        return tmpInvoker;
    }

}
