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

/**
 * @author daofeng.xjf
 * <p>
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {

    static Map<String, InvokerInfo> getInvokerMap() {
        return invokerMap;
    }

    private volatile static Map<String, InvokerInfo> invokerMap;

    public static synchronized void setLastTime(String key, long time) {
        InvokerInfo invokerInfo = invokerMap.get(key);
        invokerInfo.setLastTime(time);
        // invokerMap.put(key, invokerInfo);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (invokerMap == null) {
            synchronized (UserLoadBalance.class) {
                if (invokerMap == null) {
                    invokerMap = new ConcurrentHashMap<>(7);
                    for (Invoker<T> invoker : invokers) {
                        invokerMap.put(invoker.getUrl().toIdentityString(), new InvokerInfo(invoker, 0L));
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

    private Invoker getMinValue(Map<String, InvokerInfo> invokerMap) {
        Long tmp = Long.MAX_VALUE;
        Invoker tmpInvoker = null;
        for (InvokerInfo invokerInfo : invokerMap.values()) {
            Long invokerTime = invokerInfo.getLastTime();
            if (tmp > invokerTime) {
                tmp = invokerTime;
                tmpInvoker = invokerInfo.getInvoker();
            }
        }
        return tmpInvoker;
    }
}
