package com.man.component.mq;

import com.man.common.enumerate.QueueEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TopicSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     * 用户JOJO微信支付行为
     */
    public void userPayAction(String message) {
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), "mall.pay.wechat.user.JOJO", message);
    }

    /**
     * 用户changxin微信登录行为
     */
    public void wechatAction(String message) {
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), "mall.login.wechat.user.changxin", message);
    }

    /**
     * 用户giao阿里支付行为
     */
    public void payAction(String message) {
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), "mall.pay.alibaba.user.giao", message);
    }
}
