package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcStatus;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

/**
 * @author daofeng.xjf
 * <p>
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {


    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {

        int leastActive = RpcStatus.getStatus(invokers.get(0).getUrl(), invocation.getMethodName()).getActive();
        int leastActiveIdx = 0;
        for (int i = 1; i < invokers.size() - 1; i++) {
            int active = RpcStatus.getStatus(invokers.get(i).getUrl(), invocation.getMethodName()).getActive();
            if (active < leastActive) {
                leastActive = active;
                leastActiveIdx = i;
            }
        }
        return invokers.get(leastActiveIdx);

    }

}
