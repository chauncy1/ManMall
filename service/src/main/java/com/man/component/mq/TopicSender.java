package com.man.component.mq;

import com.man.common.enums.QueueEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class TopicSender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RabbitmqConfirmCallback rabbitmqConfirmCallback;


    /**
     * @Author mchangx
     * @Description 初始化confirm机制 确认消息发送成功和失败的方法
     * @Date 14:06 2022/3/14
     **/
    @PostConstruct
    public void initConfirmAndCallback(){
        rabbitTemplate.setConfirmCallback(rabbitmqConfirmCallback);
        rabbitTemplate.setReturnCallback(rabbitmqConfirmCallback);
    }

    /**
     * 用户JOJO微信支付行为
     */
    public void userPayAction(String message) {
        rabbitTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), "mall.pay.wechat.user.JOJO", message);
    }

    /**
     * 用户changxin微信登录行为
     */
    public void wechatAction(String message) {
        rabbitTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), "mall.login.wechat.user.changxin", message);
    }

    /**
     * 用户giao阿里支付行为
     */
    public void payAction(String message) {
        rabbitTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), "mall.pay.alibaba.user.giao", message);
    }
}
