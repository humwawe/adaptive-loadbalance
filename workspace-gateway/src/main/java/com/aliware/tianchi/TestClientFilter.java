package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {
    private static final String START_TIME = "start_time";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            long startTime = System.currentTimeMillis();
            RpcInvocation rpcInvocation = (RpcInvocation) invocation;
            rpcInvocation.setAttachment(START_TIME, String.valueOf(startTime));

            Result result = invoker.invoke(invocation);
            return result;
        } catch (Exception e) {
            String key = invoker.getUrl().toIdentityString();
            UserLoadBalance.setLastTime(key, Long.MAX_VALUE);
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        long startTime = Long.parseLong(invocation.getAttachment(START_TIME));
        long endTime = System.currentTimeMillis();
        String key = invoker.getUrl().toIdentityString();
        UserLoadBalance.setLastTime(key, endTime - startTime);
        return result;
    }
}
