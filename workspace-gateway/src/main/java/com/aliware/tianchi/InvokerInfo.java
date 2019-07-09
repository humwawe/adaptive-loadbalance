package com.aliware.tianchi;

import org.apache.dubbo.rpc.Invoker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hum
 */
public class InvokerInfo {
    private Invoker invoker;
    private volatile int max;
    private AtomicInteger cur;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public AtomicInteger getCur() {
        return cur;
    }

    public void setCur(AtomicInteger cur) {
        this.cur = cur;
    }

    public Invoker getInvoker() {

        return invoker;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }


    public InvokerInfo(Invoker invoker, int max, AtomicInteger cur) {
        this.invoker = invoker;
        this.max = max;
        this.cur = cur;
    }

    @Override
    public String toString() {
        return "InvokerInfo{" +
                "invoker=" + invoker +
                ", max=" + max +
                ", cur=" + cur +
                '}';
    }
}
