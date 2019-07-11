package com.aliware.tianchi;

import org.apache.dubbo.rpc.listener.CallbackListener;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端监听器
 * 可选接口
 * 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 */
public class CallbackListenerImpl implements CallbackListener {

    @Override
    public void receiveServerMsg(String msg) {
        int len = msg.length();
        int index = msg.indexOf('=');
        if (index != -1) {
            int key = Integer.valueOf(msg.substring(1, index));
            int value = Integer.valueOf(msg.substring(index + 1, len - 1));
            System.out.println(key + "  " + value);
            UserLoadBalance.setMaxThread(key, value);
        }
    }

}
