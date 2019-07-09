package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcStatus;
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


    private volatile static Map<String, InvokerInfo> invokerMap = new ConcurrentHashMap<>(7);

    public static void setLastTime(String key, long time) {
    }

    public static void addActive(String key) {
        InvokerInfo invokerInfo = invokerMap.get(key);
        if (invokerInfo != null) {
            invokerInfo.getCur().incrementAndGet();
        }

    }

    public static void subActive(String key) {
        InvokerInfo invokerInfo = invokerMap.get(key);
        if (invokerInfo != null) {
            invokerInfo.getCur().decrementAndGet();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (invokerMap.isEmpty()) {
            synchronized (UserLoadBalance.class) {
                if (invokerMap.isEmpty()) {
                    System.out.println("init");
                    for (Invoker<T> invoker : invokers) {
                        System.out.println(invoker.getUrl().toIdentityString());
                        String key = invoker.getUrl().toIdentityString();
                        if (key.contains("large") || key.contains("20890")) {
                            invokerMap.put(key, new InvokerInfo(invoker, 620, new AtomicInteger()));
                        } else if (key.contains("medium") || key.contains("20870")) {
                            invokerMap.put(key, new InvokerInfo(invoker, 420, new AtomicInteger()));
                        } else if (key.contains("small") || key.contains("20880")) {
                            invokerMap.put(key, new InvokerInfo(invoker, 170, new AtomicInteger()));
                        }
                    }
                    System.out.println(invokerMap.values());
                }
            }
        }
        Invoker retInvoker = getMinValue(invokerMap);
        if (retInvoker != null) {
            return (Invoker<T>) retInvoker;
        }
        return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));


//        Invoker<T> tInvoker = invokers.get());
//        Invoker<T> tInvoker = invokers.get(0);
//        System.out.println(RpcStatus.getStatus(tInvoker.getUrl(), invocation.getMethodName()).getActive());
//        RpcContext.getContext().getUrls();
//        return tInvoker;

    }

    private Invoker getMinValue(Map<String, InvokerInfo> invokerMap) {
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
