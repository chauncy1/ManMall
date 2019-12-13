package com.man.service;

import lombok.extern.slf4j.Slf4j;
import com.man.common.constant.MqConstant;
import com.man.common.enumerate.QueueEnum;
import com.man.common.result.CommonResult;
import com.man.component.mq.TopicSender;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MqService {

    @Autowired
    TopicSender topicSender;

    @Autowired
    AmqpTemplate amqpTemplate;

    public CommonResult payAction(String message, String userName, String source) {
        log.info("process send pay message");
        String routeKey = MqConstant.MQ_TOPIC_ROUTEKEY_PREFIX + "pay." + source + ".user." + userName;
        log.info("routekey: " + routeKey);
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), routeKey, message);
        return CommonResult.success(message, "消息发送成功");
    }

    public CommonResult loginAction(String message, String userName, String source) {
        log.info("process send login message");
        String routeKey = MqConstant.MQ_TOPIC_ROUTEKEY_PREFIX + "login." + source + ".user." + userName;
        log.info("routekey: " + routeKey);
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), routeKey, message);
        return CommonResult.success(message, "消息发送成功");
    }

    public CommonResult casual() {
        log.info("process send casual message");
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), "mall.wechat", "mall.wechat");
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), "mall.pay", "mall.pay");
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_MALL_ACTION.getExchange(), "mall.wechat.pay", "mall.wechat.pay");
        return CommonResult.success(null, "消息发送成功");
    }

}
