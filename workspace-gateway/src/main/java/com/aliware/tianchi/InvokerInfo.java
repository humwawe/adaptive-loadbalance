package com.aliware.tianchi;

import org.apache.dubbo.rpc.Invoker;

/**
 * @author hum
 */
public class InvokerInfo {
    private Invoker invoker;
    private Long lastTime;

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    public Invoker getInvoker() {

        return invoker;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public InvokerInfo(Invoker invoker, Long lastTime) {

        this.invoker = invoker;
        this.lastTime = lastTime;
    }
}
