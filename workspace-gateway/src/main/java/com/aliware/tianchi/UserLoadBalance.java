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
    private volatile static InvokerInfo[] invokerArray = new InvokerInfo[65536];

    //    private volatile static int totalWeight = 600;

    public static void addActive(int key) {
        InvokerInfo invokerInfo = invokerArray[key];
        if (invokerInfo != null) {
            invokerInfo.getCur().incrementAndGet();
        }
//        InvokerInfo invokerInfo = invokerMap.get(key);
//        if (invokerInfo != null) {
//            invokerInfo.getCur().incrementAndGet();
//        }

    }

    public static void subActive(int key) {
        InvokerInfo invokerInfo = invokerArray[key];
        if (invokerInfo != null) {
            invokerInfo.getCur().decrementAndGet();
        }
//        InvokerInfo invokerInfo = invokerMap.get(key);
//        if (invokerInfo != null) {
//            invokerInfo.getCur().decrementAndGet();
//        }
    }

    public synchronized static void setMaxThread(int key, int value) {
        invokerArray[key].setMax(value);
 //       invokerMap.get(key).setMax(value);
//        totalWeight += value - 200;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (invokerArray == null) {
            synchronized (UserLoadBalance.class) {
                if (invokerArray == null) {
                    System.out.println("init");
                    for (Invoker<T> invoker : invokers) {
                        System.out.println(invoker.getUrl().toIdentityString());
                        int key = invoker.getUrl().getPort();
                        invokerArray[key] = new InvokerInfo(invoker, new AtomicInteger());
//                        invokerMap.put(key, new InvokerInfo(invoker, new AtomicInteger()));
                    }
                }
            }
        }
//        Invoker retInvoker = getMinValue(invokerMap);

        Invoker retInvoker = getMinValue(invokerArray);

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

    private Invoker getMinValue(InvokerInfo[] invokerArray){
         InvokerInfo info1 = invokerArray[20870];
         InvokerInfo info2 = invokerArray[20880];
         InvokerInfo info3 = invokerArray[20880];

         int a1 = info1 != null ? info1.getMax() - info1.getCur().get() : Integer.MAX_VALUE;
         int a2 = info2 != null ? info2.getMax() - info2.getCur().get() : Integer.MAX_VALUE;
         int a3 = info3 != null ? info3.getMax() - info3.getCur().get() : Integer.MAX_VALUE;
         InvokerInfo min = null;
         if(a1 <=  a2 && a1 <= a3){
             min = info1;
         }else if(a2 <= a1 && a2 <= a3){
             min = info2;
         }else{
             min = info3;
         }
         if(min != null){
             return min.getInvoker();
         }else{
             return null;
         }
    }

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
